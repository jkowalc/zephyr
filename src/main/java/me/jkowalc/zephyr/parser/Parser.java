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
import me.jkowalc.zephyr.exception.ZephyrInternalException;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.*;
import me.jkowalc.zephyr.lexer.LexerInterface;
import me.jkowalc.zephyr.util.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final TokenReader reader;
    public Parser(LexerInterface lexer) throws LexicalException, IOException {
        this.reader = new TokenReader(lexer);
    }

    private void mustBe(TokenType type, String message) throws MissingTokenException {
        if(reader.getType() != type) {
            throw new MissingTokenException(message, reader.getToken().getStartPosition());
        }
    }

    private StatementBlock mustBeBlock(String message) throws LexicalException, SyntaxException, IOException {
        StatementBlock block = parseStatementBlock();
        if(block == null) {
            throw new SyntaxException(message, reader.getToken().getStartPosition());
        }
        return block;
    }

    // program = {struct_definition | union_definition | function_definition };
    public Program parseProgram() throws LexicalException, SyntaxException, IOException {
        List<TypeDefinition> types = new ArrayList<>();
        List<FunctionDefinition> functions = new ArrayList<>();
        boolean isEnd = false;
        while(!isEnd) {
            StructDefinition structDefinition = parseStructDefinition();
            if (structDefinition != null) {
                types.add(structDefinition);
                continue;
            }
            UnionDefinition unionDefinition = parseUnionDefinition();
            if (unionDefinition != null) {
                types.add(unionDefinition);
                continue;
            }
            FunctionDefinition functionDefinition = parseFunctionDefinition();
            if (functionDefinition != null) {
                functions.add(functionDefinition);
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

    // struct_members = "{", [struct_member, {",", struct_member}, [","]], "}";
    private List<StructDefinitionMember> parseStructMembers() throws LexicalException, SyntaxException, IOException {
        if(reader.getType() != TokenType.OPEN_BRACE) {
            return null;
        }
        reader.next();
        if(reader.getType() == TokenType.CLOSE_BRACE) {
            reader.next();
            return new ArrayList<>();
        }
        List<StructDefinitionMember> members = new ArrayList<>();
        StructDefinitionMember member = parseStructDefinitionMember();
        if(member == null) {
            throw new SyntaxException("Expected struct member", reader.getToken().getStartPosition());
        }
        members.add(member);
        while(member != null && reader.getType() == TokenType.COMMA) {
            reader.next();
            if((member = parseStructDefinitionMember()) != null) {
                members.add(member);
            }
        }
        if(reader.getType() == TokenType.COMMA) {
            reader.next();
        }
        mustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        reader.next();
        return members;
    }

    // struct_definition = "struct", identifier, struct_members;
    private StructDefinition parseStructDefinition() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.STRUCT) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected identifier");
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        List<StructDefinitionMember> members = parseStructMembers();
        if(members == null) {
            throw new SyntaxException("Expected struct members", reader.getToken().getStartPosition());
        }
        return new StructDefinition(startPosition, identifierToken.getValue(), members);
    }

    // struct_member = identifier, ":", type;
    private StructDefinitionMember parseStructDefinitionMember() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IDENTIFIER) {
            return null;
        }
        IdentifierToken nameToken = (IdentifierToken) reader.getToken();
        reader.next();
        mustBe(TokenType.COLON, "Expected ':'");
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected identifier");
        String typeName = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        return new StructDefinitionMember(nameToken.getStartPosition(), nameToken.getValue(), typeName);
    }

    // union_members = "{", type, {",", type}, [","], "}";
    private List<String> parseUnionMembers() throws SyntaxException, LexicalException, IOException {
        if(reader.getType() != TokenType.OPEN_BRACE) {
            return null;
        }
        reader.next();
        if(reader.getType() == TokenType.CLOSE_BRACE) {
            reader.next();
            return new ArrayList<>();
        }
        List<String> typeNames = new ArrayList<>();
        mustBe(TokenType.IDENTIFIER, "Expected type identifier");
        Token shouldBeIdentifier = reader.getToken();
        reader.next();
        typeNames.add(((IdentifierToken) shouldBeIdentifier).getValue());
        while(shouldBeIdentifier.getType() == TokenType.IDENTIFIER && reader.getType() == TokenType.COMMA) {
            reader.next();
            shouldBeIdentifier = reader.getToken();
            if(shouldBeIdentifier.getType() == TokenType.IDENTIFIER) {
                typeNames.add(((IdentifierToken) shouldBeIdentifier).getValue());
                reader.next();
            }
        }
        if(reader.getType() == TokenType.COMMA) {
            reader.next();
        }
        mustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        reader.next();
        return typeNames;
    }
    // union_definition = "union", identifier, union_members;
    private UnionDefinition parseUnionDefinition() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.UNION) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected identifier");
        IdentifierToken identifier = (IdentifierToken) reader.getToken();
        reader.next();
        List<String> members = parseUnionMembers();
        if(members == null) {
            throw new SyntaxException("Expected union members", reader.getToken().getStartPosition());
        }
        return new UnionDefinition(startPosition, identifier.getValue(), members);
    }

    private VariableDefinition mustBeParameter() throws LexicalException, SyntaxException, IOException {
        VariableDefinition parameter = parseVariableDefinition(null);
        if(parameter == null) {
            throw new SyntaxException("Expected parameter", reader.getToken().getStartPosition());
        }
        return parameter;
    }
    // parameters = "(", [parameter_definition, {",", parameter_definition}], ")";
    // parameter_definition = type, parameter_modifier, identifier, ["=", literal];
    // parameter_modifier = "mut", "ref", "mref";
    private List<VariableDefinition> parseParameters() throws LexicalException, SyntaxException, IOException {
        mustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        if(reader.getType() == TokenType.CLOSE_PARENTHESIS) {
            reader.next();
            return new ArrayList<>();
        }
        List<VariableDefinition> parameters = new ArrayList<>();
        parameters.add(mustBeParameter());
        while(reader.getType() == TokenType.COMMA) {
            reader.next();
            parameters.add(mustBeParameter());
        }
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        return parameters;
    }

    // function_definition = identifier, parameters, ["->", type], block;
    private FunctionDefinition parseFunctionDefinition() throws SyntaxException, LexicalException, IOException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        List<VariableDefinition> parameters = parseParameters();
        String returnType = parseFunctionReturnType();
        StatementBlock body = mustBeBlock("Expected function body");
        return new FunctionDefinition(identifierToken.getStartPosition(), identifierToken.getValue(), parameters, body, returnType);
    }

    private String parseFunctionReturnType() throws LexicalException, IOException, MissingTokenException {
        if(reader.getType() != TokenType.ARROW) return null;
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected return type");
        IdentifierToken returnTypeToken = (IdentifierToken) reader.getToken();
        reader.next();
        return returnTypeToken.getValue();
    }

    // block = "{", {statement, [";"]}, "}";
    public StatementBlock parseStatementBlock() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.OPEN_BRACE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        List<Statement> statements = new ArrayList<>();
        reader.next();
        Statement statement;
        do {
            if((statement = parseStatement()) != null) {
                statements.add(statement);
            }
            if(reader.getType() == TokenType.SEMICOLON) {
                reader.next();
                if(reader.getType() == TokenType.SEMICOLON) {
                    throw new SyntaxException("Missing statement before ';'", reader.getToken().getStartPosition());
                }
            }

        } while(statement != null);
        mustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        reader.next();
        return new StatementBlock(startPosition, statements);
    }

    // statement = assignment
    //          | variable_declaration
    //          | return_statement
    //          | loop
    //          | conditional_statement
    //          | match_statement
    //          | function_call_statement
    //          | block;
    private Statement parseStatement() throws LexicalException, SyntaxException, IOException {
        Statement statement;
        if ((statement = parseReturnStatement()) != null) return statement;
        if ((statement = parseWhileStatement()) != null) return statement;
        if ((statement = parseIfStatement()) != null) return statement;
        if ((statement = parseMatchStatement()) != null) return statement;
        if ((statement = parseStatementBlock()) != null) return statement;
        if ((statement = parseIdentifierStatement()) != null) return statement;
        return null;
    }

    // assignment = identifier, {".", identifier}, "=", expression;
    private AssignmentStatement parseAssignment(IdentifierToken identifierToken) throws LexicalException, IOException, SyntaxException {
        Assignable left = new VariableReference(identifierToken.getStartPosition(), identifierToken.getValue());
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            mustBe(TokenType.IDENTIFIER, "Expected identifier");
            left = new DotExpression((Expression) left, ((IdentifierToken) reader.getToken()).getValue());
            reader.next();
        }
        mustBe(TokenType.ASSIGNMENT, "Expected '='");
        reader.next();
        Expression right = parseExpression();
        if(right == null) {
            throw new SyntaxException("Expected expression after assignment", reader.getToken().getStartPosition());
        }
        return new AssignmentStatement(left, right);
    }

    // Parse any statement that starts with an identifier. This can be an assignment, variable declaration or function call.
    private Statement parseIdentifierStatement() throws LexicalException, SyntaxException, IOException {
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
    private VariableDefinition parseVariableDefinition(IdentifierToken typeNameIdentifier) throws LexicalException, IOException, SyntaxException {
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
            return new VariableDefinition(typeToken.getStartPosition(), nameToken.getValue(), typeToken.getValue(), isMutable, isReference, null);
        }
        reader.next();
        Expression defaultValue = parseExpression();
        if(defaultValue == null) {
            throw new SyntaxException("Expected variable default value", reader.getToken().getStartPosition());
        }
        return new VariableDefinition(typeToken.getStartPosition(), nameToken.getValue(), typeToken.getValue(), isMutable, isReference, defaultValue);
    }

    // return_statement = "return", [expression], ";";
    private ReturnStatement parseReturnStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.RETURN) return null;
        Token returnToken = reader.getToken();
        reader.next();
        Expression expression = parseExpression();
        if(expression != null) return new ReturnStatement(returnToken.getStartPosition(), expression);
        return new ReturnStatement(returnToken.getStartPosition(), null);
    }

    // loop = "while", "(", expression, ")", block;
    private WhileStatement parseWhileStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.WHILE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        mustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected while condition", reader.getToken().getStartPosition());
        }
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = mustBeBlock("Expected while body");
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
        mustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression condition = parseExpression();
        if(condition == null) {
            throw new SyntaxException("Expected if condition", reader.getToken().getStartPosition());
        }
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = mustBeBlock("Expected if body");
        if(reader.getType() == TokenType.ELIF) {
            TextPosition nextStartPosition = reader.getToken().getStartPosition();
            reader.next();
            return new IfStatement(startPosition, condition, body, parseIfPart(nextStartPosition));
        }
        if(reader.getType() == TokenType.ELSE) {
            reader.next();
            StatementBlock elseBlock = mustBeBlock("Expected else body");
            return new IfStatement(startPosition, condition, body, elseBlock);
        }
        return new IfStatement(startPosition, condition, body, null);
    }
    // match_statement = "match", "(", expression, ")", "{", {case_statement}, "}";
    private MatchStatement parseMatchStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.MATCH) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        mustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        Expression expression = parseExpression();
        if(expression == null) {
            throw new SyntaxException("Expected match expression", reader.getToken().getStartPosition());
        }
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        mustBe(TokenType.OPEN_BRACE, "Expected '{'");
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
        mustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        return new MatchStatement(startPosition, expression, cases);
    }

    // case_statement = "case", "(", type, identifier, ")", block;
    private MatchCase parseCaseStatement() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.CASE) return null;
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        mustBe(TokenType.OPEN_PARENTHESIS, "Expected '('");
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected type identifier");
        String pattern = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        mustBe(TokenType.IDENTIFIER, "Expected variable name");
        String variableName = ((IdentifierToken) reader.getToken()).getValue();
        reader.next();
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        StatementBlock body = mustBeBlock("Expected case body");
        return new MatchCase(startPosition, pattern, variableName, body);
    }

    private Expression mustBeExpression(String message) throws LexicalException, SyntaxException, IOException {
        Expression expression = parseExpression();
        if(expression == null) {
            throw new SyntaxException(message, reader.getToken().getStartPosition());
        }
        return expression;
    }

    // arguments = "(", [expression, {",", expression}], ")";
    private List<Expression> parseArguments() throws LexicalException, SyntaxException, IOException {
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) return null;
        reader.next();
        if(reader.getType() == TokenType.CLOSE_PARENTHESIS) {
            reader.next();
            return new ArrayList<>();
        }
        List<Expression> arguments = new ArrayList<>();
        arguments.add(mustBeExpression("Expected argument"));
        while(reader.getType() == TokenType.COMMA) {
            reader.next();
            arguments.add(mustBeExpression("Expected argument"));
        }
        mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
        reader.next();
        return arguments;
    }

    // function_call = identifier, arguments;
    private FunctionCall parseFunctionCall(IdentifierToken identifierToken) throws LexicalException, IOException, SyntaxException {
        List<Expression> arguments = parseArguments();
        if(arguments == null) return null;
        return new FunctionCall(identifierToken.getStartPosition(), identifierToken.getValue(), arguments);
    }

    // function_call = identifier, "(", [expression, {",", expression}], ")";
    // variable reference is just an identifier
    private Expression parseFunctionCallOrVariableReference() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        FunctionCall functionCall = parseFunctionCall(identifierToken);
        if(functionCall == null) {
            return new VariableReference(identifierToken.getStartPosition(), identifierToken.getValue());
        }
        return functionCall;
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
                default -> throw new ZephyrInternalException();
            };
        }
        if(comparisonOperators.contains(reader.getType())) {
            throw new MultipleComparisonException("Multiple comparison expressions are not allowed", reader.getToken().getStartPosition());
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
                default -> throw new ZephyrInternalException();
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
                default -> throw new ZephyrInternalException();
            };
        }
        return left;
    }
    // factor = ["-" | "!"], (dot_expression | factor);
    private Expression parseFactor() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() == TokenType.MINUS) {
            TextPosition minusPosition = reader.getToken().getStartPosition();
            reader.next();
            Expression factor = parseFactor();
            if(factor == null) {
                throw new SyntaxException("Expected expression after '-'", reader.getToken().getStartPosition());
            }
            return new NegationExpression(minusPosition, factor);
        }
        if(reader.getType() == TokenType.NOT) {
            TextPosition notPosition = reader.getToken().getStartPosition();
            reader.next();
            Expression factor = parseFactor();
            if(factor == null) {
                throw new SyntaxException("Expected expression after '!'", reader.getToken().getStartPosition());
            }
            return new NotExpression(notPosition, factor);
        }
        return parseDotExpression();
    }

    // dot_expression = elementary_expression, {".", elementary_expression};
    private Expression parseDotExpression() throws LexicalException, IOException, SyntaxException {
        Expression left = parseElementaryExpression();
        if(left == null) return null;
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            Expression right = parseElementaryExpression();
            if(right == null) {
                throw new SyntaxException("Expected expression after '.'", reader.getToken().getStartPosition());
            }
            left = new DotExpression(left, ((VariableReference) right).getName());
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
            mustBe(TokenType.CLOSE_PARENTHESIS, "Expected ')'");
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
                return new IntegerLiteral(token.getStartPosition(), token.getValue());
            }
            case FLOAT_LITERAL -> {
                FloatLiteralToken token = (FloatLiteralToken) reader.getToken();
                reader.next();
                return new FloatLiteral(token.getStartPosition(), token.getValue());
            }
            case STRING_LITERAL -> {
                StringLiteralToken token = (StringLiteralToken) reader.getToken();
                reader.next();
                return new StringLiteral(token.getStartPosition(), token.getValue());
            }
            case TRUE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), true);
            }
            case FALSE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), false);
            }
            default -> {return null;}
        }
    }
    // struct_literal = "{", [struct_literal_member, {",", struct_literal_member}], "}";
    private StructLiteral parseStructLiteral() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.OPEN_BRACE) {
            return null;
        }
        List<StructLiteralMember> fields = new ArrayList<>();
        TextPosition startPosition = reader.getToken().getStartPosition();
        reader.next();
        if(reader.getType().equals(TokenType.CLOSE_BRACE)) {
            reader.next();
            return new StructLiteral(startPosition, List.of());
        }
        StructLiteralMember field = parseStructLiteralMember();
        if(field == null) throw new SyntaxException("Expected struct literal member", reader.getToken().getStartPosition());
        fields.add(field);
        while(reader.getType() == TokenType.COMMA) {
            reader.next();
            field = parseStructLiteralMember();
            if(field == null) throw new SyntaxException("Expected struct literal member", reader.getToken().getStartPosition());
            fields.add(field);
        }
        mustBe(TokenType.CLOSE_BRACE, "Expected '}'");
        reader.next();
        return new StructLiteral(startPosition, fields);
    }
    // struct_literal_member = identifier, ":", literal;
    private StructLiteralMember parseStructLiteralMember() throws SyntaxException, LexicalException, IOException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        mustBe(TokenType.COLON, "Expected ':'");
        reader.next();
        Literal literal = parseLiteral();
        if(literal == null) {
            throw new MissingLiteralException(reader.getToken().getStartPosition());
        }
        return new StructLiteralMember(identifierToken.getStartPosition(), identifierToken.getValue(), literal);
    }
}
