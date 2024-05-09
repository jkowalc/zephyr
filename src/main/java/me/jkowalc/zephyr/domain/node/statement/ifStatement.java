package me.jkowalc.zephyr.domain.node.statement;

import me.jkowalc.zephyr.domain.node.StatementBlock;
import me.jkowalc.zephyr.domain.node.expression.Expression;

public record ifStatement (Expression condition, StatementBlock body) implements Statement {
}
