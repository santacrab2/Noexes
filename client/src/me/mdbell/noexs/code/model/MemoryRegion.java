package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

/**
 * M: Memory region to write to (0 = Main NSO, 1 = Heap, 2 = Alias, 3 = Aslr).
 * 
 * @author Anthony
 *
 */
public enum MemoryRegion {
    MAIN(0), HEAP(1), ALIAS(2), ASLR(3);

    private int memoryRegionCode;

    private MemoryRegion(int memoryRegionCode) {
        this.memoryRegionCode = memoryRegionCode;
    }

    public int getPointerAdressType() {
        return memoryRegionCode;
    }

    public static MemoryRegion getMemoryRegion(String memoryRegion) {
        return MemoryRegion.valueOf(StringUtils.upperCase(memoryRegion));
    }

}
