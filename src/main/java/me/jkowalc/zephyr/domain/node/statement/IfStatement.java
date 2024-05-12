package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class IfStatement extends Node implements Statement {
    private final Expression condition;
    private final StatementBlock body;

    public IfStatement(Expression condition, StatementBlock body) {
        super(null, null);
        this.condition = condition;
        this.body = body;
    }

    public IfStatement(TextPosition startPosition, Expression condition, StatementBlock body) {
        super(startPosition, body.getEndPosition());
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
