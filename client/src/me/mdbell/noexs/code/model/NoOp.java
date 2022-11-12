package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;

public class NoOp implements ICodeFragment {

    private String noOp;

    public NoOp(String noOp) {
        super();
        this.noOp = noOp;
    }

    public String getNoOp() {
        return noOp;
    }

    @ARevFragmentConversion
    public static NoOp valueFromFragment(String fragment) {
        return new NoOp(fragment);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public String encode() {
        return noOp;
    }

}
