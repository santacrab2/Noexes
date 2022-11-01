package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.CodeUtils;

public class Code {

	private String label;
	private WriteValue writeValue;

	public Code(String label, WriteValue writeValue) {
		super();
		this.label = CodeUtils.getStringLitteral(label);
		this.writeValue = writeValue;
	}

	public WriteValue getWriteValue() {
		return writeValue;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
