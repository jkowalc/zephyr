package me.jkowalc.zephyr.util;

public class EphemeralValue<T> {
    private T value;
    public EphemeralValue(T value) {
        this.value = value;
    }
    public T set(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        return oldValue;
    }
    public T get() {
        return set(null);
    }
}
