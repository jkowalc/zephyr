package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.CharacterUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ASTPrinter implements ASTVisitor {
    private int indent = 0;
    private final int indentSize;
    private final OutputStream output;
    private void print(String text) {
        try {
            for (int i = 0; i < indentSize * indent; i++) {
                output.write(' ');
            }
            output.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ASTPrinter(OutputStream output, int indentSize) {
        this.output = output;
        this.indentSize = indentSize;
    }
    @Override
    public void visit(Program program) {
        print("Program from " + program.getStartPosition() + " to " + program.getEndPosition() + "\n");
        if (program.getFunctions().isEmpty()) {
            print("- Functions: <empty>\n");
        } else {
            print("- Functions:\n");
            indent++;
            for (FunctionDefinition functionDefinition : program.getFunctions().values()) {
                functionDefinition.accept(this);
            }
            indent--;
        }
        if (program.getTypes().isEmpty()) {
            print("- Types: <empty>\n");
            return;
        }
        print("- Types\n");
        indent++;
        for (TypeDefinition typeDefinition : program.getTypes().values()) {
            typeDefinition.accept(this);
        }
        indent--;
    }

    @Override
    public void visit(FunctionCall functionCall) {
        print("FunctionCall(name=\"" + CharacterUtil.getRepresentation(functionCall.getName())
                + "\") from " + functionCall.getStartPosition() + " to " + functionCall.getEndPosition() + "\n");
        if (functionCall.getParameters().isEmpty()) {
            print("- Parameters: <empty>\n");
            return;
        }
        indent++;
        print("- Parameters:\n");
        for (Expression expression : functionCall.getParameters()) {
            expression.accept(this);
        }
        indent--;
    }

    @Override
    public void visit(VariableReference variableReference) {
        print("VariableReference(name=" + CharacterUtil.getRepresentation(variableReference.getName())
                + ") from " + variableReference.getStartPosition() + " to " + variableReference.getEndPosition() + "\n");
    }
    private void printDefaultBinaryExpression(DefaultBinaryExpression binaryExpression, String name) {
        print(name + " from " + binaryExpression.getStartPosition() + " to " + binaryExpression.getEndPosition() + "\n");
        indent++;
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
        indent--;
    }
    @Override
    public void visit(AddExpression addExpression) {
        printDefaultBinaryExpression(addExpression, "AddExpression");
    }

    @Override
    public void visit(AndExpression andExpression) {
        printDefaultBinaryExpression(andExpression, "AndExpression");
    }

    @Override
    public void visit(DivideExpression divideExpression) {
        printDefaultBinaryExpression(divideExpression, "DivideExpression");
    }

    @Override
    public void visit(DotExpression dotExpression) {

    }

    @Override
    public void visit(EqualExpression equalExpression) {
        printDefaultBinaryExpression(equalExpression, "EqualExpression");
    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) {
        printDefaultBinaryExpression(greaterEqualExpression, "GreaterEqualExpression");
    }

    @Override
    public void visit(GreaterExpression greaterExpression) {
        printDefaultBinaryExpression(greaterExpression, "GreaterExpression");
    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) {
        printDefaultBinaryExpression(lessEqualExpression, "LessEqualExpression");
    }

    @Override
    public void visit(LessExpression lessExpression) {
        printDefaultBinaryExpression(lessExpression, "LessExpression");
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) {
        printDefaultBinaryExpression(multiplyExpression, "MultiplyExpression");
    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) {
        printDefaultBinaryExpression(notEqualExpression, "NotEqualExpression");
    }

    @Override
    public void visit(OrExpression orExpression) {
        printDefaultBinaryExpression(orExpression, "OrExpression");
    }

    @Override
    public void visit(SubtractExpression subtractExpression) {
        printDefaultBinaryExpression(subtractExpression, "SubtractExpression");
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        print("BooleanLiteral(value=" + booleanLiteral + ") from " + booleanLiteral.getStartPosition() + " to " + booleanLiteral.getEndPosition() + "\n");
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        print("IntegerLiteral(value=" + integerLiteral.getValue() + ") from " + integerLiteral.getStartPosition() + " to " + integerLiteral.getEndPosition() + "\n");
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        print("FloatLiteral(value=" + floatLiteral.getValue() + ") from " + floatLiteral.getStartPosition() + " to " + floatLiteral.getEndPosition() + "\n");
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        print("StringLiteral(value=\"" + CharacterUtil.getRepresentation(stringLiteral.getValue()) + "\") from " + stringLiteral.getStartPosition() + " to " + stringLiteral.getEndPosition() + "\n");
    }

    @Override
    public void visit(StructLiteral structLiteral) {
        print("StructLiteral from " + structLiteral.getStartPosition() + " to " + structLiteral.getEndPosition() + "\n");
        indent++;
        for (Map.Entry<String, Literal> entry: structLiteral.getFields().entrySet()) {
            print("\"" + CharacterUtil.getRepresentation(entry.getKey()) + "\":\n");
            indent++;
            entry.getValue().accept(this);
            indent--;
        }
        indent--;
    }

    @Override
    public void visit(NegationExpression negationExpression) {
        print("NegationExpression from " + negationExpression.getStartPosition() + " to " + negationExpression.getEndPosition() + "\n");
        indent++;
        negationExpression.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(NotExpression notExpression) {
        print("NotExpression from " + notExpression.getStartPosition() + " to " + notExpression.getEndPosition() + "\n");
        indent++;
        notExpression.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(FunctionDefinition functionDefinition) {
        print("FunctionDefinition(name=\"" + CharacterUtil.getRepresentation(functionDefinition.getName())
                + "\") from " + functionDefinition.getStartPosition() + " to " + functionDefinition.getEndPosition() + "\n");
        indent++;
        if (functionDefinition.getParameters().isEmpty()) {
            print("- Parameters: <empty>\n");
        } else {
            print("- Parameters:\n");
            for (VariableDefinition variableDefinition : functionDefinition.getParameters()) {
                variableDefinition.accept(this);
            }
        }
        if (functionDefinition.getReturnType() == null) {
            print("- Return type: none (void)\n");
        } else {
            print("- Return type: " + functionDefinition.getReturnType() + "\n");
        }
        if (functionDefinition.getBody() == null) {
            print("- Body: <empty>\n");
        } else {
            print("- Body:\n");
            functionDefinition.getBody().accept(this);
        }
        indent--;
    }

    @Override
    public void visit(StructDefinition structDefinition) {
        print("StructDefinition(name=\"" + CharacterUtil.getRepresentation(structDefinition.getName())
                + "\") from " + structDefinition.getStartPosition() + " to " + structDefinition.getEndPosition() + "\n");
        indent++;
        if (structDefinition.getMembers().isEmpty()) {
            print("- Members: <empty>\n");
        } else {
            print("- Members:\n");
            for (StructDefinitionMember structDefinitionMember : structDefinition.getMembers()) {
                structDefinitionMember.accept(this);
            }
        }
        indent--;
    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {
        print("StructDefinitionMember(name=\"" + CharacterUtil.getRepresentation(structDefinitionMember.getName())
                + "\", typeName=\"" + structDefinitionMember.getTypeName() + "\") from " + structDefinitionMember.getStartPosition() + " to " + structDefinitionMember.getEndPosition() + "\n");
    }

    @Override
    public void visit(UnionDefinition unionDefinition) {
        print("UnionDefinition(name=\"" + CharacterUtil.getRepresentation(unionDefinition.getName())
                + "\") from " + unionDefinition.getStartPosition() + " to " + unionDefinition.getEndPosition() + "\n");
        indent++;
        if (unionDefinition.getTypeNames().isEmpty()) {
            print("- Types: <empty>\n");
        } else {
            print("- Types:\n");
            for (String typeName : unionDefinition.getTypeNames()) {
                print(typeName + "\n");
            }
        }
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) {
        print("AssignmentStatement from " + assignmentStatement.getStartPosition() + " to " + assignmentStatement.getEndPosition() + "\n");
        indent++;
        print("- Target:\n");
        assignmentStatement.getTarget().accept(this);
        print("- Value:\n");
        assignmentStatement.getValue().accept(this);
        indent--;
    }

    @Override
    public void visit(IfStatement ifStatement) {
        print("IfStatement from " + ifStatement.getStartPosition() + " to " + ifStatement.getEndPosition() + "\n");
        indent++;
        print("- Condition:\n");
        ifStatement.getCondition().accept(this);
        print("- Then:\n");
        ifStatement.getBody().accept(this);
        if (ifStatement.getElseBlock() == null) {
            print("- Else: null\n");
        } else {
            print("- Else:\n");
            ifStatement.getElseBlock().accept(this);
        }
        indent--;
    }

    @Override
    public void visit(MatchStatement matchStatement) {
        print("MatchStatement from " + matchStatement.getStartPosition() + " to " + matchStatement.getEndPosition() + "\n");
        indent++;
        print("- Expression:\n");
        matchStatement.getExpression().accept(this);
        if (matchStatement.getCases().isEmpty()) {
            print("- Cases: <empty>\n");
        } else {
            print("- Cases:\n");
            for (MatchCase matchCase : matchStatement.getCases()) {
                matchCase.accept(this);
            }
        }
        indent--;
    }

    @Override
    public void visit(MatchCase matchCase) {
        print("MatchCase(pattern=\"" + CharacterUtil.getRepresentation(matchCase.getPattern()) + "\", " +
                        "variableName=\"" + CharacterUtil.getRepresentation(matchCase.getVariableName()) + "\") " +
                "from " + matchCase.getStartPosition() + " to " + matchCase.getEndPosition() + "\n");
        indent++;
        print("- Body:\n");
        matchCase.getBody().accept(this);
        indent--;
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        print("ReturnStatement from " + returnStatement.getStartPosition() + " to " + returnStatement.getEndPosition() + "\n");
        indent++;
        if (returnStatement.getExpression() == null) {
            print("- Expression: null\n");
        } else {
            print("- Expression:\n");
            returnStatement.getExpression().accept(this);
        }
        indent--;
    }

    @Override
    public void visit(StatementBlock statementBlock) {
        print("StatementBlock from " + statementBlock.getStartPosition() + " to " + statementBlock.getEndPosition() + "\n");
        indent++;
        if (statementBlock.getStatements().isEmpty()) {
            print("- Statements: <empty>\n");
        } else {
            print("- Statements:\n");
            for (Statement statement : statementBlock.getStatements()) {
                statement.accept(this);
            }
        }
        indent--;
    }

    @Override
    public void visit(VariableDefinition variableDefinition) {
        print("VariableDefinition(name=\"" + CharacterUtil.getRepresentation(variableDefinition.getName())
                + "\", typeName=\"" + variableDefinition.getTypeName() + "\", " +
                "mutable=" + variableDefinition.isMutable() +
                ", reference=" + variableDefinition.isReference() + ") " +
                "from " + variableDefinition.getStartPosition() + " to " + variableDefinition.getEndPosition() + "\n");
        indent++;
        if (variableDefinition.getDefaultValue() == null) {
            print("- Default value: null\n");
        } else {
            print("- Default value:\n");
            variableDefinition.getDefaultValue().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        print("WhileStatement from " + whileStatement.getStartPosition() + " to " + whileStatement.getEndPosition() + "\n");
        indent++;
        print("- Condition:\n");
        whileStatement.getCondition().accept(this);
        print("- Body:\n");
        whileStatement.getBody().accept(this);
        indent--;
    }
}
