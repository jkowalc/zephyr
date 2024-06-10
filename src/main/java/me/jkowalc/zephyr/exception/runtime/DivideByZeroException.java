package me.jkowalc.zephyr.exception.runtime;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.util.TextPosition;

public class DivideByZeroException extends ZephyrRuntimeException {
    public DivideByZeroException(Value value, TextPosition position) {
        super("Tried to divide " + value + " by 0", position);
    }
}
