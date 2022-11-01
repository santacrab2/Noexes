package me.mdbell.noexs.code;

import org.apache.commons.lang3.StringUtils;

public class CodeUtils {

	public static String getStringLitteral(String rawStr) {
		return StringUtils.removeEnd(StringUtils.removeStart(rawStr, "\""), "\"");
	}
}
