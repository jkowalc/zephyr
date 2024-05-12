package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.ASTVisitor;

public final class SubtractExpression extends DefaultBinaryExpression {

    public SubtractExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
