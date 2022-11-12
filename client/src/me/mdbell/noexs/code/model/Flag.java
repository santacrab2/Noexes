package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;

@ARevPattern(pattern = "[01]")
public class Flag implements ICodeFragment {
    private boolean flag;

    public Flag(boolean flag) {
        super();
        this.flag = flag;
    }

    @ARevFragmentConversion
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
