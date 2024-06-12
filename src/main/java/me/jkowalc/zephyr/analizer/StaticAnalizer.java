package me.jkowalc.zephyr.analizer;

import lombok.Getter;
import me.jkowalc.zephyr.BuiltinFunctionManager;
import me.jkowalc.zephyr.domain.FunctionRepresentation;
import me.jkowalc.zephyr.domain.TypeCheckerResult;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.domain.type.StructStaticType;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.*;
import me.jkowalc.zephyr.exception.analizer.*;
import me.jkowalc.zephyr.exception.scope.VariableNotDefinedScopeException;
import me.jkowalc.zephyr.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StaticAnalizer implements ASTVisitor {
    @Getter
    private Map<String, FunctionRepresentation> functions;

    private ScopedContext<StaticType> context;

    @Getter
    private Map<String, BareStaticType> types;

    EphemeralValue<StaticType> returnType;

    StaticType expectedReturnType;

    EphemeralValue<Statement> lastFunctionStatement;

    private BareStaticType getBareType(String typeName, TextPosition position) throws ZephyrException {
        if(typeName == null)
            return new BareStaticType(TypeCategory.VOID);
        BareStaticType type = types.get(typeName);
        if(type == null)
            throw new TypeNotDefinedException(typeName, position);
        return type;
    }

    private void resetState() {
        this.context = new ScopedContext<>();
        this.returnType = new EphemeralValue<>(null);
        this.expectedReturnType = null;
        this.lastFunctionStatement = new EphemeralValue<>(null);
    }

    @Override
    public void visit(Program program) throws ZephyrException {
        this.types = new TypeBuilder(program.getTypes()).build().getTypes();
        resetState();
        this.functions = new HashMap<>(BuiltinFunctionManager.BUILTIN_FUNCTIONS);
        for(Map.Entry<String, FunctionDefinition> function : program.getFunctions().entrySet()) {
            if(BuiltinFunctionManager.BUILTIN_FUNCTIONS.containsKey(function.getKey())) {
                throw new FunctionAlreadyDefinedException(function.getKey(), true, function.getValue().getStartPosition());
            }
            if(functions.containsKey(function.getKey())) {
                throw new FunctionAlreadyDefinedException(function.getKey(), false, function.getValue().getStartPosition());
            }
            StaticType returnType = new StaticType(getBareType(function.getValue().getReturnType(), function.getValue().getStartPosition()));
            List<StaticType> parameterTypes = new ArrayList<>();
            for(VariableDefinition parameter : function.getValue().getParameters()) {
                parameter.accept(this);
                parameterTypes.add(this.returnType.get());
            }
            functions.put(function.getKey(), new FunctionRepresentation(false, function.getKey(), parameterTypes, returnType, function.getValue()));
        }
        if(!functions.containsKey("main")) {
            throw new MainFunctionNotDefinedException(program.getStartPosition());
        }
        resetState();
        for(FunctionDefinition functionDefinition : program.getFunctions().values()) {
            functionDefinition.accept(this);
            this.returnType.ignore();
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
            this.returnType.ignore();
        }
        this.expectedReturnType = new StaticType(getBareType(functionDefinition.getReturnType(), functionDefinition.getStartPosition()));
        functionDefinition.getBody().accept(this);
        Statement lastStatement = this.lastFunctionStatement.get();
        boolean endedWithoutReturn = !(lastStatement instanceof ReturnStatement);
        if(endedWithoutReturn && expectedReturnType.getBareStaticType().getCategory() != TypeCategory.VOID) {
            throw new NonConvertibleTypeException(expectedReturnType.getBareStaticType(), new BareStaticType(TypeCategory.VOID), functionDefinition.getStartPosition());
        }
        context.rollback();
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        for(Statement statement : statementBlock.getStatements()) {
            if(statement instanceof VariableDefinition variableDefinition && variableDefinition.getDefaultValue() == null) {
                throw new VariableNotInitializedException(variableDefinition.getName(), variableDefinition.getStartPosition());
            }
            statement.accept(this);
        }
        List<Statement> statements = statementBlock.getStatements();
        lastFunctionStatement.set(!statements.isEmpty() ? statementBlock.getStatements().getLast() : null);
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        FunctionRepresentation representation = functions.get(functionCall.getName());
        if(representation == null){
            throw new FunctionNotDefinedException(functionCall.getName(), functionCall.getStartPosition());
        }
        List<StaticType> parameterTypes = representation.parameterTypes();
        int argumentCount = functionCall.getArguments().size();
        if(argumentCount != parameterTypes.size()) {
            throw new InvalidArgumentCountException(functionCall.getName(), parameterTypes.size(), argumentCount, functionCall.getStartPosition());
        }
        for(int i = 0; i < argumentCount; i++) {
            functionCall.getArguments().get(i).accept(this);
            StaticType argumentType = returnType.get();
            StaticType parameterType = parameterTypes.get(i);
            TypeCheckerResult result = TypeChecker.checkType(argumentType.getBareStaticType(), parameterType.getBareStaticType());
            if(result.equals(TypeCheckerResult.ERROR)) {
                throw new NonConvertibleTypeException(parameterType.getBareStaticType(), argumentType.getBareStaticType(), functionCall.getArguments().get(i).getStartPosition());
            }
            if(result.equals(TypeCheckerResult.CONVERTIBLE) && parameterType.isReference()) {
                throw new ConvertiblePassedByReferenceException(functionCall.getArguments().get(i).getStartPosition());
            }
        }
        returnType.set(representation.returnType());
    }

    @Override
    public void visit(VariableReference variableReference) throws AnalizerException {
        try {
            returnType.set(context.get(variableReference.getName()));
        } catch(VariableNotDefinedScopeException e) {
            throw new VariableNotDefinedException(variableReference.getName(), variableReference.getStartPosition());
        }
    }

    @Override
    public void visit(DotExpression dotExpression) throws ZephyrException {
        dotExpression.getValue().accept(this);
        StaticType returnType = this.returnType.get();
        if(!(returnType.getBareStaticType() instanceof StructStaticType structStaticType)) {
            throw new InvalidFieldAccessException((Assignable) dotExpression.getValue(), dotExpression.getField(), false, dotExpression.getStartPosition());
        }
        BareStaticType type = (structStaticType.getFields().get(dotExpression.getField()));
        if(type == null) {
            throw new InvalidFieldAccessException((Assignable) dotExpression.getValue(), dotExpression.getField(), true, dotExpression.getStartPosition());
        }
        this.returnType.set(new StaticType(type, returnType.isMutable(), returnType.isReference()));
    }

    private static final List<BareStaticType> ADD_VALID_TYPES = List.of(
            new BareStaticType(TypeCategory.INT),
            new BareStaticType(TypeCategory.FLOAT),
            new BareStaticType(TypeCategory.STRING)
    );

    @Override
    public void visit(AddExpression addExpression) throws ZephyrException {
        addExpression.getLeft().accept(this);
        BareStaticType leftType = returnType.get().getBareStaticType();
        addExpression.getRight().accept(this);
        BareStaticType rightType = returnType.get().getBareStaticType();
        boolean leftOk = ADD_VALID_TYPES.stream().anyMatch(type -> !TypeChecker.checkType(leftType, type).equals(TypeCheckerResult.ERROR));
        if(!leftOk) throw new InvalidTypeForOperationException(List.of(new BareStaticType(TypeCategory.FLOAT), leftType), "+", addExpression.getLeft().getStartPosition());
        boolean rightOk = ADD_VALID_TYPES.stream().anyMatch(type -> !TypeChecker.checkType(rightType, type).equals(TypeCheckerResult.ERROR));
        if(!rightOk) throw new InvalidTypeForOperationException(List.of(new BareStaticType(TypeCategory.FLOAT), rightType), "+", addExpression.getRight().getStartPosition());
        if(leftType.getCategory().equals(TypeCategory.INT) && rightType.getCategory().equals(TypeCategory.INT)) {
            returnType.set(new StaticType(new BareStaticType(TypeCategory.INT)));
        } else if (leftType.getCategory().equals(TypeCategory.FLOAT) && rightType.getCategory().equals(TypeCategory.INT)) {
            returnType.set(new StaticType(new BareStaticType(TypeCategory.FLOAT)));
        } else if (leftType.getCategory().equals(TypeCategory.INT) && rightType.getCategory().equals(TypeCategory.FLOAT)) {
            returnType.set(new StaticType(new BareStaticType(TypeCategory.FLOAT)));
        } else {
            returnType.set(new StaticType(new BareStaticType(TypeCategory.STRING)));
        }
    }

    public void booleanCheck(Expression expression) throws ZephyrException {
        expression.accept(this);
        BareStaticType type = returnType.get().getBareStaticType();
        TypeCheckerResult result = TypeChecker.checkType(type, new BareStaticType(TypeCategory.BOOL));
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(new BareStaticType(TypeCategory.BOOL), type, expression.getStartPosition());
        }
    }
    @Override
    public void visit(AndExpression andExpression) throws ZephyrException {
        booleanCheck(andExpression.getLeft());
        booleanCheck(andExpression.getRight());
        returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
    }

    @Override
    public void visit(OrExpression orExpression) throws ZephyrException {
        booleanCheck(orExpression.getLeft());
        booleanCheck(orExpression.getRight());
        returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
    }

    @Override
    public void visit(NotExpression notExpression) throws ZephyrException {
        notExpression.getExpression().accept(this);
        BareStaticType type = returnType.get().getBareStaticType();
        TypeCheckerResult result = TypeChecker.checkType(type, new BareStaticType(TypeCategory.BOOL));
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(new BareStaticType(TypeCategory.BOOL), type, notExpression.getExpression().getStartPosition());
        }
        returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
    }

    private static final List<TypeCategory> INVALID_EQUALITY_TYPES = List.of(
            TypeCategory.VOID,
            TypeCategory.UNION
    );

    public void equalityCheck(Expression left, Expression right) throws ZephyrException {
        left.accept(this);
        BareStaticType leftType = returnType.get().getBareStaticType();
        right.accept(this);
        BareStaticType rightType = returnType.get().getBareStaticType();
        if(Stream.of(leftType.getCategory(), rightType.getCategory()).anyMatch(INVALID_EQUALITY_TYPES::contains)) {
            throw new InvalidTypeForOperationException(List.of(leftType, rightType), "==", left.getStartPosition());
        }
        if(rightType.getCategory().equals(leftType.getCategory())) {
            returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
            return;
        }
        if(leftType.getCategory().equals(TypeCategory.STRUCT) || rightType.getCategory().equals(TypeCategory.STRUCT)) {
            throw new InvalidTypeForOperationException(List.of(leftType, rightType), "==", left.getStartPosition());
        }
        returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
    }
    @Override
    public void visit(EqualExpression equalExpression) throws ZephyrException {
        equalityCheck(equalExpression.getLeft(), equalExpression.getRight());
    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) throws ZephyrException {
        equalityCheck(notEqualExpression.getLeft(), notEqualExpression.getRight());
    }
    private static final List<TypeCategory> INVALID_ARIHMETIC_TYPES = List.of(
            TypeCategory.VOID,
            TypeCategory.STRUCT,
            TypeCategory.UNION
    );

    private static final List<TypeCategory> AMBIGUOUS_ARITHMETIC_TYPES = List.of(
            TypeCategory.STRING,
            TypeCategory.BOOL
    );

    private void standardArithmeticCheck(Expression left, Expression right, String operator) throws ZephyrException {
        left.accept(this);
        BareStaticType leftType = returnType.get().getBareStaticType();
        right.accept(this);
        BareStaticType rightType = returnType.get().getBareStaticType();
        if(Stream.of(leftType.getCategory(), rightType.getCategory()).anyMatch(INVALID_ARIHMETIC_TYPES::contains)) {
            throw new InvalidTypeForOperationException(List.of(leftType, rightType), operator, left.getStartPosition());
        }
        if(Stream.of(leftType.getCategory(), rightType.getCategory()).anyMatch(AMBIGUOUS_ARITHMETIC_TYPES::contains)) {
            throw new AmbiguousExpressionType(List.of(new BareStaticType(TypeCategory.INT), new BareStaticType(TypeCategory.FLOAT)), left.getStartPosition());
        }
        if(leftType.getCategory().equals(TypeCategory.FLOAT) || rightType.getCategory().equals(TypeCategory.FLOAT)) {
            returnType.set(StaticType.fromCategory(TypeCategory.FLOAT));
        } else {
            returnType.set(StaticType.fromCategory(TypeCategory.INT));
        }
    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) throws ZephyrException {
        standardArithmeticCheck(greaterEqualExpression.getLeft(), greaterEqualExpression.getRight(), ">=");
    }

    @Override
    public void visit(GreaterExpression greaterExpression) throws ZephyrException {
        standardArithmeticCheck(greaterExpression.getLeft(), greaterExpression.getRight(), ">");
    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) throws ZephyrException {
        standardArithmeticCheck(lessEqualExpression.getLeft(), lessEqualExpression.getRight(), "<=");
    }

    @Override
    public void visit(LessExpression lessExpression) throws ZephyrException {
        standardArithmeticCheck(lessExpression.getLeft(), lessExpression.getRight(), "<");
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) throws ZephyrException {
        standardArithmeticCheck(multiplyExpression.getLeft(), multiplyExpression.getRight(), "*");
    }

    @Override
    public void visit(DivideExpression divideExpression) throws ZephyrException {
        // check if both are convertible to float
        divideExpression.getLeft().accept(this);
        BareStaticType leftType = returnType.get().getBareStaticType();
        divideExpression.getRight().accept(this);
        BareStaticType rightType = returnType.get().getBareStaticType();
        boolean leftOk = !TypeChecker.checkType(leftType, new BareStaticType(TypeCategory.FLOAT)).equals(TypeCheckerResult.ERROR);
        boolean rightOk = !TypeChecker.checkType(rightType, new BareStaticType(TypeCategory.FLOAT)).equals(TypeCheckerResult.ERROR);
        if(!leftOk || !rightOk) {
            throw new InvalidTypeForOperationException(List.of(leftType, rightType), "/", divideExpression.getStartPosition());
        }
        returnType.set(StaticType.fromCategory(TypeCategory.FLOAT));
    }

    @Override
    public void visit(SubtractExpression subtractExpression) throws ZephyrException {
        standardArithmeticCheck(subtractExpression.getLeft(), subtractExpression.getRight(), "-");
    }

    @Override
    public void visit(NegationExpression negationExpression) throws ZephyrException {
        negationExpression.getExpression().accept(this);
        BareStaticType type = returnType.get().getBareStaticType();
        if(List.of(TypeCategory.STRUCT, TypeCategory.UNION, TypeCategory.VOID).contains(type.getCategory())) {
            throw new InvalidTypeForOperationException(List.of(type), "-", negationExpression.getExpression().getStartPosition());
        }
        if(type.getCategory().equals(TypeCategory.STRING)) {
            throw new AmbiguousExpressionType(List.of(new BareStaticType(TypeCategory.INT), new BareStaticType(TypeCategory.FLOAT)), negationExpression.getExpression().getStartPosition());
        }
        if(type.getCategory().equals(TypeCategory.FLOAT)) {
            returnType.set(StaticType.fromCategory(TypeCategory.FLOAT));
        } else {
            returnType.set(StaticType.fromCategory(TypeCategory.INT));
        }
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        returnType.set(StaticType.fromCategory(TypeCategory.BOOL));
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        returnType.set(StaticType.fromCategory(TypeCategory.INT));
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        returnType.set(StaticType.fromCategory(TypeCategory.FLOAT));
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        returnType.set(StaticType.fromCategory(TypeCategory.STRING));
    }

    @Override
    public void visit(StructLiteral structLiteral) throws ZephyrException {
        Map<String, BareStaticType> fields = new SimpleMap<>();
        for(Map.Entry<String, Literal> entry : structLiteral.getFields().entrySet()) {
            entry.getValue().accept(this);
            fields.put(entry.getKey(), returnType.get().getBareStaticType());
        }
        returnType.set(new StaticType(new StructStaticType(fields)));
    }

    @Override
    public void visit(StructDefinition structDefinition) {
        throw new UnsupportedOperationException("Visiting struct definition is not supported. Struct definition checks are in TypeBuilder");
    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {
        throw new UnsupportedOperationException("Visiting struct definition member is not supported. Struct definition checks are in TypeBuilder");
    }

    @Override
    public void visit(UnionDefinition unionDefinition) {
        throw new UnsupportedOperationException("Visiting union definition is not supported. Union definition checks are in TypeBuilder");
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) throws ZephyrException {
        assignmentStatement.getTarget().accept(this);
        StaticType targetType = returnType.get();
        if(!targetType.isMutable())
            throw new ImmutableVariableException(assignmentStatement.getTarget(), assignmentStatement.getStartPosition());
        assignmentStatement.getValue().accept(this);
        StaticType valueType = returnType.get();
        TypeCheckerResult result;
        result = TypeChecker.checkType(valueType.getBareStaticType(), targetType.getBareStaticType());
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(targetType.getBareStaticType(), valueType.getBareStaticType(), assignmentStatement.getStartPosition());
        }
    }

    private void checkCondition(Expression condition) throws ZephyrException {
        condition.accept(this);
        StaticType conditionType = returnType.get();
        TypeCheckerResult result;
        result = TypeChecker.checkType(conditionType.getBareStaticType(), new BareStaticType(TypeCategory.BOOL));
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(new BareStaticType(TypeCategory.BOOL), conditionType.getBareStaticType(), condition.getStartPosition());
        }
    }

    @Override
    public void visit(IfStatement ifStatement) throws ZephyrException {
        checkCondition(ifStatement.getCondition());
        context.createLocalScope();
        ifStatement.getBody().accept(this);
        context.rollback();
    }

    @Override
    public void visit(MatchStatement matchStatement) throws ZephyrException {
        // check if the expression can be evaluated, ignore the result
        matchStatement.getExpression().accept(this);
        this.returnType.ignore();
        for(MatchCase matchCase : matchStatement.getCases()) {
            matchCase.accept(this);
        }
    }

    @Override
    public void visit(MatchCase matchCase) throws ZephyrException {
        context.createLocalScope();
        context.set(matchCase.getVariableName(), new StaticType(getBareType(matchCase.getPattern(), matchCase.getStartPosition())));
        matchCase.getBody().accept(this);
        context.rollback();
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        StaticType gotReturnType;
        if(returnStatement.getExpression() == null) {
            gotReturnType = StaticType.fromCategory(TypeCategory.VOID);
        } else {
            returnStatement.getExpression().accept(this);
            gotReturnType = returnType.get();
        }
        TypeCheckerResult result = TypeChecker.checkType(gotReturnType.getBareStaticType(), expectedReturnType.getBareStaticType());
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(expectedReturnType.getBareStaticType(), gotReturnType.getBareStaticType(), returnStatement.getStartPosition());
        }
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        StaticType variableType = new StaticType(getBareType(variableDefinition.getTypeName(), variableDefinition.getStartPosition()), variableDefinition.isMutable(), variableDefinition.isReference());
        if(variableDefinition.getDefaultValue() != null) {
            variableDefinition.getDefaultValue().accept(this);
            StaticType defaultValueType = returnType.get();
            TypeCheckerResult result;
            result = TypeChecker.checkType(defaultValueType.getBareStaticType(), variableType.getBareStaticType());
            if(result.equals(TypeCheckerResult.ERROR)) {
                throw new NonConvertibleTypeException(variableType.getBareStaticType(), defaultValueType.getBareStaticType(), variableDefinition.getDefaultValue().getStartPosition());
            }
        }
        context.set(variableDefinition.getName(), variableType);
        returnType.set(variableType);
    }

    @Override
    public void visit(WhileStatement whileStatement) throws ZephyrException {
        checkCondition(whileStatement.getCondition());
        context.createLocalScope();
        whileStatement.getBody().accept(this);
        context.rollback();
    }
}
