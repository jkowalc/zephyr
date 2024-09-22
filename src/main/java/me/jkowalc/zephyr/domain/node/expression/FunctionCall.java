package me.jkowalc.zephyr.domain.node.expression;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.statement.Statement;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class FunctionCall extends Node implements Expression, Statement {
    private final String name;
    private final List<Expression> arguments;

    public FunctionCall(String name, List<Expression> arguments) {
        super(null);
        this.name = name;
        this.arguments = arguments;
    }

    public FunctionCall(TextPosition startPosition, String name, List<Expression> arguments) {
        super(startPosition);
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
