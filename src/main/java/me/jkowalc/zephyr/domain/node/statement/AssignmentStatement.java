package me.jkowalc.zephyr.domain.node.statement;

import me.jkowalc.zephyr.domain.node.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;

public record AssignmentStatement (Assignable target, Expression value) implements Statement {
}
