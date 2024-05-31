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
import me.jkowalc.zephyr.exception.ParserInternalException;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.*;
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

    private void MustBe(TokenType type, String message) throws MissingTokenException {
        if(reader.getType() != type) {
            throw new MissingTokenException(message, reader.getToken().getStartPosition());
        }
    }

    private StatementBlock MustBeBlock(String message) throws LexicalException, SyntaxException, ParserInternalException, IOException {
        StatementBlock block = parseStatementBlock();
        if(block == null) {
            throw new SyntaxException(message, reader.getToken().getStartPosition());
        }
        return block;
    }

    // program = {struct_definition | union_definition | function_definition };
    public Program parseProgram() throws LexicalException, SyntaxException, IOException, ParserInternalException {
        HashMap<String, TypeDefinition> types = new HashMap<>();
        HashMap<String, FunctionDefinition> functions = new HashMap<>();
        boolean isEnd = false;
        while(!isEnd) {
            StructDefinition structDefinition = parseStructDefinition();
            if (structDefinition != null) {
                if(types.containsKey(structDefinition.getName())) {
                    throw new MultipleDefinitionException("Type with name " + structDefinition.getName() + " already defined" +
                            " (previous definition at " + types.get(structDefinition.getName()).getStartPosition() + ")", structDefinition.getStartPosition());
                }
                types.put(structDefinition.getName(), structDefinition);
                continue;
            }
            UnionDefinition unionDefinition = parseUnionDefinition();
            if (unionDefinition != null) {
                if(types.containsKey(unionDefinition.getName())) {
                    throw new MultipleDefinitionException("Type with name " + unionDefinition.getName() + " already defined" +
                            " (previous definition at " + types.get(unionDefinition.getName()).getStartPosition() + ")", unionDefinition.getStartPosition());
                }
                types.put(unionDefinition.getName(), unionDefinition);
                continue;
            }
            FunctionDefinition functionDefinition = parseFunctionDefinition();
            if (functionDefinition != null) {
                if(functions.containsKey(functionDefinition.getName())) {
                    throw new MultipleDefinitionException("Function with name " + functionDefinition.getName() + " already defined" +
                            " (previous definition at " + functions.get(functionDefinition.getName()).getStartPosition() + ")", functionDefinition.getStartPosition());
                }
                functions.put(functionDefinition.getName(), functionDefinition);
                continue;
            }
            // EOF token is not required
            isEnd = true;
            reader.next();
        }
        return new Program(functions, types);
    }

    public Token nextNotParsed() {
        return reader.getToken();
    }

    // struct_definition = "struct", identifier, "{", struct_members, "}";
    private StructDefinition parseStructDefinition() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.STRUCT) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        MustBe(TokenType.IDENTIFIER, "Expected identifier");
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        MustBe(TokenType.OPEN_BRACE, "Expected '{'");
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
        MustBe(TokenType.CLOSE_BRACE, "Expected '}'");
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
        MustBe(TokenType.IDENTIFIER, "Expected identifier");
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
        MustBe(TokenType.IDENTIFIER, "Expected identifier");
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        MustBe(TokenType.OPEN_BRACE, "Expected '{'");
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_BRACE) {
                break;
            }
            MustBe(TokenType.IDENTIFIER, "Expected type identifier");
            typeNames.add(((IdentifierToken) reader.getToken()).getValue());
            reader.next();
        } while(reader.getType() == TokenType.COMMA);
        MustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new UnionDefinition(startPosition, endPosition, identifierToken.getValue(), typeNames);
    }

    private FunctionDefinition parseFunctionDefinition() throws SyntaxException, LexicalException, IOException, ParserInternalException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        MustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
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
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        String returnType = null;
        if(reader.getType() == TokenType.ARROW) {
            reader.next();
            MustBe(TokenType.IDENTIFIER, "Expected return type");
            IdentifierToken returnTypeToken = (IdentifierToken) reader.getToken();
            returnType = returnTypeToken.getValue();
            reader.next();
        }
        StatementBlock body = MustBeBlock("Expected function body");
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
    public StatementBlock parseStatementBlock() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.OPEN_BRACE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        List<Statement> statements = new ArrayList<>();
        boolean firstStatement = true;
        do {
            Statement statement;
            if ((statement = parseReturnStatement()) != null){
                statements.add(statement);
            }
            if (statement == null && (statement = parseWhileStatement()) != null) {
                statements.add(statement);
            }
            if (statement == null && (statement = parseIfStatement()) != null) {
                statements.add(statement);
            }
            if (statement == null && (statement = parseMatchStatement()) != null) {
                statements.add(statement);
            }
            if (statement == null && (statement = parseStatementBlock()) != null) {
                statements.add(statement);
            }
            if (statement == null && (statement = parseIdentifierStatement()) != null) {
                statements.add(statement);
            }
            if(!firstStatement && statement == null) {
                throw new SyntaxException("Statement not recognized", reader.getToken().getStartPosition());
            }
            if(reader.getType() == TokenType.SEMICOLON) {
                reader.next();
            }
            firstStatement = false;
        } while(reader.getType() != TokenType.CLOSE_BRACE);
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StatementBlock(startPosition, endPosition, statements);
    }

    // assignment = identifier, {".", identifier}, "=", expression;
    private AssignmentStatement parseAssignment(IdentifierToken identifierToken) throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Assignable left = new VariableReference(identifierToken.getStartPosition(), identifierToken.getEndPosition(), identifierToken.getValue());
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            MustBe(TokenType.IDENTIFIER, "Expected identifier");
            left = new DotExpression(reader.getToken().getEndPosition(), (Expression) left, ((IdentifierToken) reader.getToken()).getValue());
            reader.next();
        }
        MustBe(TokenType.ASSIGNMENT, "Expected '='");
        reader.next();
        Expression right = parseExpression();
        if(right == null) {
            throw new SyntaxException("Expected expression after assignment", reader.getToken().getStartPosition());
        }
        return new AssignmentStatement(left, right);
    }

    // Parse any statement that starts with an identifier. This can be an assignment, variable declaration or function call.
    private Statement parseIdentifierStatement() throws LexicalException, SyntaxException, IOException, ParserInternalException {
        if(reader.getType() != TokenType.IDENTIFIER) {
            return null;
        }
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        Statement statement;
        if((statement = parseVariableDefinition(identifierToken)) != null) {
            VariableDefinition variableDefinition = (VariableDefinition) statement;
            if(variableDefinition.getDefaultValue() == null) {
                throw new InvalidModifierException("All variables must be initialized", variableDefinition.getStartPosition());
            }
            if(variableDefinition.isReference()) {
                throw new InvalidModifierException("The ref or mref keyword is not valid in function parameters", variableDefinition.getStartPosition());
            }
            return variableDefinition;
        }
        if((statement = parseFunctionCall(identifierToken)) != null) return statement;
        return parseAssignment(identifierToken);
    }

    private final static List<TokenType> modifierTokenTypes = List.of(TokenType.MUT, TokenType.REF, TokenType.MREF);
    // variable_declaration = type, variable_modifier, identifier, "=", expression, ";";
    private VariableDefinition parseVariableDefinition(IdentifierToken typeNameIdentifier) throws LexicalException, IOException, SyntaxException, ParserInternalException {
        IdentifierToken typeToken;
        if(typeNameIdentifier == null) {
            if(reader.getType() != TokenType.IDENTIFIER) {
                return null;
            }
            typeToken = (IdentifierToken) reader.getToken();
            reader.next();
        } else {
            // type is already parsed
            if(!(modifierTokenTypes.contains(reader.getType()) || reader.getType() == TokenType.IDENTIFIER)) return null;
            typeToken = typeNameIdentifier;
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
            return new VariableDefinition(typeToken.getStartPosition(), nameToken.getEndPosition(), nameToken.getValue(), typeToken.getValue(), isMutable, isReference, null);
        }
        reader.next();
        Expression defaultValue = parseExpression();
        if(defaultValue == null) {
            throw new SyntaxException("Expected variable default value", reader.getToken().getStartPosition());
        }
        return new VariableDefinition(typeToken.getStartPosition(), defaultValue.getEndPosition(), nameToken.getValue(), typeToken.getValue(), isMutable, isReference, defaultValue);
    }

    // return_statement = "return", [expression], ";";
    private ReturnStatement parseReturnStatement() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.RETURN) return null;
        Token returnToken = reader.getToken();
        reader.next();
        Expression expression = parseExpression();
        if(expression != null) return new ReturnStatement(returnToken.getStartPosition(), expression);
        return new ReturnStatement(returnToken.getStartPosition(), returnToken.getEndPosition());
    }

    // loop = "while", "(", expression, ")", block;
    private WhileStatement parseWhileStatement() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.WHILE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        MustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected while condition", reader.getToken().getStartPosition());
        }
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = MustBeBlock("Expected while body");
        return new WhileStatement(startPosition, condition, body);
    }

    // conditional_statement = "if", "(", expression, ")", block,
    //                        {"elif", "(", expression, ")", block},
    //                        ["else", "(", expression, ")", block];
    private IfStatement parseIfStatement() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.IF) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        return parseIfPart(startPosition);
    }
    private IfStatement parseIfPart(TextPosition startPosition) throws SyntaxException, LexicalException, IOException, ParserInternalException {
        MustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected if condition", reader.getToken().getStartPosition());
        }
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = MustBeBlock("Expected if body");
        if(reader.getType() == TokenType.ELIF) {
            TextPosition nextStartPosition = reader.getToken().getStartPosition();
            reader.next();
            return new IfStatement(startPosition, condition, body, parseIfPart(nextStartPosition));
        }
        if(reader.getType() == TokenType.ELSE) {
            reader.next();
            StatementBlock elseBlock = MustBeBlock("Expected else body");
            return new IfStatement(startPosition, condition, body, elseBlock);
        }
        return new IfStatement(startPosition, condition, body, null);
    }
    // match_statement = "match", "(", expression, ")", "{", {case_statement}, "}";
    private MatchStatement parseMatchStatement() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.MATCH) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        MustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression expression = parseExpression();
        if(expression == null) {
            throw new SyntaxException("Expected match expression", reader.getToken().getStartPosition());
        }
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        MustBe(TokenType.OPEN_BRACE, "Expected '{'");
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
        MustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        TextPosition endPosition = reader.getToken().getEndPosition();
        return new MatchStatement(startPosition, endPosition, expression, cases);
    }

    // case_statement = "case", "(", type, identifier, ")", block;
    private MatchCase parseCaseStatement() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.CASE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        MustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        MustBe(TokenType.IDENTIFIER, "Expected type identifier");
        String pattern = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        MustBe(TokenType.IDENTIFIER, "Expected variable name");
        String variableName = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = MustBeBlock("Expected case body");
        return new MatchCase(startPosition, pattern, variableName, body);
    }

    // function_call = identifier, "(", [expression, {",", expression}], ")";
    private FunctionCall parseFunctionCall(IdentifierToken identifierToken) throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            return null;
        }
        List<Expression> arguments = new ArrayList<>();
        do {
            reader.next();
            if(reader.getType() == TokenType.CLOSE_PARENTHESIS) {
                break;
            }
            Expression expression = parseExpression();
            if(expression == null) {
                throw new SyntaxException("Expected argument", reader.getToken().getStartPosition());
            }
            arguments.add(expression);
        } while(reader.getType() == TokenType.COMMA);
        MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new FunctionCall(identifierToken.getStartPosition(), endPosition, identifierToken.getValue(), arguments);
    }

    // function_call = identifier, "(", [expression, {",", expression}], ")";
    // variable reference is just an identifier
    private Expression parseFunctionCallOrVariableReference() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        FunctionCall functionCall = parseFunctionCall(identifierToken);
        if(functionCall == null) {
            return new VariableReference(identifierToken.getStartPosition(), identifierToken.getEndPosition(), identifierToken.getValue());
        }
        return functionCall;
    }

    // expression = and_term, {"or", and_term};
    public Expression parseExpression() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression left = parseAndTerm();
        if(left == null) return null;
        while(reader.getType() == TokenType.OR) {
            reader.next();
            left = new OrExpression(left, parseAndTerm());
        }
        return left;
    }

    // and_term = comparison_expression, {"and", comparison_expression};
    private Expression parseAndTerm() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression left = parseComparisonExpression();
        if(left == null) return null;
        while(reader.getType() == TokenType.AND) {
            reader.next();
            left = new AndExpression(left, parseComparisonExpression());
        }
        return left;
    }

    // comparison_expression = additive_term, [(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term];
    private Expression parseComparisonExpression() throws LexicalException, IOException, SyntaxException, ParserInternalException {
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
                default -> throw new ParserInternalException();
            };
        }
        if(comparisonOperators.contains(reader.getType())) {
            throw new MultipleComparisonException("Multiple comparison expressions are not allowed", reader.getToken().getStartPosition());
        }
        return left;
    }

    // additive_term = term, {("+" | "-"), term};
    private Expression parseAdditiveTerm() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression left = parseTerm();
        if(left == null) return null;
        while(reader.getType() == TokenType.PLUS || reader.getType() == TokenType.MINUS) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case PLUS -> new AddExpression(left, parseTerm());
                case MINUS -> new SubtractExpression(left, parseTerm());
                default -> throw new ParserInternalException();
            };
        }
        return left;
    }

    // term = factor, {("*" | "/"), factor};
    private Expression parseTerm() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression left = parseFactor();
        if(left == null) return null;
        while(reader.getType() == TokenType.MULTIPLY || reader.getType() == TokenType.DIVIDE) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case MULTIPLY -> new MultiplyExpression(left, parseFactor());
                case DIVIDE -> new DivideExpression(left, parseFactor());
                default -> throw new ParserInternalException();
            };
        }
        return left;
    }
    // factor = ["-" | "!"], (dot_expression | factor);
    private Expression parseFactor() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        if(reader.getType() == TokenType.MINUS) {
            reader.next();
            Expression factor = parseFactor();
            if(factor == null) {
                throw new SyntaxException("Expected expression after '-'", reader.getToken().getStartPosition());
            }
            return new NegationExpression(factor);
        }
        if(reader.getType() == TokenType.NOT) {
            reader.next();
            Expression factor = parseFactor();
            if(factor == null) {
                throw new SyntaxException("Expected expression after '!'", reader.getToken().getStartPosition());
            }
            return new NotExpression(factor);
        }
        return parseDotExpression();
    }

    // dot_expression = elementary_expression, {".", elementary_expression};
    private Expression parseDotExpression() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression left = parseElementaryExpression();
        if(left == null) return null;
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            Expression right = parseElementaryExpression();
            if(right == null) {
                throw new SyntaxException("Expected expression after '.'", reader.getToken().getStartPosition());
            }
            left = new DotExpression(right.getEndPosition(), left, ((VariableReference) right).getName());
        }
        return left;
    }

    // elementary_expresssion = identifier
    //                       | "(", expression, ")"
    //                       | literal
    //                       | function_call;
    private Expression parseElementaryExpression() throws LexicalException, IOException, SyntaxException, ParserInternalException {
        Expression expression;
        if(reader.getType() == TokenType.OPEN_PARENTHESIS) {
            reader.next();
            expression = parseExpression();
            MustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
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
            MustBe(TokenType.IDENTIFIER, "Expected struct member name");
            identifierToken = (IdentifierToken) reader.getToken();
            reader.next();
            MustBe(TokenType.COLON, "Expected ':'");
            reader.next();
            fields.put(identifierToken.getValue(), parseLiteral());
        } while (reader.getType() == TokenType.COMMA);
        MustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StructLiteral(startPosition, endPosition, fields);
    }
}
