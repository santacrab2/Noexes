package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Code {

	private WriteValue writeValue;

	public Code(WriteValue writeValue) {
		super();
		this.writeValue = writeValue;
	}

	public WriteValue getWriteValue() {
		return writeValue;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
