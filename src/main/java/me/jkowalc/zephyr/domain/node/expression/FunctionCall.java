package me.jkowalc.zephyr.domain.node.expression;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.statement.Statement;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class FunctionCall extends Node implements Expression, Statement {
    private final String name;
    private final List<Expression> parameters;

    public FunctionCall(String name, List<Expression> parameters) {
        super(null, null);
        this.name = name;
        this.parameters = parameters;
    }

    public FunctionCall(TextPosition startPosition, TextPosition endPosition, String name, List<Expression> parameters) {
        super(startPosition, endPosition);
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
