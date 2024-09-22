package me.jkowalc.zephyr.domain.node;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

public interface NodeInterface {
    TextPosition getStartPosition();
    void accept(ASTVisitor visitor) throws ZephyrException;
}
