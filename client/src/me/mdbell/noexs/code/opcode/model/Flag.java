package me.mdbell.noexs.code.opcode.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodePattern(pattern = "[01]")
public class Flag implements ICodeFragment {
    private boolean flag;

    public Flag(boolean flag) {
        super();
        this.flag = flag;
    }

    @AOpCodeFragmentConversion
    public static Flag valueFromFragment(String fragment) {

        return new Flag(StringUtils.equals(fragment, "1"));
    }

    public boolean getFlag() {
        return flag;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public String encode() {
        return flag ? "1" : "0";
    }

}
