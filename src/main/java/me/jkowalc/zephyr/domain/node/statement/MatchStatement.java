package me.jkowalc.zephyr.domain.node.statement;

import me.jkowalc.zephyr.domain.node.expression.Expression;

import java.util.List;

public record MatchStatement(Expression expression, List<MatchCase> cases) {
}
