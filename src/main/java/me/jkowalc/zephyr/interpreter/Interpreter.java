package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.analizer.StaticAnalizer;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;

import java.io.OutputStreamWriter;

public class Interpreter implements ASTVisitor {

    private Program program;

    private final OutputStreamWriter outputStream;

    public Interpreter(OutputStreamWriter outputStream) {
        this.outputStream = outputStream;
    }
    @Override
    public void visit(Program program) throws ZephyrException {
        new StaticAnalizer().visit(program);
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
    }

    @Override
    public void visit(VariableReference variableReference) {

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
    public void visit(DotExpression dotExpression) {

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
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {

    }

    @Override
    public void visit(StringLiteral stringLiteral) {

    }

    @Override
    public void visit(StructLiteral structLiteral) {

    }

    @Override
    public void visit(NegationExpression negationExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {

    }

    @Override
    public void visit(FunctionDefinition functionDefinition) {

    }

    @Override
    public void visit(StructDefinition structDefinition) {

    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {

    }

    @Override
    public void visit(UnionDefinition unionDefinition) {

    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(MatchStatement matchStatement) {

    }

    @Override
    public void visit(MatchCase matchCase) {

    }

    @Override
    public void visit(ReturnStatement returnStatement) {

    }

    @Override
    public void visit(StatementBlock statementBlock) {

    }

    @Override
    public void visit(VariableDefinition variableDefinition) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {
        
    }
}
