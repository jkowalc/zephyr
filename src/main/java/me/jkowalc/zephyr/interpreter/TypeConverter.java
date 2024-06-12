package me.jkowalc.zephyr.interpreter;

import lombok.Setter;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.BooleanValue;
import me.jkowalc.zephyr.domain.runtime.value.FloatValue;
import me.jkowalc.zephyr.domain.runtime.value.IntegerValue;
import me.jkowalc.zephyr.domain.runtime.value.StringValue;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.type.ConversionTypeException;


@Setter
public class TypeConverter {
    private static int parseInt(String value) {
        int result = 0;
        for (int i = 0; i < value.length(); i++) {
            if(value.charAt(i) < '0' || value.charAt(i) > '9') throw new NumberFormatException("Invalid integer format");
            result = result * 10 + (value.charAt(i) - '0');
            if(result < 0) throw new NumberFormatException("Integer overflow");
        }
        return result;
    }
    private static float parseFloat(String value) {
        float result = 0;
        int i = 0;
        while(i < value.length() && value.charAt(i) != '.') {
            if(value.charAt(i) < '0' || value.charAt(i) > '9') throw new NumberFormatException("Invalid float format");
            result = result * 10 + (value.charAt(i) - '0');
            if(result < 0) throw new NumberFormatException("Float overflow");
            i++;
        }
        if(i == value.length()) return result;
        float divider = 1;
        i++;
        while(i < value.length()) {
            if(value.charAt(i) < '0' || value.charAt(i) > '9') throw new NumberFormatException("Invalid float format");
            divider *= 10;
            result += (value.charAt(i) - '0') / divider;
            i++;
        }
        return result;
    }
    public static Value convert(Value value, TypeCategory target) throws ConversionTypeException {
        value = value.getValue();
        if (value.getCategory().equals(target)) return value;
        if (target.equals(TypeCategory.STRING)) return new StringValue(value.toString());
        if (value.getCategory().equals(TypeCategory.STRUCT)) return value;
        if(target.equals(TypeCategory.UNION)) return value;
        try {
            switch (value.getCategory()) {
                case INT:
                    IntegerValue integerValue = (IntegerValue) value;
                    switch (target) {
                        case FLOAT:
                            return new FloatValue((float) integerValue.value());
                        case BOOL:
                            if (integerValue.value() == 1) return new BooleanValue(true);
                            else if (integerValue.value() == 0) return new BooleanValue(false);
                            else throw new ConversionTypeException(value, target);
                    }
                case FLOAT:
                    assert value instanceof FloatValue;
                    FloatValue floatValue = (FloatValue) value;
                    switch (target) {
                        case INT:
                            return new IntegerValue((int) Math.floor(floatValue.value()));
                        case BOOL:
                            if (floatValue.value() == 1.0) return new BooleanValue(true);
                            else if (floatValue.value() == 0.0) return new BooleanValue(false);
                            else throw new ConversionTypeException(value, target);
                    }
                case BOOL:
                    assert value instanceof BooleanValue;
                    BooleanValue booleanValue = (BooleanValue) value;
                    switch (target) {
                        case INT:
                            return new IntegerValue(booleanValue.value() ? 1 : 0);
                        case FLOAT:
                            return new FloatValue(booleanValue.value() ? 1.0f : 0.0f);
                    }
                case STRING:
                    assert value instanceof StringValue;
                    StringValue stringValue = (StringValue) value;
                    switch (target) {
                        case INT:
                            return new IntegerValue(parseInt(stringValue.value()));
                        case FLOAT:
                            return new FloatValue(parseFloat(stringValue.value()));
                        case BOOL:
                            if (!stringValue.value().isEmpty()) return new BooleanValue(true);
                            else return new BooleanValue(false);
                    }
            }
        } catch (NumberFormatException e) {
            throw new ConversionTypeException(value, target);
        }
        return value;
    }
}
