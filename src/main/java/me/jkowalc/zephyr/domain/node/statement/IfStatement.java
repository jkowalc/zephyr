package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.ElseBlock;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class IfStatement extends Node implements Statement, ElseBlock {
    private final Expression condition;
    private final StatementBlock body;
    private final ElseBlock elseBlock;

    public IfStatement(Expression condition, StatementBlock body, ElseBlock elseBlock) {
        super(null, null);
        this.condition = condition;
        this.body = body;
        this.elseBlock = elseBlock;
    }

    public IfStatement(TextPosition startPosition, Expression condition, StatementBlock body, ElseBlock elseBlock) {
        super(startPosition, body.getEndPosition());
        this.condition = condition;
        this.body = body;
        this.elseBlock = elseBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
