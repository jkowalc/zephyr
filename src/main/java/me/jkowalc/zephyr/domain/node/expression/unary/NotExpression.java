package me.jkowalc.zephyr.domain.node.expression.unary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public record NotExpression (Expression expression) implements Expression {
}
