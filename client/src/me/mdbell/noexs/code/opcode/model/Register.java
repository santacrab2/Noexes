package me.mdbell.noexs.code.opcode.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodePattern(pattern = "[0-9A-F]")
public class Register implements ICodeFragment {
    private String register;

    public Register(String register) {
        super();
        this.register = register;
    }

    @AOpCodeFragmentConversion
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
