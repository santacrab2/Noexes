package me.mdbell.noexs.code.opcode.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodePattern(pattern = "[\\w\\s]+")
public class StringValue implements ICodeFragment {
    private String stringValue;

    public StringValue(String stringValue) {
        super();
        this.stringValue = stringValue;
    }

    @AOpCodeFragmentConversion
    public static StringValue valueFromFragment(String fragment) {

        return new StringValue(fragment);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String encode() {
        return stringValue;
    }

}
