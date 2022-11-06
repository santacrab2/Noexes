package me.mdbell.util;

import org.apache.commons.lang3.StringUtils;

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

	public static long fromString(String hexString) {
		return Long.valueOf(StringUtils.removeIgnoreCase(hexString, "0x"), 16);
	}

}
