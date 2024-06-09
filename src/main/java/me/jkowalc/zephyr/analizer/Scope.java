package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.exception.VariableAlreadyDefinedException;
import me.jkowalc.zephyr.exception.VariableNotDefinedException;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.Map;

public class Scope<T> {
    private final Scope<T> parent;
    private final Map<String, T> values = new SimpleMap<>();
    public Scope(Scope<T> parent) {
        this.parent = parent;
    }
    public Scope() {
        this.parent = null;
    }
    public void add(String name, T value) throws VariableAlreadyDefinedException {
        if(values.containsKey(name)) {
            throw new VariableAlreadyDefinedException("Variable" + name + " already defined");
        }
        values.put(name, value);
    }
    public T get(String name) throws VariableNotDefinedException {
        if(values.containsKey(name)) {
            return values.get(name);
        }
        if(parent != null) {
            return parent.get(name);
        }
        throw new VariableNotDefinedException("Variable " + name + " not defined");
    }

}
