package me.jkowalc.zephyr.domain.node;

import me.jkowalc.zephyr.domain.node.statement.Statement;

import java.util.List;

public record StatementBlock (
        List<Statement> statements
) {
}
