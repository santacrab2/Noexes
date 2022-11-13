package me.mdbell.noexs.code.opcode.model;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

/**
 * T: Width of memory write (1, 2, 4, or 8 bytes).
 * 
 * @author Anthony
 *
 */
@AOpCodePattern(pattern = "[1248]")
public enum EDataType implements ICodeFragment {
    T8(1, 2, 8), T16(2, 4, 8), T32(4, 8, 8), T64(8, 16, 16), ADDR(8, 10, 10);

    private int dataTypeCode;
    private int size;
    private int gabarit;

    private EDataType(int dataTypeCode, int size, int gabarit) {
        this.dataTypeCode = dataTypeCode;
        this.gabarit = gabarit;
        this.size = size;
    }

    public int getDataTypeCode() {
        return dataTypeCode;
    }

    public int getGabarit() {
        return gabarit;
    }

    public int getDataTypeSize() {
        return size;
    }

    @AOpCodeFragmentConversion
    public static EDataType valueFromFragment(String fragment) {
        EDataType res = null;
        for (EDataType dt : EDataType.values()) {
            if (dt.dataTypeCode == Integer.parseInt(fragment)) {
                res = dt;
                break;
            }
        }
        return res;
    }

    @Override
    public String encode() {
        return Integer.toString(dataTypeCode);
    }

}
