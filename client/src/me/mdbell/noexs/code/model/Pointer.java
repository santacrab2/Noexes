package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.util.HexUtils;

public class Pointer {

    private Pointer pointer;

    private MemoryRegion memoryRegion;

    private ArithmeticOperation arithmeticOperation;

    private Long offset;

    public Pointer() {

    }

    public Pointer(Pointer pointer) {
        super();
        this.pointer = pointer;
    }

    public Pointer(Pointer pointer, ArithmeticOperation arithmeticType, String offsetStr) {
        super();
        this.pointer = pointer;
        this.arithmeticOperation = arithmeticType;
        this.offset = HexUtils.fromString(offsetStr);
    }

    public Pointer(MemoryRegion MemoryRegion, String offsetStr) {
        super();
        this.memoryRegion = MemoryRegion;
        this.arithmeticOperation = ArithmeticOperation.ADDITION;
        this.offset = HexUtils.fromString(offsetStr);
    }

    public Pointer getPointer() {
        return pointer;
    }

    public MemoryRegion getMemoryRegion() {
        return memoryRegion;
    }

    public ArithmeticOperation getArithmeticOperation() {
        return arithmeticOperation;
    }

    public Long getOffset() {
        return offset;
    }

    public String getOffsetAsHex() {
        return Long.toHexString(offset);
    }

    public boolean isPositionFirst() {
        return pointer == null;
    }

    public MemoryRegion getInheritedMemoryRegion() {
        MemoryRegion res = memoryRegion;
        if (res == null) {
            res = pointer.getInheritedMemoryRegion();
        }

        return res;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
