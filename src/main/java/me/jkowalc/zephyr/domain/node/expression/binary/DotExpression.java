package me.jkowalc.zephyr.domain.node.expression.binary;


import me.jkowalc.zephyr.domain.node.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;

public record DotExpression (Expression value, String field) implements Assignable, Expression {
}
