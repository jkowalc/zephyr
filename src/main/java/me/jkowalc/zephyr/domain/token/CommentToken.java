package me.jkowalc.zephyr.domain.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

@Getter
@EqualsAndHashCode(callSuper = false)
public class CommentToken extends Token {
    private final String value;
    public CommentToken(TextPosition startPosition, String value) {
        super(startPosition, TokenType.COMMENT);
        this.value = value;
    }

    public CommentToken(String value) {
        super(null, TokenType.COMMENT);
        this.value = value;
    }

    @Override
    public String toString() {
        return "CommentToken{" +
                "startPosition=" + getStartPosition() +
                ", value=\"" + getRepresentation(value) + '\"' +
                '}';
    }
}
