package me.jkowalc.zephyr.domain.node.statement;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class ReturnStatement extends Node implements Statement {
    private final Expression expression;

    public ReturnStatement(Expression expression) {
        super(null, null);
        this.expression = expression;
    }

    public ReturnStatement(TextPosition startPosition, TextPosition endPosition, Expression expression) {
        super(startPosition, endPosition);
        this.expression = expression;
    }
}
