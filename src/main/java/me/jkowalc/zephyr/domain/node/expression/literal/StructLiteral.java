package me.jkowalc.zephyr.domain.node.expression.literal;

import java.util.Map;

public record StructLiteral (Map<String, Literal> fields) implements Literal {
}