package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public record LessEqualExpression (Expression left, Expression right) implements Expression {
}
