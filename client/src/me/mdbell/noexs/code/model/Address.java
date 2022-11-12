package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;
import me.mdbell.util.HexUtils;

@ARevPattern(pattern = "[0-9A-F]{2} [0-9A-F]{8}")
public class Address implements ICodeFragment {
    private long address;

    public Address(long address) {
        super();
        this.address = address;
    }

    @ARevFragmentConversion
    public static Address valueFromFragment(String fragment) {

        String frLong = RegExUtils.removeAll(fragment, "\s");
        return new Address(Long.parseUnsignedLong(frLong, 16));
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public long getAddress() {
        return address;
    }

    @Override
    public String encode() {
        return HexUtils.formatAddress(address);
    }

}
