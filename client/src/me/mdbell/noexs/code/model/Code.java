package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.CodeUtils;

public class Code {

    private String label;
    private WriteValue writeValue;
    private Block block;

    public Code() {
        super();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabelWithQuotes(String label) {
        this.label = CodeUtils.getStringLitteral(label);
    }

    public void setWriteValue(WriteValue writeValue) {
        this.writeValue = writeValue;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public WriteValue getWriteValue() {
        return writeValue;
    }

    public String getLabel() {
        return label;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
