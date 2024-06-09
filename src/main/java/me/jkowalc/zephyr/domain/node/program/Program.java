package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class Program extends Node {
    private final Map<String, FunctionDefinition> functions;
    private final Map<String, TypeDefinition> types;

    public Program(Map<String, FunctionDefinition> functions, Map<String, TypeDefinition> types) {
        super(null, null);
        this.functions = functions;
        this.types = types;
    }

    @Override
    public TextPosition getStartPosition() {
        TextPosition startPosition = functions.values().stream().map(Node::getStartPosition).min(TextPosition::compareTo).orElse(null);
        TextPosition startPosition2 = types.values().stream().map(TypeDefinition::getStartPosition).min(TextPosition::compareTo).orElse(null);
        if (startPosition == null) {
            return startPosition2;
        }
        if (startPosition2 == null) {
            return startPosition;
        }
        return startPosition.compareTo(startPosition2) < 0 ? startPosition : startPosition2;
    }

    @Override
    public TextPosition getEndPosition() {
        TextPosition endPosition = functions.values().stream().map(Node::getEndPosition).max(TextPosition::compareTo).orElse(null);
        TextPosition endPosition2 = types.values().stream().map(TypeDefinition::getEndPosition).max(TextPosition::compareTo).orElse(null);
        if (endPosition == null) {
            return endPosition2;
        }
        if (endPosition2 == null) {
            return endPosition;
        }
        return endPosition.compareTo(endPosition2) > 0 ? endPosition : endPosition2;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}