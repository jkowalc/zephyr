package me.jkowalc.zephyr.analizer;

import lombok.Getter;
import me.jkowalc.zephyr.BuiltinFunctionManager;
import me.jkowalc.zephyr.domain.CustomFunctionRepresentation;
import me.jkowalc.zephyr.domain.FunctionRepresentation;
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
import me.jkowalc.zephyr.domain.type.StructStaticType;
import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.*;
import me.jkowalc.zephyr.exception.analizer.*;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.EphemeralValue;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticAnalizer implements ASTVisitor {
    @Getter
    private Map<String, FunctionRepresentation> functions;

    private ScopedContext<StaticType> context;

    private TypeChecker typeChecker;

    EphemeralValue<StaticType> returnType;

    StaticType expectedReturnType;

    EphemeralValue<Statement> lastFunctionStatement;

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
        if(!functions.containsKey("main")) {
            throw new MainFunctionNotDefinedException(program.getStartPosition());
        }
        this.typeChecker = new TypeChecker(program.getTypes());
        this.context = new ScopedContext<>();
        this.returnType = new EphemeralValue<>(null);
        this.expectedReturnType = null;
        this.lastFunctionStatement = new EphemeralValue<>(null);
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
        }
        this.expectedReturnType = StaticType.fromString(functionDefinition.getReturnType());
        functionDefinition.getBody().accept(this);
        Statement lastStatement = this.lastFunctionStatement.get();
        boolean endedWithoutReturn = !(lastStatement instanceof ReturnStatement);
        if(endedWithoutReturn && expectedReturnType.getCategory() != TypeCategory.VOID) {
            throw new NonConvertibleTypeException(expectedReturnType, new StaticType(TypeCategory.VOID), functionDefinition.getStartPosition());
        }
        context.rollback();
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        context.createLocalScope();
        for(Statement statement : statementBlock.getStatements()) {
            if(statement instanceof VariableDefinition variableDefinition && variableDefinition.getDefaultValue() == null) {
                throw new VariableNotInitializedException(variableDefinition.getName(), variableDefinition.getStartPosition());
            }
            statement.accept(this);
        }
        lastFunctionStatement.set(statementBlock.getStatements().getLast());
        context.rollback();
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        FunctionRepresentation representation = functions.get(functionCall.getName());
        if(representation == null){
            throw new FunctionNotDefinedException(functionCall.getName(), functionCall.getStartPosition());
        }
        List<StaticType> parameterTypes = representation.getParameterTypes();
        int argumentCount = functionCall.getArguments().size();
        if(argumentCount != parameterTypes.size()) {
            throw new InvalidArgumentCountException(functionCall.getName(), parameterTypes.size(), argumentCount, functionCall.getStartPosition());
        }
        for(int i = 0; i < argumentCount; i++) {
            functionCall.getArguments().get(i).accept(this);
            StaticType argumentType = returnType.get();
            StaticType parameterType = parameterTypes.get(i);
            TypeCheckerResult result;
            try {
                result = typeChecker.checkType(argumentType, parameterType);
            } catch(TypeNotDefinedException e) {
                throw new TypeNotDefinedAnalizerException(e.getName(), functionCall.getArguments().get(i).getStartPosition());
            }
            if(result.equals(TypeCheckerResult.ERROR)) {
                throw new NonConvertibleTypeException(parameterType, argumentType, functionCall.getArguments().get(i).getStartPosition());
            }
            if(result.equals(TypeCheckerResult.CONVERTIBLE) && parameterType.isReference()) {
                throw new ConvertiblePassedByReferenceException(functionCall.getArguments().get(i).getStartPosition());
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
            StaticType type = typeChecker.getFieldType(returnType.get(), dotExpression.getField());
            if(type == null) {
                throw new InvalidFieldAccessException((Assignable) dotExpression.getValue(), dotExpression.getField(), dotExpression.getStartPosition());
            }
            returnType.set(type);
        } catch(TypeNotDefinedException e) {
            throw new TypeNotDefinedAnalizerException(e.getName(), dotExpression.getStartPosition());
        }
    }

    @Override
    public void visit(AddExpression addExpression) throws ZephyrException {
        addExpression.getLeft().accept(this);
        StaticType leftType = returnType.get();
        addExpression.getRight().accept(this);
        StaticType rightType = returnType.get();
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
        returnType.set(new StaticType(TypeCategory.BOOL));
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        returnType.set(new StaticType(TypeCategory.BOOL));
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        returnType.set(new StaticType(TypeCategory.INT));
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        returnType.set(new StaticType(TypeCategory.FLOAT));
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        returnType.set(new StaticType(TypeCategory.STRING));
    }

    @Override
    public void visit(StructLiteral structLiteral) throws ZephyrException {
        Map<String, StaticType> fields = new SimpleMap<>();
        for(Map.Entry<String, Literal> entry : structLiteral.getFields().entrySet()) {
            entry.getValue().accept(this);
            fields.put(entry.getKey(), returnType.get());
        }
        returnType.set(new StructStaticType(fields));
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
        StaticType targetType = returnType.get();
        if(!targetType.isMutable())
            throw new ImmutableVariableException(assignmentStatement.getTarget(), assignmentStatement.getStartPosition());
        assignmentStatement.getValue().accept(this);
        StaticType valueType = returnType.get();
        TypeCheckerResult result;
        try {
            result = typeChecker.checkType(valueType, targetType);
        } catch(TypeNotDefinedException e) {
            throw new TypeNotDefinedAnalizerException(e.getName(), assignmentStatement.getStartPosition());
        }
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(targetType, valueType, assignmentStatement.getStartPosition());
        }
    }

    private void checkCondition(Expression condition) throws ZephyrException {
        condition.accept(this);
        StaticType conditionType = returnType.get();
        TypeCheckerResult result;
        try {
            result = typeChecker.checkType(conditionType, new StaticType(TypeCategory.BOOL));
        } catch(TypeNotDefinedException e) {
            throw new TypeNotDefinedAnalizerException(e.getName(), condition.getStartPosition());
        }
        if(result.equals(TypeCheckerResult.ERROR)) {
            throw new NonConvertibleTypeException(new StaticType(TypeCategory.BOOL), conditionType, condition.getStartPosition());
        }
    }

    @Override
    public void visit(IfStatement ifStatement) throws ZephyrException {
        checkCondition(ifStatement.getCondition());
        ifStatement.getBody().accept(this);
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
        try {
            context.add(matchCase.getVariableName(), StaticType.fromString(matchCase.getPattern()));
        } catch(VariableAlreadyDefinedException ignored) {
            throw new ZephyrInternalException();
        }
        matchCase.getBody().accept(this);
        context.rollback();
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        StaticType gotReturnType = null;
        if(returnStatement.getExpression() == null) {
            gotReturnType = new StaticType(TypeCategory.VOID);
        } else {
            returnStatement.getExpression().accept(this);
            gotReturnType = returnType.get();
        }
        // TODO: check return type
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        StaticType variableType = StaticType.fromString(variableDefinition.getTypeName(), variableDefinition.isMutable(), variableDefinition.isReference());
        if(variableDefinition.getDefaultValue() != null) {
            variableDefinition.getDefaultValue().accept(this);
            StaticType defaultValueType = returnType.get();
            TypeCheckerResult result;
            try {
                result = typeChecker.checkType(defaultValueType, variableType);
            } catch (TypeNotDefinedException e) {
                throw new TypeNotDefinedAnalizerException(e.getName(), variableDefinition.getDefaultValue().getStartPosition());
            }
            if(result.equals(TypeCheckerResult.ERROR)) {
                throw new NonConvertibleTypeException(variableType, defaultValueType, variableDefinition.getDefaultValue().getStartPosition());
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
        checkCondition(whileStatement.getCondition());
        whileStatement.getBody().accept(this);
    }
}
