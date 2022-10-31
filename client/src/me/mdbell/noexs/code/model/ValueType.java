package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

public enum ValueType {
	U8(DataType.T8), S8(DataType.T8), U16(DataType.T16), S16(DataType.T16), U32(DataType.T32), S32(DataType.T32),
	U64(DataType.T64), S64(DataType.T64), FLT(DataType.T32), DBL(DataType.T64), PTR(DataType.ADDR);

	private DataType dataType;

	private ValueType(DataType dataType) {
		this.dataType = dataType;
	}
	
	


	public DataType getDataType() {
		return dataType;
	}




	public static ValueType getValueType(String valueType) {
		return ValueType.valueOf(StringUtils.upperCase(valueType));
	}
}
