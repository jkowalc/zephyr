package me.jkowalc.zephyr.domain.node;

import me.jkowalc.zephyr.util.TextPosition;

public interface NodeInterface {
    TextPosition getStartPosition();
    TextPosition getEndPosition();
}
