package me.mdbell.noexs.ui.models;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.core.EMemoryRegion;

public class MemoryInfoUtils {

    public static EMemoryRegion getAddressMemoryRegion(long address, List<MemoryInfoTableModel> memoryInfos) {
        EMemoryRegion res = null;
        for (MemoryInfoTableModel memroyInfo : memoryInfos) {
            if (address >= memroyInfo.getAddr() && address <= memroyInfo.getEnd()) {
                res = memroyInfo.getMemoryRegion();
                break;
            }
        }

        return res;
    }

    public static long getOffset(long address, EMemoryRegion region, Map<String, Long> regionAddress) {
        String memoryStartKey = StringUtils.lowerCase(region.name());
        long regionStart = regionAddress.get(memoryStartKey);
        long res = address - regionStart;
        return res;
    }
}
