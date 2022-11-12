package me.mdbell.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.ui.models.DataType;
import me.mdbell.noexs.ui.models.EAccessType;

public class HexUtils {

    private HexUtils() {

    }

    public static String formatAddress(long addr) {
        return pad('0', 10, Long.toUnsignedString(addr, 16).toUpperCase());
    }

    public static String formatSize(long size) {
        return "0x" + pad('0', 10, Long.toUnsignedString(size, 16).toUpperCase());
    }

    public static String pad(char with, int len, String str) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.insert(0, with);
        }
        return sb.toString();
    }

    public static String formatInt(int value) {
        return pad('0', 8, Integer.toUnsignedString(value, 16).toUpperCase());
    }

    public static String formatLong(long value) {
        return pad('0', 8, Long.toUnsignedString(value, 16).toUpperCase());
    }

    public static String format(DataType dataType, long value, boolean hexPrefix) {
        String res = pad('0', dataType.getSize(), Long.toUnsignedString(value, 16).toUpperCase());
        if (hexPrefix) {
            res = "0x" + res;
        }
        return res;
    }

    public static String format(EDataType dataType, long value, boolean hexPrefix) {
        String res = pad('0', dataType.getDataTypeSize(), Long.toUnsignedString(value, 16).toUpperCase());
        if (hexPrefix) {
            res = "0x" + res;
        }
        return res;
    }

    public static long readFromString(DataType dataType, String value) {
        return Long.parseLong(StringUtils.removeStartIgnoreCase(value, "0x"), 16);

    }

    public static String formatTitleId(long titleId) {
        return pad('0', 16, Long.toUnsignedString(titleId, 16).toUpperCase());
    }

    public static String formatAccess(int access) {
        StringBuilder sb = new StringBuilder();

        for (EAccessType accesType : EAccessType.values()) {
            if (accesType.hasAcces(access)) {
                sb.append(accesType.getShortDesc());
            } else {
                sb.append('-');
            }
        }

        return sb.toString();
    }

    public static String formatBlockOf(String hexCode, int blockSize) {
        String res = RegExUtils.removeAll(hexCode, "\s");
        List<String> stringFrag = new ArrayList<>();
        for (int i = 0; i < hexCode.length(); i += blockSize) {
            stringFrag.add(StringUtils.substring(res, i, i + blockSize));
        }
        return StringUtils.join(stringFrag, " ");
    }

    public static long fromString(String hexString) {
        return Long.valueOf(StringUtils.removeIgnoreCase(hexString, "0x"), 16);
    }

}
