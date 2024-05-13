package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import me.jkowalc.zephyr.lexer.LexerInterface;
import me.jkowalc.zephyr.util.SimpleMap;
import me.jkowalc.zephyr.util.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final TokenReader reader;
    public Parser(LexerInterface lexer) throws LexicalException, IOException {
        this.reader = new TokenReader(lexer);
    }

    // program = {struct_definition | union_definition | function_definition };
    public Program parseProgram() throws LexicalException, SyntaxException, IOException {
        HashMap<String, TypeDefinition> types = new HashMap<>();
        HashMap<String, FunctionDefinition> functions = new HashMap<>();
        boolean isEnd = false;
        while(!isEnd) {
            StructDefinition structDefinition = parseStructDefinition();
            if (structDefinition != null) {
                if(types.containsKey(structDefinition.getName())) {
                    throw new SyntaxException("Type with name " + structDefinition.getName() + " already defined" +
                            " (previous definition at " + types.get(structDefinition.getName()).getStartPosition() + ")", structDefinition.getStartPosition());
                }
                types.put(structDefinition.getName(), structDefinition);
                continue;
            }
            UnionDefinition unionDefinition = parseUnionDefinition();
            if (unionDefinition != null) {
                if(types.containsKey(unionDefinition.getName())) {
                    throw new SyntaxException("Type with name " + unionDefinition.getName() + " already defined" +
                            " (previous definition at " + types.get(unionDefinition.getName()).getStartPosition() + ")", unionDefinition.getStartPosition());
                }
                types.put(unionDefinition.getName(), unionDefinition);
                continue;
            }
            FunctionDefinition functionDefinition = parseFunctionDefinition();
            if (functionDefinition != null) {
                if(functions.containsKey(functionDefinition.getName())) {
                    throw new SyntaxException("Function with name " + functionDefinition.getName() + " already defined" +
                            " (previous definition at " + functions.get(functionDefinition.getName()).getStartPosition() + ")", functionDefinition.getStartPosition());
                }
                functions.put(functionDefinition.getName(), functionDefinition);
                continue;
            }
            isEnd = true;
        }
        return new Program(functions, types);
    }

    // struct_definition = "struct", identifier, "{", struct_members, "}";
    private StructDefinition parseStructDefinition() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.STRUCT) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
        }
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.OPEN_BRACE) {
            throw new SyntaxException("Expected '{'", reader.getToken().getStartPosition());
        }
        List<StructDefinitionMember> members = new ArrayList<>();
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_BRACE) {
                break;
            }
            StructDefinitionMember member = parseStructDefinitionMember();
            if(member == null) {
                throw new SyntaxException("Expected struct member", reader.getToken().getStartPosition());
            }
            members.add(member);
        } while(reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_BRACE) {
            throw new SyntaxException("Expected '}'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StructDefinition(startPosition, endPosition, identifierToken.getValue(), members);
    }

    // struct_members = type, identifier, {",", type, identifier};
    private StructDefinitionMember parseStructDefinitionMember() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IDENTIFIER) {
            return null;
        }
        IdentifierToken typeToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
        }
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        return new StructDefinitionMember(typeToken.getStartPosition(), identifierToken.getEndPosition(), identifierToken.getValue(), typeToken.getValue());
    }

    // union_definition = "union", identifier, "{", union_members, "}";
    private UnionDefinition parseUnionDefinition() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.UNION) return null;
        List<String> typeNames = new ArrayList<>();
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
        }
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.OPEN_BRACE) {
            throw new SyntaxException("Expected '{'", reader.getToken().getStartPosition());
        }
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_BRACE) {
                break;
            }
            if(reader.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxException("Expected type identifier", reader.getToken().getStartPosition());
            }
            typeNames.add(((IdentifierToken) reader.getToken()).getValue());
            reader.next();
        } while(reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_BRACE) {
            throw new SyntaxException("Expected '}'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new UnionDefinition(startPosition, endPosition, identifierToken.getValue(), typeNames);
    }

    private FunctionDefinition parseFunctionDefinition() throws SyntaxException, LexicalException, IOException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            throw new SyntaxException("Expected '('", reader.getToken().getStartPosition());
        }
        List<VariableDefinition> parameters = new ArrayList<>();
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_PARENTHESIS) {
                break;
            }
            VariableDefinition parameter = parseVariableDefinition(null);
            if(parameter == null && !parameters.isEmpty()) {
                throw new SyntaxException("Expected parameter", reader.getToken().getStartPosition());
            }
            if(parameter == null) {
                break;
            }
            parameters.add(parameter);
        } while(reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        reader.next();
        String returnType = null;
        if(reader.getType() == TokenType.ARROW) {
            reader.next();
            if(reader.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxException("Expected return type", reader.getToken().getStartPosition());
            }
            IdentifierToken returnTypeToken = (IdentifierToken) reader.getToken();
            returnType = returnTypeToken.getValue();
            reader.next();
        }
        StatementBlock body = parseStatementBlock();
        if(body == null) {
            throw new SyntaxException("Expected statement block", reader.getToken().getStartPosition());
        }
        return new FunctionDefinition(identifierToken.getStartPosition(), identifierToken.getValue(), parameters, body, returnType);
    }

    // statement = assignment
    //          | variable_declaration
    //          | return_statement
    //          | loop
    //          | conditional_statement
    //          | match_statement
    //          | function_call_statement
    //          | block;
    public StatementBlock parseStatementBlock() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.OPEN_BRACE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        List<Statement> statements = new ArrayList<>();
        do {
            Statement statement;
            if ((statement = parseReturnStatement()) != null){
                statements.add(statement);
                continue;
            }
            if ((statement = parseWhileStatement()) != null) {
                statements.add(statement);
                continue;
            }
            if ((statement = parseIfStatement()) != null) {
                statements.add(statement);
                continue;
            }
            if ((statement = parseMatchStatement()) != null) {
                statements.add(statement);
                continue;
            }
            if ((statement = parseStatementBlock()) != null) {
                statements.add(statement);
                continue;
            }
            if ((statement = parseExpressionStatement()) != null) {
                statements.add(statement);
                continue;
            }
            if(reader.getType() == TokenType.SEMICOLON) {
                reader.next();
            }
        } while(reader.getType() != TokenType.CLOSE_BRACE);
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StatementBlock(startPosition, endPosition, statements);
    }

    private Statement parseExpressionStatement() throws LexicalException, SyntaxException, IOException {
        Expression dotExpression = parseDotExpression();
        if(dotExpression == null) {
            return null;
        }
        if(dotExpression instanceof FunctionCall) {
            return (FunctionCall) dotExpression;
        }
        if(dotExpression instanceof Assignable && reader.getType() == TokenType.ASSIGNMENT) {
            reader.next();
            return new AssignmentStatement((Assignable) dotExpression, parseExpression());
        }
        if(dotExpression instanceof VariableReference) {
            VariableDefinition variableDefinition = parseVariableDefinition(((VariableReference) dotExpression));
            if(variableDefinition != null) {
                if(variableDefinition.getDefaultValue() == null) {
                    throw new SyntaxException("All variables must be initialized", variableDefinition.getStartPosition());
                }
                if(variableDefinition.isReference()) {
                    throw new SyntaxException("The ref or mref keyword is not valid in function parameters", variableDefinition.getStartPosition());
                }
                return variableDefinition;
            }
        }
        throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
    }

    private final static List<TokenType> modifierTokenTypes = List.of(TokenType.MUT, TokenType.REF, TokenType.MREF);
    // variable_declaration = type, variable_modifier, identifier, "=", expression, ";";
    private VariableDefinition parseVariableDefinition(VariableReference typeNameExpression) throws LexicalException, IOException, SyntaxException {
        TextPosition startPosition;
        String typeName;
        if(typeNameExpression == null) {
            if(reader.getType() != TokenType.IDENTIFIER) {
                return null;
            }
            IdentifierToken typeToken = (IdentifierToken) reader.getToken();
            typeName = typeToken.getValue();
            startPosition = typeToken.getStartPosition();
            reader.next();
        } else {
            // type is already parsed
            if(!(modifierTokenTypes.contains(reader.getType()) || reader.getType() == TokenType.IDENTIFIER)) return null;
            typeName = typeNameExpression.getName();
            startPosition = typeNameExpression.getStartPosition();
        }

        boolean isMutable = false;
        boolean isReference = false;
        while(reader.getType() != TokenType.IDENTIFIER) {
            switch(reader.getType()) {
                case MUT -> isMutable = true;
                case REF -> isReference = true;
                case MREF -> {
                    isMutable = true;
                    isReference = true;
                }
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            }
            reader.next();
        }
        IdentifierToken nameToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.ASSIGNMENT) {
            return new VariableDefinition(startPosition, nameToken.getEndPosition(), nameToken.getValue(), typeName, isMutable, isReference, null);
        }
        reader.next();
        Expression defaultValue = parseExpression();
        if(defaultValue == null) {
            throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
        }
        return new VariableDefinition(startPosition, defaultValue.getEndPosition(), nameToken.getValue(), typeName, isMutable, isReference, defaultValue);
    }

    // return_statement = "return", [expression], ";";
    private ReturnStatement parseReturnStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.RETURN) return null;
        Token returnToken = reader.getToken();
        reader.next();
        Expression expression = parseExpression();
        reader.next();
        if(expression != null) return new ReturnStatement(returnToken.getStartPosition(), expression);
        else return new ReturnStatement(returnToken.getStartPosition(), returnToken.getEndPosition());
    }

    // loop = "while", "(", expression, ")", block;
    private WhileStatement parseWhileStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.WHILE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            throw new SyntaxException("Expected '('", reader.getToken().getStartPosition());
        }
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
        }
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        reader.next();
        StatementBlock body = parseStatementBlock();
        if(body == null) {
            throw new SyntaxException("Expected statement block", reader.getToken().getStartPosition());
        }
        return new WhileStatement(startPosition, condition, body);
    }

    // conditional_statement = "if", "(", expression, ")", block,
    //                        {"elif", "(", expression, ")", block},
    //                        ["else", "(", expression, ")", block];
    private IfStatement parseIfStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IF) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        return parseIfPart(startPosition);
    }
    private IfStatement parseIfPart(TextPosition startPosition) throws SyntaxException, LexicalException, IOException {
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            throw new SyntaxException("Expected '('", reader.getToken().getStartPosition());
        }
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
        }
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        reader.next();
        StatementBlock body = parseStatementBlock();
        if(body == null) {
            throw new SyntaxException("Expected statement block", reader.getToken().getStartPosition());
        }
        if(reader.getType() == TokenType.ELIF) {
            TextPosition nextStartPosition = reader.getToken().getStartPosition();
            reader.next();
            return new IfStatement(startPosition, condition, body, parseIfPart(nextStartPosition));
        }
        if(reader.getType() == TokenType.ELSE) {
            reader.next();
            StatementBlock elseBlock = parseStatementBlock();
            if(elseBlock == null) {
                throw new SyntaxException("Expected statement block", reader.getToken().getStartPosition());
            }
            return new IfStatement(startPosition, condition, body, elseBlock);
        }
        throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
    }
    // match_statement = "match", "(", expression, ")", "{", {case_statement}, "}";
    private MatchStatement parseMatchStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.MATCH) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            throw new SyntaxException("Expected '('", reader.getToken().getStartPosition());
        }
        reader.next();
        Expression expression = parseExpression();
        if(expression == null) {
            throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
        }
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        reader.next();
        if(reader.getType() != TokenType.OPEN_BRACE) {
            throw new SyntaxException("Expected '{'", reader.getToken().getStartPosition());
        }
        reader.next();
        List<MatchCase> cases = new ArrayList<>();
        MatchCase matchCase;
        do {
            if(reader.getType() == TokenType.CLOSE_BRACE) {
                break;
            }
            matchCase = parseCaseStatement();
            if(matchCase == null) {
                throw new SyntaxException("Expected case statement", reader.getToken().getStartPosition());
            }
            cases.add(matchCase);
        } while(reader.getType() == TokenType.CASE);
        if(reader.getType() != TokenType.CLOSE_BRACE) {
            throw new SyntaxException("Expected '}'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new MatchStatement(startPosition, endPosition, expression, cases);
    }

    // case_statement = "case", "(", type, identifier, ")", block;
    private MatchCase parseCaseStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.CASE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            throw new SyntaxException("Expected '('", reader.getToken().getStartPosition());
        }
        reader.next();
        if(reader.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Expected type identifier", reader.getToken().getStartPosition());
        }
        String pattern = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        if(reader.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Expected variable name", reader.getToken().getStartPosition());
        }
        String variableName = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        reader.next();
        StatementBlock body = parseStatementBlock();
        if(body == null) {
            throw new SyntaxException("Expected statement block", reader.getToken().getStartPosition());
        }
        return new MatchCase(startPosition, pattern, variableName, body);
    }

    // function_call = identifier, "(", [expression, {",", expression}], ")";
    private Expression parseFunctionCallOrVariableReference() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            return new VariableReference(identifierToken.getStartPosition(), identifierToken.getEndPosition(), identifierToken.getValue());
        }
        List<Expression> parameters = new ArrayList<>();
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_PARENTHESIS) {
                break;
            }
            Expression expression = parseExpression();
            if(expression == null) {
                throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
            }
            parameters.add(expression);
        } while(reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new FunctionCall(identifierToken.getStartPosition(), endPosition, identifierToken.getValue(), parameters);
    }

    // expression = and_term, {"or", and_term};
    public Expression parseExpression() throws LexicalException, IOException, SyntaxException {
        Expression left = parseAndTerm();
        if(left == null) return null;
        while(reader.getType() == TokenType.OR) {
            reader.next();
            left = new OrExpression(left, parseAndTerm());
        }
        return left;
    }

    // and_term = comparison_expression, {"and", comparison_expression};
    private Expression parseAndTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseComparisonExpression();
        if(left == null) return null;
        while(reader.getType() == TokenType.AND) {
            reader.next();
            left = new AndExpression(left, parseComparisonExpression());
        }
        return left;
    }

    // comparison_expression = additive_term, [(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term];
    private Expression parseComparisonExpression() throws LexicalException, IOException, SyntaxException {
        List<TokenType> comparisonOperators = List.of(TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.EQUALS, TokenType.NOT_EQUALS);
        Expression left = parseAdditiveTerm();
        if(left == null) return null;
        if(comparisonOperators.contains(reader.getType())) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case GREATER -> new GreaterExpression(left, parseAdditiveTerm());
                case LESS -> new LessExpression(left, parseAdditiveTerm());
                case GREATER_EQUALS -> new GreaterEqualExpression(left, parseAdditiveTerm());
                case LESS_EQUALS -> new LessEqualExpression(left, parseAdditiveTerm());
                case EQUALS -> new EqualExpression(left, parseAdditiveTerm());
                case NOT_EQUALS -> new NotEqualExpression(left, parseAdditiveTerm());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        if(comparisonOperators.contains(reader.getType())) {
            throw new SyntaxException("Multiple comparison expressions are not allowed", reader.getToken().getStartPosition());
        }
        return left;
    }

    // additive_term = term, {("+" | "-"), term};
    private Expression parseAdditiveTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseTerm();
        if(left == null) return null;
        while(reader.getType() == TokenType.PLUS || reader.getType() == TokenType.MINUS) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case PLUS -> new AddExpression(left, parseTerm());
                case MINUS -> new SubtractExpression(left, parseTerm());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        return left;
    }

    // term = factor, {("*" | "/"), factor};
    private Expression parseTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseFactor();
        if(left == null) return null;
        while(reader.getType() == TokenType.MULTIPLY || reader.getType() == TokenType.DIVIDE) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case MULTIPLY -> new MultiplyExpression(left, parseFactor());
                case DIVIDE -> new DivideExpression(left, parseFactor());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        return left;
    }
    // factor = ["-" | "!"], (dot_expression | factor);
    private Expression parseFactor() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() == TokenType.MINUS) {
            reader.next();
            Expression factor = parseFactor();
            return factor != null ? new NegationExpression(parseFactor()) : null;
        }
        if(reader.getType() == TokenType.NOT) {
            reader.next();
            Expression factor = parseFactor();
            return factor != null ? new NotExpression(parseFactor()) : null;
        }
        return parseDotExpression();
    }

    // dot_expression = elementary_expression, {".", elementary_expression};
    private Expression parseDotExpression() throws LexicalException, IOException, SyntaxException {
        Expression left = parseElementaryExpression();
        if(left == null) return null;
        if(left instanceof StringLiteral || left instanceof IntegerLiteral || left instanceof FloatLiteral || left instanceof BooleanLiteral) {
            if(reader.getType() == TokenType.DOT) {
                throw new SyntaxException("Only structs can have fields", reader.getToken().getStartPosition());
            }
            return left;
        }
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            Expression right = parseElementaryExpression();
            if(!(right instanceof VariableReference)) {
                throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
            }
            left = new DotExpression(right.getEndPosition(), left, ((VariableReference) right).getName());
        }
        return left;
    }

    // elementary_expresssion = identifier
    //                       | "(", expression, ")"
    //                       | literal
    //                       | function_call;
    private Expression parseElementaryExpression() throws LexicalException, IOException, SyntaxException {
        Expression expression;
        if(reader.getType() == TokenType.OPEN_PARENTHESIS) {
            reader.next();
            expression = parseExpression();
            if (reader.getType() != TokenType.CLOSE_PARENTHESIS) {
                throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
            }
            reader.next();
            return expression;
        }
        if((expression = parseFunctionCallOrVariableReference()) != null) {
            return expression;
        }
        if((expression = parseLiteral()) != null) {
            return expression;
        }
        return null;
    }

    // literal = int_literal | float_literal | string_literal | bool_literal;
    private Literal parseLiteral() throws SyntaxException, LexicalException, IOException {
        StructLiteral structLiteral = parseStructLiteral();
        if(structLiteral != null) {
            return structLiteral;
        }
        switch(reader.getType()) {
            case INTEGER_LITERAL -> {
                IntegerLiteralToken token = (IntegerLiteralToken) reader.getToken();
                reader.next();
                return new IntegerLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case FLOAT_LITERAL -> {
                FloatLiteralToken token = (FloatLiteralToken) reader.getToken();
                reader.next();
                return new FloatLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case STRING_LITERAL -> {
                StringLiteralToken token = (StringLiteralToken) reader.getToken();
                reader.next();
                return new StringLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case TRUE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), reader.getToken().getEndPosition(), true);
            }
            case FALSE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), reader.getToken().getEndPosition(), false);
            }
            default -> {return null;}
        }
    }
    // struct_literal = "{", {struct_literal_member}, "}";
    private StructLiteral parseStructLiteral() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.OPEN_BRACE) {
            return null;
        }
        Map<String, Literal> fields = new SimpleMap<>();
        TextPosition startPosition = reader.getToken().getStartPosition();
        IdentifierToken identifierToken;
        do {
            reader.next();
            if(reader.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
            }
            identifierToken = (IdentifierToken) reader.getToken();
            reader.next();
            if(reader.getType() != TokenType.COLON) {
                throw new SyntaxException("Expected ':'", reader.getToken().getStartPosition());
            }
            reader.next();
            fields.put(identifierToken.getValue(), parseLiteral());
        } while (reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_BRACE) {
            throw new SyntaxException("Expected '}'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StructLiteral(startPosition, endPosition, fields);
    }
}
