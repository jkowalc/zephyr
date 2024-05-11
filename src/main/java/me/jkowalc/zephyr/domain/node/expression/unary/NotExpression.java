package me.jkowalc.zephyr.domain.node.expression.unary;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class NotExpression extends DefaultUnaryExpression {
    public NotExpression(Expression expression) {
        super(expression);
    }
    public NotExpression(TextPosition startPosition, Expression expression) {
        super(startPosition, expression);
    }
}