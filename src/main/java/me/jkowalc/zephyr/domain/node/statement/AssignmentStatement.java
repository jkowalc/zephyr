package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class AssignmentStatement extends Node implements Statement {
    private final Assignable target;
    private final Expression value;

    public AssignmentStatement(Assignable target, Expression value) {
        super(null, null);
        this.target = target;
        this.value = value;
    }

    @Override
    protected TextPosition getDefaultStartPosition() {
        return target.getStartPosition();
    }

    @Override
    protected TextPosition getDefaultEndPosition() {
        return value.getEndPosition();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
