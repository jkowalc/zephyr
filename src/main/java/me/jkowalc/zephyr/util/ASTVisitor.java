package me.jkowalc.zephyr.util;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.exception.ZephyrException;

public interface ASTVisitor {
    void visit(Program program) throws ZephyrException;
    void visit(FunctionCall functionCall) throws ZephyrException;
    void visit(VariableReference variableReference) throws ZephyrException;
    void visit(AddExpression addExpression) throws ZephyrException;
    void visit(AndExpression andExpression) throws ZephyrException;
    void visit(DivideExpression divideExpression) throws ZephyrException;
    void visit(DotExpression dotExpression) throws ZephyrException;
    void visit(EqualExpression equalExpression) throws ZephyrException;
    void visit(GreaterEqualExpression greaterEqualExpression) throws ZephyrException;
    void visit(GreaterExpression greaterExpression) throws ZephyrException;
    void visit(LessEqualExpression lessEqualExpression) throws ZephyrException;
    void visit(LessExpression lessExpression) throws ZephyrException;
    void visit(MultiplyExpression multiplyExpression) throws ZephyrException;
    void visit(NotEqualExpression notEqualExpression) throws ZephyrException;
    void visit(OrExpression orExpression) throws ZephyrException;
    void visit(SubtractExpression subtractExpression) throws ZephyrException;
    void visit(BooleanLiteral booleanLiteral) throws ZephyrException;
    void visit(IntegerLiteral integerLiteral) throws ZephyrException;
    void visit(FloatLiteral floatLiteral) throws ZephyrException;
    void visit(StringLiteral stringLiteral) throws ZephyrException;
    void visit(StructLiteral structLiteral) throws ZephyrException;
    void visit(StructLiteralMember structLiteralMember) throws ZephyrException;
    void visit(NegationExpression negationExpression) throws ZephyrException;
    void visit(NotExpression notExpression) throws ZephyrException;
    void visit(FunctionDefinition functionDefinition) throws ZephyrException;
    void visit(StructDefinition structDefinition) throws ZephyrException;
    void visit(StructDefinitionMember structDefinitionMember) throws ZephyrException;
    void visit(UnionDefinition unionDefinition) throws ZephyrException;
    void visit(AssignmentStatement assignmentStatement) throws ZephyrException;
    void visit(IfStatement ifStatement) throws ZephyrException;
    void visit(MatchStatement matchStatement) throws ZephyrException;
    void visit(MatchCase matchCase) throws ZephyrException;
    void visit(ReturnStatement returnStatement) throws ZephyrException;
    void visit(StatementBlock statementBlock) throws ZephyrException;
    void visit(VariableDefinition variableDefinition) throws ZephyrException;
    void visit(WhileStatement whileStatement) throws ZephyrException;
}
