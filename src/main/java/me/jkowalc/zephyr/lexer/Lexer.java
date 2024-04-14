package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.*;
import me.jkowalc.zephyr.input.LineReader;
import me.jkowalc.zephyr.util.TextPosition;

import java.io.IOException;
import java.io.InputStreamReader;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

public class Lexer {
    private final StringBuilder buffer = new StringBuilder();
    private final LineReader reader;
    public static final int MAX_IDENTIFIER_LENGTH = 100;
    public static final int MAX_STRING_LENGTH = 400;
    public static final int MAX_NUMBER_LENGTH = 50;
    public static final int MAX_COMMENT_LENGTH = 400;
    public Lexer(InputStreamReader inputStreamReader) throws IOException {
        this.reader = new LineReader(inputStreamReader);
    }
    private void skipWhitespace() throws IOException {
        while(Character.isWhitespace(reader.getChar())) {
            reader.next();
        }
    }
    private Token readIdentifierOrKeyword() throws IOException, LexicalException {
        if(!Character.isUnicodeIdentifierStart(reader.getChar())) return null;
        TextPosition tokenStart = reader.getPosition();
        while(Character.isUnicodeIdentifierPart(reader.getChar()) && reader.getChar() != Character.UNASSIGNED) {
            if(buffer.length() >= MAX_IDENTIFIER_LENGTH) {
                throw new TokenTooLongException("Identifier too long, max is " + MAX_IDENTIFIER_LENGTH, tokenStart);
            }
            buffer.append(reader.getChar());
            reader.next();
        }
        String tokenValue = buffer.toString();
        TokenType keywordType = KeywordMapper.mapKeyword(tokenValue);
        if(keywordType != null) {
            return new Token(tokenStart, reader.getPosition().subtractColumn(1), keywordType);
        }
        return new IdentifierToken(tokenStart, reader.getPosition().subtractColumn(1), tokenValue);
    }
    private boolean readFractionalPart() throws IOException, LexicalException {
        if(reader.getChar() != '.') return false;
        buffer.append('.');
        reader.next();
        while(Character.isDigit(reader.getChar())) {
            if(buffer.length() >= MAX_NUMBER_LENGTH) throw new TokenTooLongException("Number too long, max is " + MAX_NUMBER_LENGTH, reader.getPosition());
            buffer.append(reader.getChar());
            reader.next();
            if(reader.getChar() == '.') {
                throw new InvalidNumberException("Invalid number", reader.getPosition());
            }
        }
        return true;
    }
    private Token readNumber() throws IOException, LexicalException {
        if(!Character.isDigit(reader.getChar())) return null;
        TextPosition startPosition = reader.getPosition();
        try {
            if (reader.getChar() == '0') {
                buffer.append('0');
                reader.next();
                if (readFractionalPart())
                    return new FloatLiteralToken(startPosition, reader.getPosition().subtractColumn(1), buffer.toString());
                else if (Character.isDigit(reader.getChar()))
                    throw new LexicalException("Integer literal cannot start with a 0, unless it is 0", reader.getPosition());
                else return new IntegerLiteralToken(startPosition, reader.getPosition().subtractColumn(1), "0");
            }
            while (Character.isDigit(reader.getChar())) {
                if (buffer.length() >= MAX_NUMBER_LENGTH)
                    throw new TokenTooLongException("Number too long, max is " + MAX_NUMBER_LENGTH, reader.getPosition());
                buffer.append(reader.getChar());
                reader.next();
            }
            if (readFractionalPart())
                return new FloatLiteralToken(startPosition, reader.getPosition().subtractColumn(1), buffer.toString());
            else
                return new IntegerLiteralToken(startPosition, reader.getPosition().subtractColumn(1), buffer.toString());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("Invalid number", startPosition);
        }
    }
    private boolean readEscapeSequence() throws IOException, LexicalException {
        if(reader.getChar() != '\\') return false;
        TextPosition escapeStart = reader.getPosition();
        reader.next();
        switch(reader.getChar()) {
            case 'n': buffer.append('\n'); break;
            case 't': buffer.append('\t'); break;
            case 'r': buffer.append('\r'); break;
            case 'b': buffer.append('\b'); break;
            case 'f': buffer.append('\f'); break;
            case '"': buffer.append('"'); break;
            case '\\': buffer.append('\\'); break;
            default: throw new InvalidEscapeSequenceException("Invalid escape sequence \\" + getRepresentation(reader.getChar()), escapeStart);
        }
        reader.next();
        return true;
    }
    private Token readString() throws IOException, LexicalException {
        if(reader.getChar() != '"') return null;
        TextPosition stringStart = reader.getPosition();
        reader.next();
        while(reader.getChar() != '"' && reader.getChar() != Character.UNASSIGNED) {
            if(buffer.length() >= MAX_STRING_LENGTH) {
                throw new TokenTooLongException("String too long, max is " + MAX_STRING_LENGTH, stringStart);
            }
            if(!readEscapeSequence()) {
                buffer.append(reader.getChar());
                reader.next();
            }
        }
        if(reader.getChar() == Character.UNASSIGNED) {
            throw new UnterminatedStringException("Unterminated string", stringStart);
        }
        reader.next();
        return new StringLiteralToken(stringStart, reader.getPosition(), buffer.toString());
    }
    private Token readSingleCharOperator() throws IOException {
        TokenType tokenType = OperatorMapper.mapSingleCharOperator(reader.getChar());
        if(tokenType == null) return null;
        TextPosition operatorPosition = reader.getPosition();
        reader.next();
        return new Token(operatorPosition, operatorPosition, tokenType);
    }
    private Token readDoubleCharOperator() throws IOException {
        TokenType tokenType = OperatorMapper.mapDoubleCharOperator("" + reader.getChar() + reader.peek());
        if(tokenType == null) return null;
        TextPosition operatorPosition = reader.getPosition();
        reader.next();
        reader.next();
        return new Token(operatorPosition, operatorPosition.addColumn(1), tokenType);
    }
    private Token readComment() throws IOException, LexicalException {
        if(reader.getChar() != '/') return null;
        if(reader.peek() != '/') return null;
        TextPosition commentStart = reader.getPosition();
        reader.next(); reader.next();
        while(reader.getChar() != '\n' && reader.getChar() != Character.UNASSIGNED) {
            if(buffer.length() >= MAX_COMMENT_LENGTH) {
                throw new TokenTooLongException("Comment too long, max is " + MAX_COMMENT_LENGTH, commentStart);
            }
            buffer.append(reader.getChar());
            reader.next();
        }
        return new CommentToken(commentStart, reader.getPosition().subtractColumn(1), buffer.toString());
    }
    public Token nextToken() throws IOException, LexicalException {
        buffer.setLength(0);
        skipWhitespace();
        Token token;
        if(reader.getChar() == Character.UNASSIGNED) {
            return new Token(reader.getPosition(), reader.getPosition(), TokenType.EOF);
        }
        token = readComment();
        if (token != null) {return token;}
        token = readIdentifierOrKeyword();
        if (token != null) {return token;}
        token = readNumber();
        if (token != null) {return token;}
        token = readString();
        if (token != null) {return token;}
        token = readDoubleCharOperator();
        if (token != null) {return token;}
        token = readSingleCharOperator();
        if (token != null) {return token;}
        throw new InvalidCharacterException("Invalid character " + getRepresentation(reader.getChar()), reader.getPosition());
    }
}
