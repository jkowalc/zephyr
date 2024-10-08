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

public class Lexer implements LexerInterface {
    private final StringBuilder buffer = new StringBuilder();
    private final LineReader reader;
    public static final int MAX_IDENTIFIER_LENGTH = 100;
    public static final int MAX_STRING_LENGTH = 400;
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
            return new Token(tokenStart, keywordType);
        }
        return new IdentifierToken(tokenStart, tokenValue);
    }
    private Float readFractionalPart() throws IOException, LexicalException {
        if(reader.getChar() != '.') return null;
        reader.next();
        int number = 0;
        int fractionalPartLength = 0;
        while(Character.isDigit(reader.getChar())) {
            int result = number * 10 + (reader.getChar() - '0');
            if(result < 0) {
                throw new InvalidNumberException("Fractional part of number is too large", reader.getPosition());
            }
            number = result;
            fractionalPartLength += 1;
            reader.next();
            if(reader.getChar() == '.') {
                throw new InvalidNumberException("Invalid number", reader.getPosition());
            }
        }
        return number / (float) Math.pow(10, fractionalPartLength);
    }
    private Token readNumber() throws IOException, LexicalException {
        if(!Character.isDigit(reader.getChar())) return null;
        TextPosition startPosition = reader.getPosition();
        if (reader.getChar() == '0') {
            reader.next();
            Float fractionalPart;
            if ((fractionalPart = readFractionalPart()) != null)
                return new FloatLiteralToken(startPosition, fractionalPart);
            else if (Character.isDigit(reader.getChar()))
                throw new LexicalException("Integer literal cannot have a leading 0", reader.getPosition());
            else return new IntegerLiteralToken(startPosition, 0);
        }
        int number = 0;
        while (Character.isDigit(reader.getChar())) {
            int result = number * 10 + (reader.getChar() - '0');
            if(result < 0) {
                throw new InvalidNumberException("Number too large", reader.getPosition());
            }
            number = result;
            reader.next();
        }
        Float fractionalPart;
        if ((fractionalPart = readFractionalPart()) != null)
            return new FloatLiteralToken(startPosition, number + fractionalPart);
        else
            return new IntegerLiteralToken(startPosition, number);
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
        return new StringLiteralToken(stringStart, buffer.toString());
    }
    private Token readSingleCharOperator() throws IOException {
        TokenType tokenType = OperatorMapper.mapSingleCharOperator(reader.getChar());
        if(tokenType == null) return null;
        TextPosition operatorPosition = reader.getPosition();
        reader.next();
        return new Token(operatorPosition, tokenType);
    }
    private Token readDoubleCharOperator() throws IOException {
        TokenType tokenType = OperatorMapper.mapDoubleCharOperator("" + reader.getChar() + reader.peek());
        if(tokenType == null) return null;
        TextPosition operatorPosition = reader.getPosition();
        reader.next();
        reader.next();
        return new Token(operatorPosition, tokenType);
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
        return new CommentToken(commentStart, buffer.toString());
    }
    public Token nextToken() throws IOException, LexicalException {
        buffer.setLength(0);
        skipWhitespace();
        if(reader.getChar() == Character.UNASSIGNED) {
            return new Token(reader.getPosition(), TokenType.EOF);
        }
        Token token;
        if((token = readComment()) != null) return token;
        if((token = readIdentifierOrKeyword()) != null) return token;
        if((token = readNumber()) != null) return token;
        if((token = readString()) != null) return token;
        if((token = readDoubleCharOperator()) != null) return token;
        if((token = readSingleCharOperator()) != null) return token;
        throw new InvalidCharacterException("Invalid character " + getRepresentation(reader.getChar()), reader.getPosition());
    }
}
