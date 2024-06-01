package me.jkowalc.zephyr.domain.node;

import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public abstract class Node implements NodeInterface {
    private final TextPosition startPosition;
    private final TextPosition endPosition;

    protected Node(TextPosition startPosition, TextPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
}
