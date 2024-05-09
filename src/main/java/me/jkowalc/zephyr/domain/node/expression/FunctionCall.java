package me.jkowalc.zephyr.domain.node.expression;


import java.util.List;

public record FunctionCall (String name, List<Expression> parameters) implements Expression {
}
