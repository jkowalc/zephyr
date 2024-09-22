package me.jkowalc.zephyr.domain.node;

import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public abstract class Node implements NodeInterface {
    private final TextPosition startPosition;

    protected Node(TextPosition startPosition) {
        this.startPosition = startPosition;
    }
}
