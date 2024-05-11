package me.jkowalc.zephyr.domain.node.statement;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class ifStatement extends Node implements Statement {
    private final Expression condition;
    private final StatementBlock body;

    public ifStatement(Expression condition, StatementBlock body) {
        super(null, null);
        this.condition = condition;
        this.body = body;
    }

    public ifStatement(TextPosition startPosition, Expression condition, StatementBlock body) {
        super(startPosition, body.getEndPosition());
        this.condition = condition;
        this.body = body;
    }
}
