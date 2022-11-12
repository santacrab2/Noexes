package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;

@ARevPattern(pattern = "[0-9A-F]")
public class Register implements ICodeFragment {
    private String register;

    public Register(String register) {
        super();
        this.register = register;
    }

    @ARevFragmentConversion
    public static Register valueFromFragment(String fragment) {
        return new Register(fragment);
    }

    public String getRegister() {
        return register;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String encode() {
        return this.register;
    }

}
