package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public record GreaterExpression (Expression left, Expression right) {
}
