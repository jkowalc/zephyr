package me.jkowalc.zephyr.interpreter;

import me.jkowalc.zephyr.VoidTextPrinter;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.FunctionDefinition;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.*;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.exception.analizer.AmbiguousExpressionType;
import me.jkowalc.zephyr.exception.analizer.NonConvertibleTypeException;
import me.jkowalc.zephyr.exception.runtime.ConversionException;
import me.jkowalc.zephyr.exception.runtime.DivideByZeroException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionEvaluationTest {
    private static final FunctionDefinition DEFAULT_MAIN = new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), null);
    private static Interpreter interpreter;
    @BeforeAll
    public static void setUp() throws ZephyrException {
        interpreter = new Interpreter(new Program(List.of(DEFAULT_MAIN), List.of()), new VoidTextPrinter());
    }
    @Test
    public void testBasicExpression() throws ZephyrException {
        Value result = interpreter.evaluateExpression(new IntegerLiteral(1));
        assertEquals(new IntegerValue(1), result);
    }
    @Test
    public void testStringConversion() throws ZephyrException {
        assertEquals(new StringValue("{}"), interpreter.evaluateExpression(
                new FunctionCall("to_string", List.of(
                        new StructLiteral(List.of())
                ))
        ));
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
                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))))));

        assertEquals(new StringValue("{a: 1}1"),
                interpreter.evaluateExpression(new AddExpression(
                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))),
                        new IntegerLiteral(1))));

        assertEquals(new StringValue("{a: 1}{b: 2}"),
                interpreter.evaluateExpression(new AddExpression(
                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))),
                        new StructLiteral(List.of(new StructLiteralMember("b", new IntegerLiteral(2)))))));

        assertEquals(new StringValue("1.0{a: 1}"),
                interpreter.evaluateExpression(new AddExpression(
                        new FloatLiteral(1.0f),
                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))))));

        assertEquals(new StringValue("The struct: {a: 1}"),
                interpreter.evaluateExpression(new AddExpression(
                        new StringLiteral("The struct: "),
                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))))));
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
    public void testBoolean() throws ZephyrException {
        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new AndExpression(new BooleanLiteral(false), new BooleanLiteral(true))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new OrExpression(new BooleanLiteral(false), new BooleanLiteral(true))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new NotExpression(new BooleanLiteral(false))));
        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new NotExpression(new BooleanLiteral(true))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new BooleanLiteral(true)))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new StringLiteral("a")))));
        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new StringLiteral("")))));
        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new IntegerLiteral(0)))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new IntegerLiteral(1)))));
        assertEquals(new BooleanValue(true),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new FloatLiteral(1.0f)))));
        assertEquals(new BooleanValue(false),
                interpreter.evaluateExpression(new NotExpression(new NotExpression(new FloatLiteral(0.0f)))));
        assertThrows(NonConvertibleTypeException.class, () -> interpreter.evaluateExpression(
                new NotExpression(new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))))
        ));
    }
    @Test
    public void testDivide() throws ZephyrException {
        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new DivideExpression(new IntegerLiteral(4), new IntegerLiteral(2))));
        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new DivideExpression(new IntegerLiteral(4), new FloatLiteral(2.0f))));
        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new DivideExpression(new FloatLiteral(4.0f), new IntegerLiteral(2))));
        assertEquals(new FloatValue(2.0f),
                interpreter.evaluateExpression(new DivideExpression(new FloatLiteral(4.0f), new FloatLiteral(2.0f))));
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
    public void testMultiply() throws ZephyrException {
        assertEquals(new IntegerValue(6),
                interpreter.evaluateExpression(new MultiplyExpression(new IntegerLiteral(2), new IntegerLiteral(3))));
        assertEquals(new IntegerValue(-6),
                interpreter.evaluateExpression(new MultiplyExpression(new IntegerLiteral(2), new IntegerLiteral(-3))));
        assertEquals(new FloatValue(6.0f),
                interpreter.evaluateExpression(new MultiplyExpression(new IntegerLiteral(2), new FloatLiteral(3.0f))));
        assertThrows(AmbiguousExpressionType.class, () -> interpreter.evaluateExpression(
                new MultiplyExpression(new StringLiteral("2"), new StringLiteral("3"))
        ));
    }
    @Test
    public void testConversionException() {
        assertThrows(ConversionException.class, () -> interpreter.evaluateExpression(
                new FunctionCall("to_int", List.of(new StringLiteral("a")))
        ));
    }
}
