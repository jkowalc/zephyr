package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.VoidTextPrinter;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.binary.AddExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.DivideExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.EqualExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.MultiplyExpression;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.BooleanValue;
import me.jkowalc.zephyr.domain.runtime.value.FloatValue;
import me.jkowalc.zephyr.domain.runtime.value.IntegerValue;
import me.jkowalc.zephyr.domain.runtime.value.StringValue;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.exception.runtime.ConversionException;
import me.jkowalc.zephyr.exception.runtime.DivideByZeroException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionEvaluationTest {
    private static final FunctionDefinition DEFAULT_MAIN = new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), null);
    private static Interpreter interpreter;
    @BeforeAll
    public static void setUp() throws ZephyrException {
        interpreter = new Interpreter(new Program(Map.of("main", DEFAULT_MAIN), Map.of()), new VoidTextPrinter());
    }
    @Test
    public void testBasicExpression() throws ZephyrException {
        Value result = interpreter.evaluateExpression(new IntegerLiteral(1));
        assertEquals(new IntegerValue(1), result);
    }
    @Test
    public void testEqualsBasic() throws ZephyrException {
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new EqualExpression(new IntegerLiteral(1), new IntegerLiteral(1))));

        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new EqualExpression(new IntegerLiteral(1), new IntegerLiteral(2))));

        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new EqualExpression(new StringLiteral("a"), new StringLiteral("a"))));

        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new EqualExpression(new StringLiteral("a"), new StringLiteral("b"))));
    }
    @Test
    public void testEqualsArithmeticConversion() throws ZephyrException {
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new EqualExpression(new IntegerLiteral(1), new FloatLiteral(1.0f))));

        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new EqualExpression(new IntegerLiteral(1), new FloatLiteral(1.1f))));
    }
    @Test
    public void testAdd() throws ZephyrException {
        assertEquals(new IntegerValue(3),
                interpreter.evaluateExpression(new AddExpression(new IntegerLiteral(1), new IntegerLiteral(2))));

        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new AddExpression(new IntegerLiteral(1), new FloatLiteral(1.0f))));

        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new AddExpression(new FloatLiteral(1.0f), new IntegerLiteral(1))));

        assertEquals(new StringValue("1a"),
                interpreter.evaluateExpression(new AddExpression(new IntegerLiteral(1), new StringLiteral("a"))));

        assertEquals(new StringValue("a1"),
                interpreter.evaluateExpression(new AddExpression(new StringLiteral("a"), new IntegerLiteral(1))));

        assertEquals(new StringValue("1.0a"),
                interpreter.evaluateExpression(new AddExpression(new FloatLiteral(1.0f), new StringLiteral("a"))));

        assertEquals(new StringValue("a1.0"),
                interpreter.evaluateExpression(new AddExpression(new StringLiteral("a"), new FloatLiteral(1.0f))));

        assertEquals(new StringValue("atrue"),
                interpreter.evaluateExpression(new AddExpression(new StringLiteral("a"), new BooleanLiteral(true))));

        assertEquals(new StringValue("truea"),
                interpreter.evaluateExpression(new AddExpression(new BooleanLiteral(true), new StringLiteral("a"))));

        assertEquals(new StringValue("true1"),
                interpreter.evaluateExpression(new AddExpression(new BooleanLiteral(true), new IntegerLiteral(1))));
    }
    @Test
    public void testAddExplicitConversion() throws ZephyrException {
        assertEquals(new IntegerValue(2),
                interpreter.evaluateExpression(new AddExpression(new IntegerLiteral(1), new FunctionCall("to_int", List.of(new BooleanLiteral(true))))));

        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new AddExpression(new IntegerLiteral(1), new FunctionCall("to_float", List.of(new BooleanLiteral(true))))));
    }
    @Test
    public void testAddStruct() throws ZephyrException {
        assertEquals(new StringValue("1{a: 1}"),
                interpreter.evaluateExpression(new AddExpression(
                        new IntegerLiteral(1),
                        new StructLiteral(Map.of("a", new IntegerLiteral(1))))));

        assertEquals(new StringValue("{a: 1}1"),
                interpreter.evaluateExpression(new AddExpression(
                        new StructLiteral(Map.of("a", new IntegerLiteral(1))),
                        new IntegerLiteral(1))));

        assertEquals(new StringValue("{a: 1}{b: 2}"),
                interpreter.evaluateExpression(new AddExpression(
                        new StructLiteral(Map.of("a", new IntegerLiteral(1))),
                        new StructLiteral(Map.of("b", new IntegerLiteral(2))))));

        assertEquals(new StringValue("1.0{a: 1}"),
                interpreter.evaluateExpression(new AddExpression(
                        new FloatLiteral(1.0f),
                        new StructLiteral(Map.of("a", new IntegerLiteral(1))))));

        assertEquals(new StringValue("The struct: {a: 1}"),
                interpreter.evaluateExpression(new AddExpression(
                        new StringLiteral("The struct: "),
                        new StructLiteral(Map.of("a", new IntegerLiteral(1))))));
    }
    @Test
    public void testComplexExpression() throws ZephyrException {
        Value result = interpreter.evaluateExpression(new MultiplyExpression(
                new IntegerLiteral(2),
                new FunctionCall("to_float", List.of(new StringLiteral("2.5")))
        ));
        assertInstanceOf(FloatValue.class, result);
        FloatValue floatValue = (FloatValue) result;
        assertEquals(5.0f, floatValue.value(), 0.00001f);
    }
    @Test
    public void testDivideByZero() {
        assertThrows(DivideByZeroException.class, () -> interpreter.evaluateExpression(
                new DivideExpression(new IntegerLiteral(1), new IntegerLiteral(0))
        ));
        assertThrows(DivideByZeroException.class, () -> interpreter.evaluateExpression(
                new DivideExpression(new FloatLiteral(1.0f), new FloatLiteral(0.0f))
        ));
    }
    @Test
    public void testConversionException() {
        assertThrows(ConversionException.class, () -> interpreter.evaluateExpression(
                new FunctionCall("to_int", List.of(new StringLiteral("a")))
        ));
    }
}
