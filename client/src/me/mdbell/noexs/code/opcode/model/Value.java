package me.mdbell.noexs.code.opcode.model;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.ICodeFragmentWithVariableLength;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.util.HexUtils;

@AOpCodePattern(pattern = "[0-9A-F]{8} [0-9A-F]{8}")
public class Value implements ICodeFragmentWithVariableLength {
    private long value;

    public Value(long value) {
        super();
        this.value = value;
    }

    @AOpCodeFragmentConversion
    public static Value valueFromFragment(String fragment) {

        String frLong = RegExUtils.removeAll(fragment, "\s");
        return new Value(Long.parseLong(frLong, 16));
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public long getValue() {
        return value;
    }

    @Override
    public String encode(EDataType datatype) {
        return HexUtils.format(datatype, value, false);
    }

}
