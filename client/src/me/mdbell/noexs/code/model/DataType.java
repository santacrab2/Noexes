package me.mdbell.noexs.code.model;

public enum DataType {
	T8((short) 1), T16((short) 2), T32((short) 4), T64((short) 8);

	private short pointerDataType;

	private DataType(short pointerDataType) {
		this.pointerDataType = pointerDataType;
	}

	public short getPointerDataType() {
		return pointerDataType;
	}

	public short getDataTypeSize() {
		return (short)(pointerDataType * 2);
	}

}
