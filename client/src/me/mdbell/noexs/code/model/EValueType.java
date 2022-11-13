package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.opcode.model.EDataType;

public enum EValueType {
    U8(EDataType.T8),
    S8(EDataType.T8),
    U16(EDataType.T16),
    S16(EDataType.T16),
    U32(EDataType.T32),
    S32(EDataType.T32),
    U64(EDataType.T64),
    S64(EDataType.T64),
    FLT(EDataType.T32),
    DBL(EDataType.T64),
    PTR(EDataType.ADDR);

    private EDataType dataType;

    private EValueType(EDataType dataType) {
        this.dataType = dataType;
    }

    public EDataType getDataType() {
        return dataType;
    }

    public static EValueType getValueType(String valueType) {
        return EValueType.valueOf(StringUtils.upperCase(valueType));
    }
}
