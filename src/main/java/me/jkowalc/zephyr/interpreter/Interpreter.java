package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.BuiltinFunctionManager;
import me.jkowalc.zephyr.analizer.ScopedContext;
import me.jkowalc.zephyr.analizer.StaticAnalizer;
import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.Reference;
import me.jkowalc.zephyr.domain.runtime.value.*;
import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.*;
import me.jkowalc.zephyr.exception.analizer.TypeNotDefinedException;
import me.jkowalc.zephyr.exception.runtime.ConversionException;
import me.jkowalc.zephyr.exception.runtime.ReturnSignal;
import me.jkowalc.zephyr.exception.scope.VariableAlreadyDefinedScopeException;
import me.jkowalc.zephyr.exception.scope.VariableNotDefinedScopeException;
import me.jkowalc.zephyr.exception.type.ConversionTypeException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.EphemeralValue;
import me.jkowalc.zephyr.util.SimpleMap;
import me.jkowalc.zephyr.util.TextPosition;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interpreter implements ASTVisitor {
    private Map<String, FunctionRepresentation> functions;

    private Map<String, BareStaticType> types;

    private final OutputStreamWriter outputStream;

    private EphemeralValue<Value> returnValue;

    private EphemeralValue<List<Value>> arguments;

    private ScopedContext<Reference> context;

    private BuiltinFunctionManager builtinFunctionManager;

    public Interpreter(OutputStreamWriter outputStream) {
        this.outputStream = outputStream;
    }

    private Value convertAndCatch(Value value, TypeCategory target) {
        try {
            return TypeConverter.convert(value, target);
        } catch (ConversionTypeException e) {
            throw new ZephyrInternalException();
        }
    }

    private BareStaticType getBareType(String typeName, TextPosition position) throws ZephyrException {
        if(typeName == null)
            return new BareStaticType(TypeCategory.VOID);
        BareStaticType type = types.get(typeName);
        if(type == null)
            throw new TypeNotDefinedException(typeName, position);
        return type;
    }

    private void executeFunction(FunctionRepresentation functionRepresentation) throws ConversionTypeException, ZephyrException {
        if(functionRepresentation.builtIn()) {
            Value returnValue = this.builtinFunctionManager.execute(functionRepresentation.name(), arguments.get());
            this.returnValue.set(returnValue);
        } else {
            functionRepresentation.definition().accept(this);
        }
    }
    @Override
    public void visit(Program program) throws ZephyrException {
        StaticAnalizer analizer = new StaticAnalizer();
        program.accept(analizer);
        this.functions = analizer.getFunctions();
        this.types = analizer.getTypes();
        this.context = new ScopedContext<>();
        this.returnValue = new EphemeralValue<>(null);
        this.arguments = new EphemeralValue<>(List.of());
        this.builtinFunctionManager = new BuiltinFunctionManager(outputStream);
        try {
            executeFunction(functions.get("main"));
        } catch (ConversionTypeException e) {
            // ConversionException can only occur if the main function is a custom function, which is not possible
            throw new ZephyrInternalException();
        }
    }

    @Override
    public void visit(FunctionDefinition functionDefinition) throws ZephyrException {
        context.createScope();
        List<Value> arguments = this.arguments.get();
        for(int i = 0; i < functionDefinition.getParameters().size(); i++) {
            VariableDefinition parameter = functionDefinition.getParameters().get(i);
            try {
                Value argument = arguments.get(i);
                Reference reference = argument instanceof Reference var ? var : new Reference(argument);
                context.add(parameter.getName(), reference);
            } catch (VariableAlreadyDefinedScopeException e) {
                throw new ZephyrInternalException();
            }
        }
        functionDefinition.getBody().accept(this);
        context.rollback();
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        context.createLocalScope();
        for(Statement statement : statementBlock.getStatements()) {
            statement.accept(this);
        }
        context.rollback();
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        FunctionRepresentation functionRepresentation = functions.get(functionCall.getName());
        List<Value> args = new ArrayList<>();
        for(int i = 0; i < functionCall.getArguments().size(); i++) {
            functionCall.getArguments().get(i).accept(this);
            Value value = returnValue.get();
            StaticType requestedtype = functionRepresentation.parameterTypes().get(i);
            if(requestedtype.isReference()) {
                args.add(value);
                continue;
            }
            Value convertedValue = convertAndCatch(value, requestedtype.getBareStaticType().getCategory());
            convertedValue = convertedValue.deepCopy();
            args.add(convertedValue);
        }
        this.arguments.set(args);
        try {
            executeFunction(functionRepresentation);
        } catch (ConversionTypeException e) {
            // ConversionException can occur if the builtin 'to_string', 'to_int', etc. function are called with the wrong argument
            throw new ConversionException(e.getValue(), e.getTarget(), functionCall.getStartPosition());
        } catch (ReturnSignal ignored) {}
    }

    @Override
    public void visit(VariableReference variableReference) {
        try {
            returnValue.set(context.get(variableReference.getName()));
        } catch (VariableNotDefinedScopeException e) {
            // every variable reference should already be checked by the static analizer
            throw new ZephyrInternalException();
        }
    }

    @Override
    public void visit(DotExpression dotExpression) throws ZephyrException {
        dotExpression.getValue().accept(this);
        StructValue value = (StructValue) returnValue.get().getValue();
        returnValue.set(value.getField(dotExpression.getField()));
    }

    @Override
    public void visit(AddExpression addExpression) {

    }

    @Override
    public void visit(AndExpression andExpression) {

    }

    @Override
    public void visit(DivideExpression divideExpression) {

    }

    @Override
    public void visit(EqualExpression equalExpression) {

    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) {

    }

    @Override
    public void visit(GreaterExpression greaterExpression) {

    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) {

    }

    @Override
    public void visit(LessExpression lessExpression) {

    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) {

    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) {

    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(SubtractExpression subtractExpression) {

    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        returnValue.set(new BooleanValue(booleanLiteral.getValue()));
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        returnValue.set(new IntegerValue(integerLiteral.getValue()));
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        returnValue.set(new FloatValue(floatLiteral.getValue()));
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        returnValue.set(new StringValue(stringLiteral.getValue()));
    }

    @Override
    public void visit(StructLiteral structLiteral) throws ZephyrException {
        Map<String, Value> fields = new SimpleMap<>();
        for(Map.Entry<String, Literal> entry : structLiteral.getFields().entrySet()) {
            entry.getValue().accept(this);
            fields.put(entry.getKey(), returnValue.get());
        }
        returnValue.set(new StructValue(fields));
    }

    @Override
    public void visit(NegationExpression negationExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {

    }

    @Override
    public void visit(StructDefinition structDefinition) {
        throw new UnsupportedOperationException("Visiting struct definition is not supported");
    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {
        throw new UnsupportedOperationException("Visiting struct definition member is not supported");
    }

    @Override
    public void visit(UnionDefinition unionDefinition) {
        throw new UnsupportedOperationException("Visiting union definition is not supported");
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) throws ZephyrException {
        if(assignmentStatement.getTarget() instanceof DotExpression dotExpression) {
            dotExpression.getValue().accept(this);
            StructValue target = (StructValue) returnValue.get().getValue();
            assignmentStatement.getValue().accept(this);
            target.setField(dotExpression.getField(), returnValue.get());
        } else if(assignmentStatement.getTarget() instanceof VariableReference variableReference) {
            assignmentStatement.getValue().accept(this);
            Value value = returnValue.get();
            try {
                context.get(variableReference.getName()).setValue(value.getValue().deepCopy());
            } catch (VariableNotDefinedScopeException e) {
                // every variable reference should already be checked by the static analizer
                throw new ZephyrInternalException();
            }
        } else {
            throw new ZephyrInternalException();
        }
    }

    @Override
    public void visit(IfStatement ifStatement) throws ZephyrException {
        ifStatement.getCondition().accept(this);
        BooleanValue condition = (BooleanValue) returnValue.get().getValue();
        if(condition.value()) {
            ifStatement.getBody().accept(this);
        } else {
            if(ifStatement.getElseBlock() != null) ifStatement.getElseBlock().accept(this);
        }
    }

    @Override
    public void visit(MatchStatement matchStatement) {

    }

    @Override
    public void visit(MatchCase matchCase) {

    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        if(returnStatement.getExpression() != null) {
            returnStatement.getExpression().accept(this);
            // propagate the return value
        }
        throw new ReturnSignal();
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        variableDefinition.getDefaultValue().accept(this);
        Value defaultValue = returnValue.get();
        BareStaticType type = getBareType(variableDefinition.getTypeName(), variableDefinition.getStartPosition());
        try {
            context.add(variableDefinition.getName(), new Reference(TypeConverter.convert(defaultValue, type.getCategory())));
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), variableDefinition.getDefaultValue().getStartPosition());
        } catch (VariableAlreadyDefinedScopeException e) {
            // every variable definition should already be checked by the static analizer
            throw new ZephyrInternalException();
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) throws ZephyrException {
        whileStatement.getCondition().accept(this);
        BooleanValue condition = (BooleanValue) returnValue.get().getValue();
        while(condition.value()) {
            whileStatement.getBody().accept(this);
            whileStatement.getCondition().accept(this);
            condition = (BooleanValue) returnValue.get().getValue();
        }
    }
}
