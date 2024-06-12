package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.VoidTextPrinter;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.binary.EqualExpression;
import me.jkowalc.zephyr.domain.node.expression.literal.FloatLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.StringLiteral;
import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.node.statement.ReturnStatement;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.BooleanValue;
import me.jkowalc.zephyr.domain.runtime.value.IntegerValue;
import me.jkowalc.zephyr.exception.ZephyrException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionEvaluationTest {
    private Value result;
    private static final FunctionDefinition DEFAULT_MAIN = new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), null);
    private void evaluate(Expression expression, String expectedType) throws ZephyrException {
        Program program = new Program(Map.of("main", DEFAULT_MAIN, "eval",
                new FunctionDefinition("eval",
                        List.of(),
                        new StatementBlock(List.of(
                                new ReturnStatement(expression)
                        )), expectedType)), Map.of());
        Interpreter interpreter = new Interpreter(program, new VoidTextPrinter());
        result = interpreter.executeFunction("eval", List.of());
    }
    @Test
    public void testBasicExpression() throws ZephyrException {
        evaluate(new IntegerLiteral(1), "int");
        assertEquals(new IntegerValue(1), result);
    }
    @Test
    public void testEqualsBasic() throws ZephyrException {
        evaluate(new EqualExpression(new IntegerLiteral(1), new IntegerLiteral(1)), "bool");
        assertEquals(new BooleanValue(true), result);
        evaluate(new EqualExpression(new IntegerLiteral(1), new IntegerLiteral(2)), "bool");
        assertEquals(new BooleanValue(false), result);

        evaluate(new EqualExpression(new StringLiteral("a"), new StringLiteral("a")), "bool");
        assertEquals(new BooleanValue(true), result);
        evaluate(new EqualExpression(new StringLiteral("a"), new StringLiteral("b")), "bool");
        assertEquals(new BooleanValue(false), result);
    }
    @Test
    public void testEqualsArithmeticConversion() throws ZephyrException {
        evaluate(new EqualExpression(new IntegerLiteral(1), new FloatLiteral(1.0f)), "bool");
        assertEquals(new BooleanValue(true), result);

        evaluate(new EqualExpression(new IntegerLiteral(1), new FloatLiteral(1.1f)), "bool");
        assertEquals(new BooleanValue(false), result);


    }
}
