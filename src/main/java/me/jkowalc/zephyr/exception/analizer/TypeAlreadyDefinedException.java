package me.jkowalc.zephyr.exception.analizer;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.program.TypeDefinition;
import me.jkowalc.zephyr.exception.ZephyrException;

@Getter
public class TypeAlreadyDefinedException extends ZephyrException {
    private final TypeDefinition typeDefinition;
    public TypeAlreadyDefinedException(TypeDefinition typeDefinition) {
        super("Type " + typeDefinition.getName() + " already defined", typeDefinition.getStartPosition());
        this.typeDefinition = typeDefinition;
    }
}
