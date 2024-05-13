package me.jkowalc.zephyr.util;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;

public interface ASTVisitor {
    void visit(Program program);
    void visit(FunctionCall functionCall);
    void visit(VariableReference variableReference);
    void visit(AddExpression addExpression);
    void visit(AndExpression andExpression);
    void visit(DivideExpression divideExpression);
    void visit(DotExpression dotExpression);
    void visit(EqualExpression equalExpression);
    void visit(GreaterEqualExpression greaterEqualExpression);
    void visit(GreaterExpression greaterExpression);
    void visit(LessEqualExpression lessEqualExpression);
    void visit(LessExpression lessExpression);
    void visit(MultiplyExpression multiplyExpression);
    void visit(NotEqualExpression notEqualExpression);
    void visit(OrExpression orExpression);
    void visit(SubtractExpression subtractExpression);
    void visit(BooleanLiteral booleanLiteral);
    void visit(IntegerLiteral integerLiteral);
    void visit(FloatLiteral floatLiteral);
    void visit(StringLiteral stringLiteral);
    void visit(StructLiteral structLiteral);
    void visit(NegationExpression negationExpression);
    void visit(NotExpression notExpression);
    void visit(FunctionDefinition functionDefinition);
    void visit(StructDefinition structDefinition);
    void visit(StructDefinitionMember structDefinitionMember);
    void visit(UnionDefinition unionDefinition);
    void visit(AssignmentStatement assignmentStatement);
    void visit(IfStatement ifStatement);
    void visit(MatchStatement matchStatement);
    void visit(MatchCase matchCase);
    void visit(ReturnStatement returnStatement);
    void visit(StatementBlock statementBlock);
    void visit(VariableDefinition variableDefinition);
    void visit(WhileStatement whileStatement);
}
