package me.jkowalc.zephyr.domain.node.expression.unary;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public class DefaultUnaryExpression extends Node implements Expression {
    private final Expression expression;

    public DefaultUnaryExpression(Expression expression) {
        super(null, null);
        this.expression = expression;
    }
    public DefaultUnaryExpression(TextPosition startPosition, Expression expression) {
        super(startPosition, expression.getEndPosition());
        this.expression = expression;
    }
}
