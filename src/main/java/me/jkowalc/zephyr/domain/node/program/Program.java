package me.jkowalc.zephyr.domain.node.program;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class Program extends Node {
    private final List<FunctionDefinition> functions;
    private final List<TypeDefinition> types;

    public Program(List<FunctionDefinition> functions, List<TypeDefinition> types) {
        super(null, null);
        this.functions = functions;
        this.types = types;
    }

    @Override
    public TextPosition getStartPosition() {
        if(functions.stream().anyMatch(f -> f.getStartPosition() == null) || types.stream().anyMatch(t -> t.getStartPosition() == null)) {
            return null;
        }
        TextPosition startPosition = functions.stream().map(Node::getStartPosition).min(TextPosition::compareTo).orElse(null);
        TextPosition startPosition2 = types.stream().map(TypeDefinition::getStartPosition).min(TextPosition::compareTo).orElse(null);
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
        TextPosition endPosition = functions.stream().map(Node::getEndPosition).max(TextPosition::compareTo).orElse(null);
        TextPosition endPosition2 = types.stream().map(TypeDefinition::getEndPosition).max(TextPosition::compareTo).orElse(null);
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