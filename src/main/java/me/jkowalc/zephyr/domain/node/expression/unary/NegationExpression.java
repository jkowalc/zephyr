package me.jkowalc.zephyr.domain.node.expression.unary;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class NegationExpression extends DefaultUnaryExpression {
    public NegationExpression(Expression expression) {
        super(expression);
    }
    public NegationExpression(TextPosition startPosition, Expression expression) {
        super(startPosition, expression);
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
