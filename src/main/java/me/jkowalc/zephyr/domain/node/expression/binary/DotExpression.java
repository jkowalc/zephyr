package me.jkowalc.zephyr.domain.node.expression.binary;


import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;

@Getter
public final class DotExpression extends Node implements Assignable, Expression {
    private final Expression value;
    private final String field;

    public DotExpression(Expression value, String field) {
        super(null, null);
        this.value = value;
        this.field = field;
    }
}
