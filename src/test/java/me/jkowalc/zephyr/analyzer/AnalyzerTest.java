package me.jkowalc.zephyr.analyzer;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.DotExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.SubtractExpression;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.StringLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.StructLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.StructLiteralMember;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.AssignmentStatement;
import me.jkowalc.zephyr.domain.node.statement.ReturnStatement;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.node.statement.VariableDefinition;
import me.jkowalc.zephyr.exception.analizer.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnalyzerTest {
    private final StaticAnalyzer analyzer = new StaticAnalyzer();

    private Program programFromMainBlock(StatementBlock block) {
        return new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                block,
                                null
                        )
                ),
                List.of()
        );
    }

    @Test
    public void testVariableNotDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new ReturnStatement(new VariableReference("a"))
                )
        ));
        assertThrows(VariableNotDefinedException.class, () -> program.accept(analyzer));
    }

    @Test
    public void testVariableNotInitialized() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "int", false, false, null)
                )
        ));
        assertThrows(VariableNotInitializedException.class, () -> program.accept(analyzer));
    }

    @Test
    public void testTypeNotDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "not_defined", false, false, new IntegerLiteral(1))
                )
        ));
        assertThrows(TypeNotDefinedException.class, () -> program.accept(analyzer));
    }

    @Test
    public void testConvertiblePassedByReference() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of(
                                                new VariableDefinition("a", "int", false, false, new IntegerLiteral(1)),
                                                new FunctionCall("doSomething", List.of(new VariableReference("a")))
                                        )
                                ),
                                null
                        ),
                        new FunctionDefinition(
                                "doSomething",
                                List.of(new VariableDefinition("a", "float", true, true, null)),
                                new StatementBlock(
                                        List.of()
                                ),
                                null)
                ),
                List.of()
        );
        assertThrows(ConvertiblePassedByReferenceException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testFunctionAlreadyDefinedStandard() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition(
                                "hello",
                                List.of(),
                                new StatementBlock(List.of()),
                                null
                        ),
                        new FunctionDefinition(
                                "hello",
                                List.of(),
                                new StatementBlock(List.of()),
                                null
                        )
                ),
                List.of()
        );
        assertThrows(FunctionAlreadyDefinedException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testFunctionAlreadyDefinedBuiltin() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of()),
                                null
                        ),
                        new FunctionDefinition(
                                "print",
                                List.of(),
                                new StatementBlock(
                                        List.of()
                                ),
                                null)
                ),
                List.of()
        );
        assertThrows(FunctionAlreadyDefinedException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testFunctionNotDefined() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new FunctionCall("doSomething", List.of())
                )
        ));
        assertThrows(FunctionNotDefinedException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testImmutableVariableModified() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new VariableDefinition("a", "int", false, false, new IntegerLiteral(1)),
                        new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(2))
                )
        ));
        assertThrows(ImmutableVariableException.class, () -> program.accept(analyzer));

        Program program1 = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of(
                                                new VariableDefinition("a", "SomeStruct", false, false,
                                                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1))))),
                                                new AssignmentStatement(new VariableReference("a"), new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(2)))))
                                        )
                                ),
                                null
                        )
                ),
                List.of(
                        new StructDefinition("SomeStruct", List.of(
                                new StructDefinitionMember("a", "int")
                        ))
                )
        );
        assertThrows(ImmutableVariableException.class, () -> program1.accept(analyzer));
        Program program2 = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of(
                                                new VariableDefinition("a", "SomeStruct", false, false,
                                                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1))))),
                                                new AssignmentStatement(new DotExpression(new VariableReference("a"), "a"), new IntegerLiteral(2))
                                        )
                                ),
                                null
                        )
                ),
                List.of(
                        new StructDefinition("SomeStruct", List.of(
                                new StructDefinitionMember("a", "int")
                        ))
                )
        );
        assertThrows(ImmutableVariableException.class, () -> program2.accept(analyzer));
    }
    @Test
    public void testInvalidArgumentCount() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of(
                                                new FunctionCall("doSomething", List.of(new IntegerLiteral(1), new IntegerLiteral(2)))
                                        )
                                ),
                                null
                        ),
                        new FunctionDefinition(
                                "doSomething",
                                List.of(new VariableDefinition("a", "int", false, false, null)),
                                new StatementBlock(
                                        List.of()
                                ),
                                null)
                ),
                List.of()
        );
        assertThrows(InvalidArgumentCountException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testInvalidFieldAccess() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition(
                                "main",
                                List.of(),
                                new StatementBlock(
                                        List.of(
                                                new VariableDefinition("a", "SomeStruct", false, false,
                                                        new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1))))),
                                                new FunctionCall("print", List.of(new DotExpression(new VariableReference("a"), "b")))
                                        )
                                ),
                                null
                        )
                ),
                List.of(
                        new StructDefinition("SomeStruct", List.of(
                                new StructDefinitionMember("a", "int")
                        ))
                )
        );
        assertThrows(InvalidFieldAccessException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testInvalidTypeForOperation() {
        Program program = programFromMainBlock(new StatementBlock(
                List.of(
                        new FunctionCall("print", List.of(
                                new SubtractExpression(new IntegerLiteral(1), new StructLiteral(List.of(new StructLiteralMember("a", new IntegerLiteral(1)))))
                        ))
                )
        ));
        assertThrows(InvalidTypeForOperationException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testMainFunctionNotDefined() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition("doSomething", List.of(), new StatementBlock(List.of()), null)
                ),
                List.of()
        );
        assertThrows(MainFunctionNotDefinedException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testNonConvertibleType() {
        Program program = new Program(
                List.of(
                        new FunctionDefinition("main", List.of(), new StatementBlock(List.of(
                                new VariableDefinition("a", "SomeVariant", false, false, new IntegerLiteral(1)),
                                new FunctionCall("doSomething", List.of(new VariableReference("a")))
                        )), null),
                        new FunctionDefinition("doSomething", List.of(new VariableDefinition("a", "float", false, false, null)), new StatementBlock(List.of()), null)
                ),
                List.of(
                        new UnionDefinition("SomeVariant", List.of("int", "string"))
                )
        );
        assertThrows(NonConvertibleTypeException.class, () -> program.accept(analyzer));
    }
    @Test
    public void testDuplicateFieldInStructLiteral() {
        Program program = new Program(
                List.of(new FunctionDefinition("main", List.of(), new StatementBlock(List.of(
                        new VariableDefinition("a", "A", false, false, new StructLiteral(List.of(
                                new StructLiteralMember("a", new StringLiteral("hello")),
                                new StructLiteralMember("a", new StringLiteral("world"))
                        )))
                )), null)),
                List.of(new StructDefinition("A", List.of(
                        new StructDefinitionMember("a", "string")
                        ))
                )
        );
        assertThrows(DuplicateStructFieldException.class, () -> program.accept(analyzer));
    }
}
