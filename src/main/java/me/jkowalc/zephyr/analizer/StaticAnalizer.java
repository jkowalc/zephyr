package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.BuiltinFunctionManager;
import me.jkowalc.zephyr.domain.CustomFunctionRepresentation;
import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.domain.type.StructType;
import me.jkowalc.zephyr.domain.type.Type;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.*;
import me.jkowalc.zephyr.exception.analizer.*;
import me.jkowalc.zephyr.exception.runtime.ReturnSignal;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.EphemeralValue;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticAnalizer implements ASTVisitor {
    private Map<String, FunctionRepresentation> functions;

    private ScopedContext<Type> context;

    private TypeChecker typeChecker;

    EphemeralValue<Type> returnType;

    @Override
    public void visit(Program program) throws ZephyrException {
        this.functions = new HashMap<>();
        functions.putAll(BuiltinFunctionManager.BUILTIN_FUNCTIONS);
        for(Map.Entry<String, FunctionDefinition> function : program.getFunctions().entrySet()) {
            if(BuiltinFunctionManager.BUILTIN_FUNCTIONS.containsKey(function.getKey())) {
                throw new FunctionAlreadyDefinedException(function.getKey(), true, function.getValue().getStartPosition());
            }
            if(functions.containsKey(function.getKey())) {
                throw new FunctionAlreadyDefinedException(function.getKey(), false, function.getValue().getStartPosition());
            }
            functions.put(function.getKey(), new CustomFunctionRepresentation(function.getValue()));
        }
        this.typeChecker = new TypeChecker(program.getTypes());
        this.context = new ScopedContext<>();
        this.returnType = new EphemeralValue<>(null);
        for(FunctionDefinition functionDefinition : program.getFunctions().values()) {
            functionDefinition.accept(this);
            this.returnType.get();
        }
    }

    @Override
    public void visit(FunctionDefinition functionDefinition) throws ZephyrException {
        context.createScope();
        if(functionDefinition.getName().equals("main")) {
            String mainReturnType = functionDefinition.getReturnType();
            if(!(mainReturnType == null || mainReturnType.equals("int"))) {
                throw new AnalizerException("Main function must return int or void", functionDefinition.getStartPosition());
            }
        }
        for(VariableDefinition parameter : functionDefinition.getParameters()) {
            parameter.accept(this);
        }
        Type returnType = null;
        try {
            functionDefinition.getBody().accept(this);
        } catch(ReturnSignal ignored) {
            returnType = this.returnType.get();
        }
        if(returnType == null) returnType = new Type(TypeCategory.VOID, false);
        TypeCategory expectedReturnTypeCategory = TypeCategory.fromString(functionDefinition.getReturnType());
        // TODO: Change into full return type checking
        if(!returnType.getCategory().equals(expectedReturnTypeCategory)) {
            throw new AnalizerException("Return type mismatch", functionDefinition.getStartPosition());
        }
        context.rollback();
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        context.createLocalScope();
        try {
            for(Statement statement : statementBlock.getStatements()) {
                if(statement instanceof VariableDefinition variableDefinition && variableDefinition.getDefaultValue() == null) {
                    throw new VariableNotInitializedException(variableDefinition.getName(), variableDefinition.getStartPosition());
                }
                statement.accept(this);
            }
        } finally {
            context.rollback();
        }
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        FunctionRepresentation representation = functions.get(functionCall.getName());
        if(representation == null){
            throw new FunctionNotDefinedException(functionCall.getName(), functionCall.getStartPosition());
        }
        List<Type> parameterTypes = representation.getParameterTypes();
        int argumentCount = functionCall.getArguments().size();
        if(argumentCount != parameterTypes.size()) {
            throw new InvalidArgumentCountException(functionCall.getName(), parameterTypes.size(), argumentCount, functionCall.getStartPosition());
        }
        for(int i = 0; i < argumentCount; i++) {
            functionCall.getArguments().get(i).accept(this);
            Type argumentType = returnType.get();
            Type parameterType = parameterTypes.get(i);
            if(!argumentType.getCategory().equals(parameterType.getCategory())) {
                throw new AnalizerException("Argument type mismatch", functionCall.getStartPosition());
            }
        }
        returnType.set(representation.getReturnType());
    }

    @Override
    public void visit(VariableReference variableReference) throws AnalizerException {
        try {
            returnType.set(context.get(variableReference.getName()));
        } catch(VariableNotDefinedException e) {
            throw new VariableNotDefinedAnalizerException(variableReference.getName(), variableReference.getStartPosition());
        }
    }

    @Override
    public void visit(DotExpression dotExpression) throws ZephyrException {
        dotExpression.getValue().accept(this);
        try {
            Type type = typeChecker.getFieldType(returnType.get(), dotExpression.getField());
            if(type == null) {
                throw new InvalidFieldAccessException((Assignable) dotExpression.getValue(), dotExpression.getField(), dotExpression.getStartPosition());
            }
            returnType.set(type);
        } catch(TypeNotDefinedException e) {
            throw new TypeNotDefinedAnalizerException(e.getName(), dotExpression.getStartPosition());
        }
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
    public void visit(NegationExpression negationExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {
        returnType.set(new Type(TypeCategory.BOOL));
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        returnType.set(new Type(TypeCategory.BOOL));
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        returnType.set(new Type(TypeCategory.INT));
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        returnType.set(new Type(TypeCategory.FLOAT));
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        returnType.set(new Type(TypeCategory.STRING));
    }

    @Override
    public void visit(StructLiteral structLiteral) throws ZephyrException {
        Map<String, Type> fields = new SimpleMap<>();
        for(Map.Entry<String, Literal> entry : structLiteral.getFields().entrySet()) {
            entry.getValue().accept(this);
            fields.put(entry.getKey(), returnType.get());
        }
        returnType.set(new StructType(fields));
    }

    @Override
    public void visit(StructDefinition structDefinition) {
        // TODO: implement checking struct definition
    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {
        // TODO: implement checking struct definition member
    }

    @Override
    public void visit(UnionDefinition unionDefinition) {
        // TODO: implement checking union definition
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) throws ZephyrException {
        assignmentStatement.getTarget().accept(this);
        Type targetType = returnType.get();
        if(!targetType.isMutable())
            throw new ImmutableVariableException(assignmentStatement.getTarget(), assignmentStatement.getStartPosition());
        assignmentStatement.getValue().accept(this);
        Type valueType = returnType.get();
        // TODO: Change into full assignment type checking
        if(!targetType.getCategory().equals(valueType.getCategory())) {
            throw new AnalizerException("Assignment type mismatch", assignmentStatement.getStartPosition());
        }
    }

    @Override
    public void visit(IfStatement ifStatement) throws ZephyrException {
        ifStatement.getCondition().accept(this);
        Type conditionType = returnType.get();
        // TODO: Change into full condition type checking
        if(!conditionType.getCategory().equals(TypeCategory.BOOL)) {
            throw new AnalizerException("Condition must be boolean", ifStatement.getStartPosition());
        }
        ifStatement.getBody().accept(this);
    }

    @Override
    public void visit(MatchStatement matchStatement) throws ZephyrException {
        // check if the expression can be evaluated, ignore the result
        matchStatement.getExpression().accept(this);
        this.returnType.get();
        for(MatchCase matchCase : matchStatement.getCases()) {
            matchCase.accept(this);
        }
    }

    @Override
    public void visit(MatchCase matchCase) throws ZephyrException {
        context.createLocalScope();
        try {
            context.add(matchCase.getVariableName(), new Type(TypeCategory.fromString(matchCase.getPattern()), false));
        } catch(VariableAlreadyDefinedException ignored) {
            throw new ZephyrInternalException();
        }
        matchCase.getBody().accept(this);
        context.rollback();
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        if(returnStatement.getExpression() == null) {
            returnType.set(new Type(TypeCategory.VOID, false));
            throw new ReturnSignal();
        }
        returnStatement.getExpression().accept(this);
        throw new ReturnSignal();
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        TypeCategory variableCategory = TypeCategory.fromString(variableDefinition.getTypeName());
        Type variableType = new Type(variableCategory, variableDefinition.isMutable(), variableDefinition.isReference());
        if(variableDefinition.getDefaultValue() != null) {
            variableDefinition.getDefaultValue().accept(this);
            Type defaultValueType = returnType.get();
            // TODO: Change into full variable type checking
            if(!variableType.getCategory().equals(defaultValueType.getCategory())) {
                throw new AnalizerException("Variable type mismatch", variableDefinition.getStartPosition());
            }
        }
        try {
            context.add(variableDefinition.getName(), variableType);
        } catch(VariableAlreadyDefinedException e) {
            throw new VariableAlreadyDefinedAnalizerException(variableDefinition.getName(), variableDefinition.getStartPosition());
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) throws ZephyrException {
        whileStatement.getCondition().accept(this);
        Type conditionType = returnType.get();
        // TODO: Change into full condition type checking
        if(!conditionType.getCategory().equals(TypeCategory.BOOL)) {
            throw new AnalizerException("Condition must be boolean", whileStatement.getStartPosition());
        }
        whileStatement.getBody().accept(this);
    }
}
