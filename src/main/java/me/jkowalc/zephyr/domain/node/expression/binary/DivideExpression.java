package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public record DivideExpression (Expression left, Expression right) implements Expression {
}
