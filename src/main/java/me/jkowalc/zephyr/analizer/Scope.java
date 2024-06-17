package me.jkowalc.zephyr.analizer;

import me.jkowalc.zephyr.exception.scope.VariableNotDefinedScopeException;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.Map;

public class Scope<T> {
    private final Scope<T> parent;
    private final Map<String, T> values;
    public Scope(Scope<T> parent, Map<String, T> values) {
        this.parent = parent;
        if(values == null) this.values = new SimpleMap<>();
        else this.values = values;
    }
    public void set(String name, T value) {
        values.put(name, value);
    }
    public T get(String name) throws VariableNotDefinedScopeException {
        if(values.containsKey(name)) {
            return values.get(name);
        }
        if(parent != null) {
            return parent.get(name);
        }
        throw new VariableNotDefinedScopeException("Variable " + name + " not defined");
    }

}
