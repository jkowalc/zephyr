package me.jkowalc.zephyr.interpreter;

import lombok.Setter;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.runtime.value.BooleanValue;
import me.jkowalc.zephyr.domain.runtime.value.FloatValue;
import me.jkowalc.zephyr.domain.runtime.value.IntegerValue;
import me.jkowalc.zephyr.domain.runtime.value.StringValue;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.exception.type.ConversionTypeException;
import me.jkowalc.zephyr.exception.ZephyrInternalException;


@Setter
public class TypeConverter {
    public static Value convert(Value value, TypeCategory target) throws ConversionTypeException {
        value = value.getValue();
        if (value.getType().equals(target)) return value;
        if (target.equals(TypeCategory.STRING)) return new StringValue(value.toString());
        if (value.getType().equals(TypeCategory.STRUCT)) throw new ZephyrInternalException();
        switch(value.getType()) {
            case INT:
                IntegerValue integerValue = (IntegerValue) value;
                switch(target) {
                    case FLOAT:
                        return new FloatValue((float) integerValue.value());
                    case BOOL:
                        if(integerValue.value() == 1) return new BooleanValue(true);
                        else if(integerValue.value() == 0) return new BooleanValue(false);
                        else throw new ConversionTypeException(value, target);
                }
            case FLOAT:
                assert value instanceof FloatValue;
                FloatValue floatValue = (FloatValue) value;
                switch(value.getType()) {
                    case INT:
                        return new IntegerValue((int) Math.floor(floatValue.value()));
                    case BOOL:
                        if(floatValue.value() == 1.0) return new BooleanValue(true);
                        else if(floatValue.value() == 0.0) return new BooleanValue(false);
                        else throw new ConversionTypeException(value, target);
                }
            case BOOL:
                assert value instanceof BooleanValue;
                BooleanValue booleanValue = (BooleanValue) value;
                switch(value.getType()) {
                    case INT:
                        return new IntegerValue(booleanValue.value() ? 1 : 0);
                    case FLOAT:
                        return new FloatValue(booleanValue.value() ? 1.0f : 0.0f);
                }
            case STRING:
                assert value instanceof StringValue;
                StringValue stringValue = (StringValue) value;
                switch(value.getType()) {
                    case INT:
                        return new IntegerValue(Integer.parseInt(stringValue.value()));
                    case FLOAT:
                        return new FloatValue(Float.parseFloat(stringValue.value()));
                    case BOOL:
                        if(!stringValue.value().isEmpty()) return new BooleanValue(true);
                        else return new BooleanValue(false);
                }
        }
        return value;
    }
}
