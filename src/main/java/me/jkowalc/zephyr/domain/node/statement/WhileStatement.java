package me.jkowalc.zephyr.domain.node.statement;

import me.jkowalc.zephyr.domain.node.StatementBlock;
import me.jkowalc.zephyr.domain.node.expression.Expression;

public record WhileStatement (Expression condition, StatementBlock body) implements Statement {
}
