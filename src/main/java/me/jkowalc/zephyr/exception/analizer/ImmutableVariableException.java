package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.util.TextPosition;

public class ImmutableVariableException extends AnalizerException {
    public ImmutableVariableException(Assignable target, TextPosition position) {
        super("Cannot assign to immutable object " + target.toString(), position);
    }
}
