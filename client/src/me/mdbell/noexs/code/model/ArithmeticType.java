package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

public enum ArithmeticType {
	ADDITION("+", (short) 0), SUBSTRACTION("-", (short) 1);

	private String symbol;

	private short pointerArithmeticType;

	private ArithmeticType(String symbol, short pointerArithmeticType) {
		this.symbol = symbol;
		this.pointerArithmeticType = pointerArithmeticType;
	}

	
	
	public short getPointerArithmeticType() {
		return pointerArithmeticType;
	}



	public static ArithmeticType getArithmeticTypeFromSymbol(String symbol) {
		ArithmeticType res = null;
		for (ArithmeticType at : ArithmeticType.values()) {
			if (StringUtils.equals(symbol, at.symbol)) {
				res = at;
				break;
			}
		}
		return res;
	}

}
