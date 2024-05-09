package me.jkowalc.zephyr.domain.node;

import me.jkowalc.zephyr.domain.node.statement.Statement;

public record VariableDefinition  (
        String name,
        String typeName
) implements Statement {
}
