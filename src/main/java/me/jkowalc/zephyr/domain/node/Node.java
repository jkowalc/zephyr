package me.jkowalc.zephyr.domain.node;

import me.jkowalc.zephyr.util.TextPosition;

public abstract class Node implements NodeInterface {
    private final TextPosition startPosition;
    private final TextPosition endPosition;

    protected Node(TextPosition startPosition, TextPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    protected TextPosition getDefaultStartPosition() {
        return null;
    }
    protected TextPosition getDefaultEndPosition() {
        return null;
    }
    public TextPosition getStartPosition() {
        return startPosition != null ? startPosition : getDefaultStartPosition();
    }
    public TextPosition getEndPosition() {
        return endPosition != null ? endPosition : getDefaultEndPosition();
    }
}
