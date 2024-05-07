package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

public class CommentToken extends Token {
    private final String value;
    public CommentToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.COMMENT);
        this.value = value;
    }

    public CommentToken(String value) {
        super(null, null, TokenType.COMMENT);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommentToken that = (CommentToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
