package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

public class CommentToken extends Token {
    private final String value;
    public CommentToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.COMMENT);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CommentToken{" +
                "startPosition=" + getStartPosition() +
                ", endPosition=" + getEndPosition() +
                ", value=\"" + getRepresentation(value) + '\"' +
                '}';
    }
}
