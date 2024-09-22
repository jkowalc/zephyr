package me.jkowalc.zephyr.domain.node.statement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class AssignmentStatement extends Node implements Statement {
    private final Assignable target;
    private final Expression value;

    public AssignmentStatement(Assignable target, Expression value) {
        super(target.getStartPosition());
        this.target = target;
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
