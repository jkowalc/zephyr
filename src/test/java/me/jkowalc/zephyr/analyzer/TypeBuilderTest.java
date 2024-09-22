package me.jkowalc.zephyr.analyzer;

import me.jkowalc.zephyr.domain.node.program.StructDefinition;
import me.jkowalc.zephyr.domain.node.program.StructDefinitionMember;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.domain.node.program.UnionDefinition;
import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.domain.type.StructStaticType;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.domain.type.UnionStaticType;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.exception.analizer.DuplicateStructFieldException;
import me.jkowalc.zephyr.exception.analizer.RecursiveTypeDefinitionException;
import me.jkowalc.zephyr.exception.analizer.TypeAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TypeBuilderTest {
    private Map<String, BareStaticType> result;
    private void build(List<TypeDefinition> typeDefinitions) throws ZephyrException {
        TypeBuilder typeBuilder = new TypeBuilder(typeDefinitions);
        result = typeBuilder.build().getTypes();
    }
    @Test
    public void testSimpleTypes() throws ZephyrException {
        build(List.of(
                new StructDefinition("SomeStruct", List.of(
                        new StructDefinitionMember("a", "int"),
                        new StructDefinitionMember("b", "float")
                )),
                new UnionDefinition("SomeUnion", List.of("SomeStruct", "int"))
                ));
        Map<String, BareStaticType> expected = Map.of(
                "int", new BareStaticType(TypeCategory.INT),
                "float", new BareStaticType(TypeCategory.FLOAT),
                "string", new BareStaticType(TypeCategory.STRING),
                "bool", new BareStaticType(TypeCategory.BOOL),
                "SomeStruct", new StructStaticType(Map.of(
                        "a", new BareStaticType(TypeCategory.INT),
                        "b", new BareStaticType(TypeCategory.FLOAT)
                )),
                "SomeUnion", new UnionStaticType(List.of(
                        new StructStaticType(Map.of(
                                "a", new BareStaticType(TypeCategory.INT),
                                "b", new BareStaticType(TypeCategory.FLOAT)
                        )),
                        new BareStaticType(TypeCategory.INT)
                ))
        );
        assertEquals(expected, result);
    }
    @Test
    public void testRecursiveType() {
        assertThrows(RecursiveTypeDefinitionException.class, () -> build(List.of(
                new StructDefinition("SomeStruct", List.of(
                        new StructDefinitionMember("a", "SomeStruct")
                ))
        )));
        assertThrows(RecursiveTypeDefinitionException.class, () -> build(List.of(
                new UnionDefinition("SomeUnion", List.of("SomeUnion"))
        )));
        assertThrows(RecursiveTypeDefinitionException.class, () -> build(List.of(
                new StructDefinition("SomeStruct", List.of(
                        new StructDefinitionMember("a", "SomeUnion")
                )),
                new UnionDefinition("SomeUnion", List.of("SomeStruct"))
        )));
    }
    @Test
    public void testDuplicateFieldInStructDefinition() {
        assertThrows(DuplicateStructFieldException.class, () -> build(List.of(
                new StructDefinition(
                        "SomeStruct", List.of(
                                new StructDefinitionMember("a", "int"),
                                new StructDefinitionMember("a", "string")
                        )
                ))));
    }
    @Test
    public void testDuplicateTypes() {
        assertThrows(TypeAlreadyDefinedException.class, () -> build(List.of(
                new StructDefinition(
                        "A", List.of()
                ),
                new StructDefinition(
                        "A", List.of(new StructDefinitionMember("a", "int"))
                )
        )));
        assertThrows(TypeAlreadyDefinedException.class, () -> build(List.of(
                new UnionDefinition(
                        "A", List.of()
                ),
                new UnionDefinition(
                        "A", List.of("int")
                )
        )));
        assertThrows(TypeAlreadyDefinedException.class, () -> build(List.of(
                new StructDefinition(
                        "A", List.of()
                ),
                new UnionDefinition(
                        "A", List.of()
                )
        )));
    }
}
