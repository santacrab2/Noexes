package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

//	#### Arithmetic Types
//	+ 0: Addition
//	+ 1: Subtraction
//	+ 2: Multiplication
//	+ 3: Left Shift
//	+ 4: Right Shift
//	+ 5: Logical And
//	+ 6: Logical Or
//	+ 7: Logical Not (discards right-hand operand)
//	+ 8: Logical Xor
//	+ 9: None/Move (discards right-hand operand)
//	
//	---



public enum ArithmeticOperation {
	ADDITION("+", 0), SUBSTRACTION("-", 1), MULTIPLICATION("*", 2), LEFT_SHIFT("<<", 3), RIGHT_SHIFT(">>", 4),
	LOGICAL_AND("&&", 5), LOGICAL_OR("||", 6), LOGICAL_NOT("!", 7), LOGICAL_XOR("^", 8), NONE_MOVE("_", 9);

	private String symbol;

	private int arithmeticOperationCode;

	private ArithmeticOperation(String symbol, int arithmeticOperationCode) {
		this.symbol = symbol;
		this.arithmeticOperationCode = arithmeticOperationCode;
	}

	public int getArithmeticOperationCode() {
		return arithmeticOperationCode;
	}

	public static ArithmeticOperation getArithmeticOperationFromSymbol(String symbol) {
		ArithmeticOperation res = null;
		for (ArithmeticOperation at : ArithmeticOperation.values()) {
			if (StringUtils.equals(symbol, at.symbol)) {
				res = at;
				break;
			}
		}
		return res;
	}

}
