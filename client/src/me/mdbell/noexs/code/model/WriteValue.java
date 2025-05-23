package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WriteValue implements IInstruction {

    private Pointer pointer;

    private EValueType valueType;

    private String value;

    public WriteValue(Pointer pointer, EValueType valueType, String value) {
        super();
        this.pointer = pointer;
        this.valueType = valueType;
        this.value = value;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    public EValueType getValueType() {
        return valueType;
    }

    public void setValueType(EValueType valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getLongValue() {
        long res = 0;
        if (StringUtils.startsWithIgnoreCase(value, "0x")) {
            String val = StringUtils.remove(value, "0x");
            res = Long.parseLong(val, 16);
        } else {
            switch (valueType) {
                case S8:
                case S16:
                case S32:
                case S64:
                    res = Long.parseUnsignedLong(value);
                    break;
                case U8:
                case U16:
                case U32:
                case U64:
                    res = Long.parseLong(value);
                    break;
                case FLT:
                    Float floatValue = Float.parseFloat(value);

                    res = Float.floatToIntBits(floatValue);
            }
        }

        return res;
    }

    public String getHexValue() {
        String res = "";
        if (StringUtils.startsWithIgnoreCase(value, "0x")) {
            res = StringUtils.remove(value, "0x");
        } else {
            switch (valueType) {
                case S8:
                case S16:
                case S32:
                case S64:
                    Long ulVal = Long.parseUnsignedLong(value);
                    res = Long.toHexString(ulVal);
                    break;
                case U8:
                case U16:
                case U32:
                case U64:
                    Long lVal = Long.parseLong(value);
                    res = Long.toHexString(lVal);
                    break;
                case FLT:
                    Float floatValue = Float.parseFloat(value);

                    Integer iVal = Float.floatToIntBits(floatValue);
                    res = Integer.toHexString(iVal);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
