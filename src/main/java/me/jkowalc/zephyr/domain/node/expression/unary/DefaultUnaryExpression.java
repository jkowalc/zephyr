package me.jkowalc.zephyr.domain.node.expression.unary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class DefaultUnaryExpression extends Node implements Expression {
    private final Expression expression;

    public DefaultUnaryExpression(Expression expression) {
        super(null);
        this.expression = expression;
    }
    public DefaultUnaryExpression(TextPosition startPosition, Expression expression) {
        super(startPosition);
        this.expression = expression;
    }
}
