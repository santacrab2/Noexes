package me.mdbell.noexs.code;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.model.DataType;

public class PointerUtils {

	public static String padPointer(String hexValue, DataType dataType, int digit) {

		int valueMaxSize = dataType.getDataTypeSize();
		int currentSize = StringUtils.length(hexValue);
		int startPos = Math.max(0, currentSize -valueMaxSize);
				
		String cappedValue = StringUtils.substring(hexValue, startPos, startPos+valueMaxSize);
		
		if (!StringUtils.equals(hexValue, cappedValue)) {
			System.err.println("Truncated value : " + hexValue + " to " + cappedValue);
		}
		
		//TODO : manage error 
		
		String paddedValue = StringUtils.leftPad(StringUtils.upperCase(cappedValue), digit, "0");
		int firstPartSize = digit - 8;

		String value = StringUtils.substring(paddedValue, 0, firstPartSize) + " "
				+ StringUtils.substring(paddedValue, firstPartSize, firstPartSize + 8);
		return value;

	}
}
