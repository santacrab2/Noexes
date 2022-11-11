package me.mdbell.noexs.ui.models;

public enum ConversionType {
    U8("Unsigned 8", 1),
    U16("Unsigned 16", 2),
    U32("Unsigned 32", 4),
    U64("Unsigned 64", 8),
    S8("Signed 8", 1),
    S16("Signed 16", 2),
    S32("Signed 32", 4),
    S64("Signed 64", 8),
    FLT("Float", 4),
    ADDR("Address", 5);

    String readable;
    int length;

    ConversionType(String str, int length) {
        this.readable = str;
        this.length = length;
    }

    public String toString() {
        return readable;
    }

    public int getLength() {
        return length;
    }

}
