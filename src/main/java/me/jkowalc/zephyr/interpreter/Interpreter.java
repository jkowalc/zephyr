package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.BuiltinFunctionManager;
import me.jkowalc.zephyr.analyzer.ScopedContext;
import me.jkowalc.zephyr.analyzer.StaticAnalyzer;
import me.jkowalc.zephyr.analyzer.TypeChecker;
import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
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
import me.jkowalc.zephyr.exception.runtime.DivideByZeroException;
import me.jkowalc.zephyr.exception.runtime.ReturnSignal;
import me.jkowalc.zephyr.exception.scope.VariableNotDefinedScopeException;
import me.jkowalc.zephyr.exception.type.ConversionTypeException;
import me.jkowalc.zephyr.input.TextPrinter;
import me.jkowalc.zephyr.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class Interpreter implements ASTVisitor {
    private final Map<String, FunctionRepresentation> functions;

    private final StaticAnalyzer analizer;

    private final Map<String, BareStaticType> types;

    private final EphemeralValue<Value> returnValue;

    private final EphemeralValue<List<Value>> arguments;

    private final ScopedContext<Reference> context;

    private final BuiltinFunctionManager builtinFunctionManager;

    private Value matchValue = null;

    public Interpreter(Program program, TextPrinter outputStream) throws ZephyrException {
        this.analizer = new StaticAnalyzer();
        program.accept(analizer);
        this.functions = analizer.getFunctions();
        this.types = analizer.getTypes();
        this.context = new ScopedContext<>();
        this.returnValue = new EphemeralValue<>(null);
        this.arguments = new EphemeralValue<>(List.of());
        this.builtinFunctionManager = new BuiltinFunctionManager(outputStream);
    }

    private BareStaticType getBareType(String typeName, TextPosition position) throws ZephyrException {
        if (typeName == null)
            return new BareStaticType(TypeCategory.VOID);
        BareStaticType type = types.get(typeName);
        if (type == null)
            throw new TypeNotDefinedException(typeName, position);
        return type;
    }

    private void executeFunction(FunctionRepresentation functionRepresentation) throws ZephyrException, ConversionTypeException {
        Value returnValue;
        if (functionRepresentation.builtIn()) {
            returnValue = this.builtinFunctionManager.execute(functionRepresentation.name(), arguments.get());
        } else {
            try {
                functionRepresentation.definition().accept(this);
            } catch (ReturnSignal ignored) {
            }
            returnValue = this.returnValue.get();
            returnValue = (returnValue == null ? new VoidValue() : returnValue);
        }
        Value convertedReturnValue = TypeConverter.convert(returnValue, functionRepresentation.returnType().getBareStaticType().getCategory());
        this.returnValue.set(convertedReturnValue);
    }

    public Value executeFunction(String name, List<Value> arguments) throws ZephyrException {
        FunctionRepresentation functionRepresentation = functions.get(name);
        this.arguments.set(arguments);
        try {
            executeFunction(functionRepresentation);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), null);
        }
        return returnValue.get();
    }

    public Value evaluateExpression(Expression expression) throws ZephyrException {
        expression.accept(analizer);
        expression.accept(this);
        return returnValue.get();
    }

    public int executeMain() throws ZephyrException {
        Value returnValue = executeFunction("main", List.of());
        if (returnValue instanceof VoidValue)
            return 0;
        IntegerValue exitCodeValue = (IntegerValue) returnValue;
        return exitCodeValue.value();
    }

    private Value eval(Node node) throws ZephyrException {
        node.accept(this);
        return returnValue.get();
    }
    private Value eval(Expression expression) throws ZephyrException {
        expression.accept(this);
        return returnValue.get();
    }
    private BooleanValue evalBool(Node node) throws ZephyrException {
        node.accept(this);
        return (BooleanValue) returnValue.get().getValue();
    }
    private BooleanValue evalBool(Expression expression) throws ZephyrException {
        expression.accept(this);
        return (BooleanValue) returnValue.get().getValue();
    }
    private StructValue evalStruct(Node node) throws ZephyrException {
        node.accept(this);
        return (StructValue) returnValue.get().getValue();
    }
    private StructValue evalStruct(Expression expression) throws ZephyrException {
        expression.accept(this);
        return (StructValue) returnValue.get().getValue();
    }
    @Override
    public void visit(FunctionDefinition functionDefinition) throws ZephyrException {
        context.createScope();
        List<Value> arguments = this.arguments.get();
        for (int i = 0; i < functionDefinition.getParameters().size(); i++) {
            VariableDefinition parameter = functionDefinition.getParameters().get(i);
            Value argument = arguments.get(i);
            Reference reference = argument instanceof Reference var ? var : new Reference(argument);
            context.set(parameter.getName(), reference);
        }
        try {
            functionDefinition.getBody().accept(this);
        } finally {
            context.rollback();
        }
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        context.createLocalScope();
        try {
            for (Statement statement : statementBlock.getStatements()) {
                statement.accept(this);
            }
        } finally {
            context.rollback();
        }
    }

    @Override
    public void visit(Program program) throws ZephyrException {
        throw new UnsupportedOperationException("Visiting program is not supported. Use executeMain() instead.");
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        FunctionRepresentation functionRepresentation = functions.get(functionCall.getName());
        List<Value> args = new ArrayList<>();
        for (int i = 0; i < functionCall.getArguments().size(); i++) {
            Expression argument = functionCall.getArguments().get(i);
            Value argumentValue = eval(argument);
            StaticType requestedtype = functionRepresentation.parameterTypes().get(i);
            if (requestedtype.isReference()) {
                args.add(argumentValue);
                continue;
            }
            try {
                Value convertedValue = TypeConverter.convert(argumentValue, requestedtype.getBareStaticType().getCategory());
                convertedValue = convertedValue.deepCopy();
                args.add(convertedValue);
            } catch (ConversionTypeException e) {
                throw new ConversionException(e.getValue(), e.getTarget(), argument.getStartPosition());
            }
        }
        this.arguments.set(args);
        try {
            executeFunction(functionRepresentation);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), functionCall.getStartPosition());
        }
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
        StructValue value = evalStruct(dotExpression.getValue());
        returnValue.set(value.getField(dotExpression.getField()));
    }

    @Override
    public void visit(AddExpression addExpression) throws ZephyrException {
        Value leftValue = eval(addExpression.getLeft());
        Value rightValue = eval(addExpression.getRight());
        TypeCategory leftCategory = leftValue.getValue().getCategory();
        TypeCategory rightCategory = rightValue.getValue().getCategory();
        if (leftCategory.equals(TypeCategory.INT) && rightCategory.equals(TypeCategory.INT)) {
            IntegerValue leftInt = (IntegerValue) leftValue.getValue();
            IntegerValue rightInt = (IntegerValue) rightValue.getValue();
            returnValue.set(new IntegerValue(leftInt.value() + rightInt.value()));
        } else if (Stream.of(leftCategory, rightCategory).allMatch(c -> List.of(TypeCategory.INT, TypeCategory.FLOAT).contains(c))) {
            Pair<FloatValue, FloatValue> floats = convertToFloats(leftValue, rightValue, addExpression.getStartPosition());
            returnValue.set(new FloatValue(floats.first().value() + floats.second().value()));
        } else {
            try {
                leftValue = TypeConverter.convert(leftValue, TypeCategory.STRING);
            } catch (ConversionTypeException e) {
                throw new ConversionException(e.getValue(), e.getTarget(), addExpression.getLeft().getStartPosition());
            }
            try {
                rightValue = TypeConverter.convert(rightValue, TypeCategory.STRING);
            } catch (ConversionTypeException e) {
                throw new ConversionException(e.getValue(), e.getTarget(), addExpression.getRight().getStartPosition());
            }
            StringValue leftStringValue = (StringValue) leftValue.getValue();
            StringValue rightStringValue = (StringValue) rightValue.getValue();
            returnValue.set(new StringValue(leftStringValue.value() + rightStringValue.value()));
        }
    }

    private void booleanOperation(Expression left, Expression right, BiFunction<Boolean, Boolean, Boolean> function) throws ZephyrException {
        Value leftValue = eval(left);
        Value rightValue = eval(right);
        boolean leftBoolean;
        boolean rightBoolean;
        try {
            BooleanValue leftBooleanValue = (BooleanValue) TypeConverter.convert(leftValue, TypeCategory.BOOL).getValue();
            leftBoolean = leftBooleanValue.value();
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), left.getStartPosition());
        }
        try {
            BooleanValue rightBooleanValue = (BooleanValue) TypeConverter.convert(rightValue, TypeCategory.BOOL).getValue();
            rightBoolean = rightBooleanValue.value();
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), right.getStartPosition());
        }
        returnValue.set(new BooleanValue(function.apply(leftBoolean, rightBoolean)));
    }

    @Override
    public void visit(AndExpression andExpression) throws ZephyrException {
        booleanOperation(andExpression.getLeft(), andExpression.getRight(), Boolean::logicalAnd);
    }

    @Override
    public void visit(OrExpression orExpression) throws ZephyrException {
        booleanOperation(orExpression.getLeft(), orExpression.getRight(), Boolean::logicalOr);
    }

    @Override
    public void visit(NotExpression notExpression) throws ZephyrException {
        notExpression.getExpression().accept(this);
        Value returnValue;
        try {
            returnValue = TypeConverter.convert(this.returnValue.get(), TypeCategory.BOOL);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), notExpression.getExpression().getStartPosition());
        }
        BooleanValue value = (BooleanValue) returnValue.getValue();
        this.returnValue.set(new BooleanValue(!value.value()));
    }

    private static final Map<Pair<TypeCategory, TypeCategory>, TypeCategory> COMPARE_TARGET_MAP = Map.ofEntries(
            Map.entry(new Pair<>(TypeCategory.STRING, TypeCategory.INT), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.STRING, TypeCategory.FLOAT), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.STRING, TypeCategory.BOOL), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.INT, TypeCategory.STRING), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.INT, TypeCategory.FLOAT), TypeCategory.FLOAT),
            Map.entry(new Pair<>(TypeCategory.INT, TypeCategory.BOOL), TypeCategory.INT),
            Map.entry(new Pair<>(TypeCategory.FLOAT, TypeCategory.STRING), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.FLOAT, TypeCategory.INT), TypeCategory.FLOAT),
            Map.entry(new Pair<>(TypeCategory.FLOAT, TypeCategory.BOOL), TypeCategory.FLOAT),
            Map.entry(new Pair<>(TypeCategory.BOOL, TypeCategory.STRING), TypeCategory.STRING),
            Map.entry(new Pair<>(TypeCategory.BOOL, TypeCategory.INT), TypeCategory.INT),
            Map.entry(new Pair<>(TypeCategory.BOOL, TypeCategory.FLOAT), TypeCategory.FLOAT)
    );

    private boolean compare(Expression left, Expression right) throws ZephyrException {
        Value leftValue = eval(left);
        Value rightValue = eval(right);
        TypeCategory leftCategory = leftValue.getValue().getCategory();
        TypeCategory rightCategory = rightValue.getValue().getCategory();
        if (leftCategory.equals(rightCategory)) {
            return leftValue.getValue().equals(rightValue.getValue());
        }
        TypeCategory targetCategory = COMPARE_TARGET_MAP.get(new Pair<>(leftCategory, rightCategory));
        try {
            leftValue = TypeConverter.convert(leftValue, targetCategory);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), left.getStartPosition());
        }
        try {
            rightValue = TypeConverter.convert(rightValue, targetCategory);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), right.getStartPosition());
        }
        return leftValue.getValue().equals(rightValue.getValue());
    }

    @Override
    public void visit(EqualExpression equalExpression) throws ZephyrException {
        returnValue.set(new BooleanValue(compare(equalExpression.getLeft(), equalExpression.getRight())));
    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) throws ZephyrException {
        returnValue.set(new BooleanValue(!compare(notEqualExpression.getLeft(), notEqualExpression.getRight())));
    }

    private void standardComparisonOperation(Expression left, Expression right, BiPredicate<Float, Float> floatFunction, BiPredicate<Integer,Integer> intFunction) throws ZephyrException {
        Value leftValue = eval(left);
        Value rightValue = eval(right);
        TypeCategory leftCategory = leftValue.getValue().getCategory();
        TypeCategory rightCategory = rightValue.getValue().getCategory();
        if (leftCategory.equals(TypeCategory.INT) && rightCategory.equals(TypeCategory.INT)) {
            IntegerValue leftInt = (IntegerValue) leftValue.getValue();
            IntegerValue rightInt = (IntegerValue) rightValue.getValue();
            returnValue.set(new BooleanValue(intFunction.test(leftInt.value(), rightInt.value())));
        } else {
            Pair<FloatValue, FloatValue> floats = convertToFloats(leftValue, rightValue, left.getStartPosition());
            returnValue.set(new BooleanValue(floatFunction.test(floats.first().value(), floats.second().value())));
        }
    }

    private void standardArithmeticOperation(Expression left, Expression right, BiFunction<Float, Float, Float> floatFunction, BiFunction<Integer, Integer, Integer> intFunction) throws ZephyrException {
        Value leftValue = eval(left);
        Value rightValue = eval(right);
        TypeCategory leftCategory = leftValue.getValue().getCategory();
        TypeCategory rightCategory = rightValue.getValue().getCategory();
        if (leftCategory.equals(TypeCategory.INT) && rightCategory.equals(TypeCategory.INT)) {
            IntegerValue leftInt = (IntegerValue) leftValue.getValue();
            IntegerValue rightInt = (IntegerValue) rightValue.getValue();
            returnValue.set(new IntegerValue(intFunction.apply(leftInt.value(), rightInt.value())));
        } else {
            Pair<FloatValue, FloatValue> floats = convertToFloats(leftValue, rightValue, left.getStartPosition());
            returnValue.set(new FloatValue(floatFunction.apply(floats.first().value(), floats.second().value())));
        }
    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) throws ZephyrException {
        standardComparisonOperation(greaterEqualExpression.getLeft(), greaterEqualExpression.getRight(), (left, right) -> left >= right, (left, right) -> left >= right);
    }

    @Override
    public void visit(GreaterExpression greaterExpression) throws ZephyrException {
        standardComparisonOperation(greaterExpression.getLeft(), greaterExpression.getRight(), (left, right) -> left > right, (left, right) -> left > right);
    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) throws ZephyrException {
        standardComparisonOperation(lessEqualExpression.getLeft(), lessEqualExpression.getRight(), (left, right) -> left <= right, (left, right) -> left <= right);
    }

    @Override
    public void visit(LessExpression lessExpression) throws ZephyrException {
        standardComparisonOperation(lessExpression.getLeft(), lessExpression.getRight(), (left, right) -> left < right, (left, right) -> left < right);
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) throws ZephyrException {
        standardArithmeticOperation(multiplyExpression.getLeft(), multiplyExpression.getRight(), (left, right) -> left * right, (left, right) -> left * right);
    }

    private Pair<FloatValue, FloatValue> convertToFloats(Value leftValue, Value rightValue, TextPosition position) throws ZephyrException {
        try {
            leftValue = TypeConverter.convert(leftValue, TypeCategory.FLOAT);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), position);
        }
        try {
            rightValue = TypeConverter.convert(rightValue, TypeCategory.FLOAT);
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), position);
        }
        return new Pair<>((FloatValue) leftValue.getValue(), (FloatValue) rightValue.getValue());
    }
    @Override
    public void visit(DivideExpression divideExpression) throws ZephyrException {
        Value leftValue = eval(divideExpression.getLeft());
        Value rightValue = eval(divideExpression.getRight());
        Pair<FloatValue, FloatValue> floats = convertToFloats(leftValue, rightValue, divideExpression.getStartPosition());
        FloatValue leftFloat = floats.first();
        FloatValue rightFloat = floats.second();
        if(rightFloat.value() == 0) throw new DivideByZeroException(leftValue, divideExpression.getStartPosition());
        returnValue.set(new FloatValue(leftFloat.value() / rightFloat.value()));
    }

    @Override
    public void visit(SubtractExpression subtractExpression) throws ZephyrException {
        standardArithmeticOperation(subtractExpression.getLeft(), subtractExpression.getRight(), (left, right) -> left - right, (left, right) -> left - right);
    }

    @Override
    public void visit(NegationExpression negationExpression) throws ZephyrException {
        Value value = eval(negationExpression.getExpression());
        TypeCategory category = value.getValue().getCategory();
        if (category.equals(TypeCategory.FLOAT)) {
            FloatValue floatValue = (FloatValue) value.getValue();
            returnValue.set(new FloatValue(-floatValue.value()));
        } else {
            try {
                value = TypeConverter.convert(value, TypeCategory.INT);
            } catch (ConversionTypeException e) {
                throw new ConversionException(e.getValue(), e.getTarget(), negationExpression.getExpression().getStartPosition());
            }
            IntegerValue intValue = (IntegerValue) value.getValue();
            returnValue.set(new IntegerValue(-intValue.value()));
        }
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
        for (Map.Entry<String, Literal> entry : structLiteral.getFields().entrySet()) {
            fields.put(entry.getKey(), eval(entry.getValue()));
        }
        returnValue.set(new StructValue(fields));
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
        if (assignmentStatement.getTarget() instanceof DotExpression dotExpression) {
            StructValue target = evalStruct(dotExpression.getValue());
            target.setField(dotExpression.getField(), eval(assignmentStatement.getValue()));
        } else if (assignmentStatement.getTarget() instanceof VariableReference variableReference) {
            Value value = eval(assignmentStatement.getValue());
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
        BooleanValue condition = evalBool(ifStatement.getCondition());
        if (condition.value()) {
            context.createLocalScope();
            try {
                ifStatement.getBody().accept(this);
            } finally {
                context.rollback();
            }
        } else {
            if (ifStatement.getElseBlock() != null) ifStatement.getElseBlock().accept(this);
        }
    }

    @Override
    public void visit(MatchStatement matchStatement) throws ZephyrException {
        matchValue = eval(matchStatement.getExpression()).getValue();
        for (MatchCase matchCase : matchStatement.getCases()) {
            matchCase.accept(this);
        }
    }

    @Override
    public void visit(MatchCase matchCase) throws ZephyrException {
        BareStaticType type = getBareType(matchCase.getPattern(), matchCase.getStartPosition());
        boolean result = TypeChecker.checkValue(matchValue, type);
        if (!result) return;
        context.createLocalScope();
        context.set(matchCase.getVariableName(), new Reference(matchValue));
        try {
            matchCase.getBody().accept(this);
        } finally {
            context.rollback();
        }
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        if (returnStatement.getExpression() != null) {
            returnStatement.getExpression().accept(this);
            // propagate return value
        }
        throw new ReturnSignal();
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        Value defaultValue = eval(variableDefinition.getDefaultValue());
        BareStaticType type = getBareType(variableDefinition.getTypeName(), variableDefinition.getStartPosition());
        try {
            context.set(variableDefinition.getName(), new Reference(TypeConverter.convert(defaultValue, type.getCategory())));
        } catch (ConversionTypeException e) {
            throw new ConversionException(e.getValue(), e.getTarget(), variableDefinition.getDefaultValue().getStartPosition());
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) throws ZephyrException {
        BooleanValue condition = evalBool(whileStatement.getCondition());
        while (condition.value()) {
            context.createLocalScope();
            try {
                whileStatement.getBody().accept(this);
            } finally {
                context.rollback();
            }
            condition = evalBool(whileStatement.getCondition());
        }
    }
}
