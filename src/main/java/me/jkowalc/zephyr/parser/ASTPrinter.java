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
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.CharacterUtil;

import java.io.IOException;
import java.io.OutputStream;

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
    public void visit(Program program) throws ZephyrException {
        print("Program at " + program.getStartPosition() + "\n");
        if (program.getFunctions().isEmpty()) {
            print("- Functions: <empty>\n");
        } else {
            print("- Functions:\n");
            indent++;
            for (FunctionDefinition functionDefinition : program.getFunctions()) {
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
        for (TypeDefinition typeDefinition : program.getTypes()) {
            typeDefinition.accept(this);
        }
        indent--;
    }

    @Override
    public void visit(FunctionCall functionCall) throws ZephyrException {
        print("FunctionCall(name=\"" + CharacterUtil.getRepresentation(functionCall.getName())
                + "\") at " + functionCall.getStartPosition() + "\n");
        if (functionCall.getArguments().isEmpty()) {
            print("- Parameters: <empty>\n");
            return;
        }
        print("- Parameters:\n");
        indent++;
        for (Expression expression : functionCall.getArguments()) {
            expression.accept(this);
        }
        indent--;
    }

    @Override
    public void visit(VariableReference variableReference) {
        print("VariableReference(name=" + CharacterUtil.getRepresentation(variableReference.getName())
                + ") at " + variableReference.getStartPosition() + "\n");
    }
    private void printDefaultBinaryExpression(DefaultBinaryExpression binaryExpression, String name) throws ZephyrException {
        print(name + " at " + binaryExpression.getStartPosition() + "\n");
        indent++;
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
        indent--;
    }
    @Override
    public void visit(AddExpression addExpression) throws ZephyrException {
        printDefaultBinaryExpression(addExpression, "AddExpression");
    }

    @Override
    public void visit(AndExpression andExpression) throws ZephyrException {
        printDefaultBinaryExpression(andExpression, "AndExpression");
    }

    @Override
    public void visit(DivideExpression divideExpression) throws ZephyrException {
        printDefaultBinaryExpression(divideExpression, "DivideExpression");
    }

    @Override
    public void visit(DotExpression dotExpression) throws ZephyrException {
        print("DotExpression at " + dotExpression.getStartPosition() + "\n");
        if(dotExpression.getValue() == null) {
            print("- Value: null\n");
        } else {
            print("- Value:\n");
            indent++;
            dotExpression.getValue().accept(this);
            indent--;
        }
        print("- Field: " + CharacterUtil.getRepresentation(dotExpression.getField()) + "\n");
    }

    @Override
    public void visit(EqualExpression equalExpression) throws ZephyrException {
        printDefaultBinaryExpression(equalExpression, "EqualExpression");
    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) throws ZephyrException {
        printDefaultBinaryExpression(greaterEqualExpression, "GreaterEqualExpression");
    }

    @Override
    public void visit(GreaterExpression greaterExpression) throws ZephyrException {
        printDefaultBinaryExpression(greaterExpression, "GreaterExpression");
    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) throws ZephyrException {
        printDefaultBinaryExpression(lessEqualExpression, "LessEqualExpression");
    }

    @Override
    public void visit(LessExpression lessExpression) throws ZephyrException {
        printDefaultBinaryExpression(lessExpression, "LessExpression");
    }

    @Override
    public void visit(MultiplyExpression multiplyExpression) throws ZephyrException {
        printDefaultBinaryExpression(multiplyExpression, "MultiplyExpression");
    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) throws ZephyrException {
        printDefaultBinaryExpression(notEqualExpression, "NotEqualExpression");
    }

    @Override
    public void visit(OrExpression orExpression) throws ZephyrException {
        printDefaultBinaryExpression(orExpression, "OrExpression");
    }

    @Override
    public void visit(SubtractExpression subtractExpression) throws ZephyrException {
        printDefaultBinaryExpression(subtractExpression, "SubtractExpression");
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        print("BooleanLiteral(value=" + booleanLiteral.getValue() + ") at " + booleanLiteral.getStartPosition() + "\n");
    }

    @Override
    public void visit(IntegerLiteral integerLiteral) {
        print("IntegerLiteral(value=" + integerLiteral.getValue() + ") at " + integerLiteral.getStartPosition() + "\n");
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        print("FloatLiteral(value=" + floatLiteral.getValue() + ") at " + floatLiteral.getStartPosition() + "\n");
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        print("StringLiteral(value=\"" + CharacterUtil.getRepresentation(stringLiteral.getValue()) + "\") at " + stringLiteral.getStartPosition() + "\n");
    }

    @Override
    public void visit(StructLiteral structLiteral) throws ZephyrException {
        print("StructLiteral at " + structLiteral.getStartPosition() + "\n");
        indent++;
        for (StructLiteralMember field: structLiteral.getFields()) {
            field.accept(this);

        }
        indent--;
    }

    @Override
    public void visit(StructLiteralMember structLiteralMember) throws ZephyrException {
        print("\"" + CharacterUtil.getRepresentation(structLiteralMember.getFieldName()) + "\":\n");
        indent++;
        structLiteralMember.getFieldValue().accept(this);
        indent--;
    }

    @Override
    public void visit(NegationExpression negationExpression) throws ZephyrException {
        print("NegationExpression at " + negationExpression.getStartPosition() + "\n");
        indent++;
        negationExpression.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(NotExpression notExpression) throws ZephyrException {
        print("NotExpression at " + notExpression.getStartPosition() + "\n");
        indent++;
        notExpression.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(FunctionDefinition functionDefinition) throws ZephyrException {
        print("FunctionDefinition(name=\"" + CharacterUtil.getRepresentation(functionDefinition.getName())
                + "\") at " + functionDefinition.getStartPosition()  + "\n");
        if (functionDefinition.getParameters().isEmpty()) {
            print("- Parameters: <empty>\n");
        } else {
            print("- Parameters:\n");
            indent++;
            for (VariableDefinition variableDefinition : functionDefinition.getParameters()) {
                variableDefinition.accept(this);
            }
            indent--;
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
            indent++;
            functionDefinition.getBody().accept(this);
            indent--;
        }
    }

    @Override
    public void visit(StructDefinition structDefinition) throws ZephyrException {
        print("StructDefinition(name=\"" + CharacterUtil.getRepresentation(structDefinition.getName())
                + "\") at " + structDefinition.getStartPosition() + "\n");

        if (structDefinition.getMembers().isEmpty()) {
            print("- Members: <empty>\n");
        } else {
            print("- Members:\n");
            indent++;
            for (StructDefinitionMember structDefinitionMember : structDefinition.getMembers()) {
                structDefinitionMember.accept(this);
            }
            indent--;
        }
    }

    @Override
    public void visit(StructDefinitionMember structDefinitionMember) {
        print("StructDefinitionMember(name=\"" + CharacterUtil.getRepresentation(structDefinitionMember.getName())
                + "\", typeName=\"" + structDefinitionMember.getTypeName() + "\") at " + structDefinitionMember.getStartPosition() + "\n");
    }

    @Override
    public void visit(UnionDefinition unionDefinition) {
        print("UnionDefinition(name=\"" + CharacterUtil.getRepresentation(unionDefinition.getName())
                + "\") at " + unionDefinition.getStartPosition() + "\n");
        if (unionDefinition.getTypeNames().isEmpty()) {
            print("- Types: <empty>\n");
        } else {
            print("- Types:\n");
            indent++;
            for (String typeName : unionDefinition.getTypeNames()) {
                print(typeName + "\n");
            }
            indent--;
        }
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) throws ZephyrException {
        print("AssignmentStatement at " + assignmentStatement.getStartPosition() + "\n");
        print("- Target:\n");
        indent++;
        assignmentStatement.getTarget().accept(this);
        indent--;
        print("- Value:\n");
        indent++;
        assignmentStatement.getValue().accept(this);
        indent--;
    }

    @Override
    public void visit(IfStatement ifStatement) throws ZephyrException {
        print("IfStatement at " + ifStatement.getStartPosition() + "\n");
        print("- Condition:\n");
        indent++;
        ifStatement.getCondition().accept(this);
        indent--;
        print("- Then:\n");
        indent++;
        ifStatement.getBody().accept(this);
        indent--;
        if (ifStatement.getElseBlock() == null) {
            print("- Else: null (no else block)\n");
        } else {
            print("- Else:\n");
            indent++;
            ifStatement.getElseBlock().accept(this);
            indent--;
        }
    }

    @Override
    public void visit(MatchStatement matchStatement) throws ZephyrException {
        print("MatchStatement at " + matchStatement.getStartPosition() + "\n");
        print("- Expression:\n");
        indent++;
        matchStatement.getExpression().accept(this);
        indent--;
        if (matchStatement.getCases().isEmpty()) {
            print("- Cases: <empty>\n");
        } else {
            print("- Cases:\n");
            indent++;
            for (MatchCase matchCase : matchStatement.getCases()) {
                matchCase.accept(this);
            }
            indent--;
        }
    }

    @Override
    public void visit(MatchCase matchCase) throws ZephyrException {
        print("MatchCase(pattern=\"" + CharacterUtil.getRepresentation(matchCase.getPattern()) + "\", " +
                        "variableName=\"" + CharacterUtil.getRepresentation(matchCase.getVariableName()) + "\") " +
                "at " + matchCase.getStartPosition() + "\n");
        print("- Body:\n");
        indent++;
        matchCase.getBody().accept(this);
        indent--;
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws ZephyrException {
        print("ReturnStatement at " + returnStatement.getStartPosition() + "\n");

        if (returnStatement.getExpression() == null) {
            print("- Expression: null\n");
        } else {
            print("- Expression:\n");
            indent++;
            returnStatement.getExpression().accept(this);
            indent--;
        }
    }

    @Override
    public void visit(StatementBlock statementBlock) throws ZephyrException {
        print("StatementBlock at " + statementBlock.getStartPosition() + "\n");
        if (statementBlock.getStatements().isEmpty()) {
            print("- Statements: <empty>\n");
        } else {
            print("- Statements:\n");
            indent++;
            for (Statement statement : statementBlock.getStatements()) {
                statement.accept(this);
            }
            indent--;
        }
    }

    @Override
    public void visit(VariableDefinition variableDefinition) throws ZephyrException {
        print("VariableDefinition(name=\"" + CharacterUtil.getRepresentation(variableDefinition.getName())
                + "\", typeName=" + variableDefinition.getTypeName() + ", " +
                "mutable=" + variableDefinition.isMutable() +
                ", reference=" + variableDefinition.isReference() + ") " +
                "at " + variableDefinition.getStartPosition() + "\n");

        if (variableDefinition.getDefaultValue() == null) {
            print("- Default value: null (no default value)\n");
        } else {
            print("- Default value:\n");
            indent++;
            variableDefinition.getDefaultValue().accept(this);
            indent--;
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) throws ZephyrException {
        print("WhileStatement at " + whileStatement.getStartPosition() + "\n");
        print("- Condition:\n");
        indent++;
        whileStatement.getCondition().accept(this);
        indent--;
        print("- Body:\n");
        indent++;
        whileStatement.getBody().accept(this);
        indent--;
    }
}
