package me.mdbell.noexs.ui.services;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.core.EMemoryRegion;
import me.mdbell.noexs.ui.controllers.ToolsController;
import me.mdbell.util.HexUtils;

public class PointerSearchResult implements Comparable<PointerSearchResult>, Cloneable {

    PointerSearchResult prev;
    int depth;
    long address;
    long offset;

    public PointerSearchResult(long address, long offset) {
        this.address = address;
        this.offset = offset;
        this.depth = 0;
    }

    public String formattedRegion(ToolsController tc) {
        EMemoryRegion region = tc.getAddressMemoryRegion(address);
        long offset = tc.getOffset(address, region);
        String base = region + " + 0x" + HexUtils.formatLong(offset);
        return formatted(base);
    }

    public String formattedMain(long main) {
        long rel = address - main;
        String base = "main" + (rel >= 0 ? "+" : "-") + Long.toUnsignedString(Math.abs(rel), 16);
        return formatted(base);
    }

    public String formattedRaw() {
        String base = HexUtils.formatAddress(address);
        return formatted(base);

    }

    public String formatted(String base) {
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        PointerSearchResult psr = prev;
        while (psr != null) {
            prefix.append('[');
            suffix.append(']');
            if (psr.offset != 0) {
                suffix.append(' ').append(prev.offset < 0 ? '-' : '+').append(" ")
                        .append(Long.toUnsignedString(Math.abs(prev.offset), 16));
            }

            psr = prev.prev;
        }

        String str = "[" + base + "]";

        if (offset != 0) {
            str = str + " " + (offset < 0 ? " - " : "+ ") + Long.toUnsignedString(Math.abs(offset), 16);
        }

        return prefix.toString() + str + suffix.toString();
    }

    @Override
    public int compareTo(PointerSearchResult o) {
        int depthDiff = depth - o.depth;
        if (depthDiff != 0) {
            return depthDiff;
        }
        long addrDiff = address = o.address;
        if (addrDiff != 0) {
            return (int) addrDiff;
        }

        long offsetDiff = offset - o.offset;
        if (offsetDiff != 0) {
            return (int) offsetDiff;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PointerSearchResult that = (PointerSearchResult) o;
        return that.formattedRaw().equals(formattedRaw()); // TODO not this.
    }

    @Override
    public int hashCode() {
        return formattedRaw().hashCode();
    }

    @Override
    protected Object clone() {
        PointerSearchResult psr = new PointerSearchResult(address, offset);
        psr.depth = depth;
        psr.prev = prev == null ? null : (PointerSearchResult) prev.clone();
        return psr;
    }

    public long getAddress() {
        return address;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
