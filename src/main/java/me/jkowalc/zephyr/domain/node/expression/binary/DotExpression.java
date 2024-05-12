package me.jkowalc.zephyr.domain.node.expression.binary;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class DotExpression extends Node implements Assignable, Expression {
    private final Expression value;
    private final String field;

    public DotExpression(Expression value, String field) {
        super(value.getStartPosition(), null);
        this.value = value;
        this.field = field;
    }

    public DotExpression(TextPosition endPosition, Expression value, String field) {
        super(value.getStartPosition(), endPosition);
        this.value = value;
        this.field = field;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}