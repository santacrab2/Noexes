package me.mdbell.noexs.code;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.model.Code;
import me.mdbell.noexs.code.model.DataType;
import me.mdbell.noexs.code.model.Pointer;
import me.mdbell.noexs.code.model.WriteValue;

public class PointerCode {

	private static char DEFAULT_REGISTER_TO_USE = 'F';

	private char registerToUse;

	private Code c;

	public PointerCode(Code c, char registerToUse) {
		super();
		this.registerToUse = registerToUse;
		this.c = c;
	}

	public PointerCode(Code c) {
		this(c, DEFAULT_REGISTER_TO_USE);
	}

	public String generateCode() {
		WriteValue wv = c.getWriteValue();
		String res = "";

		res += generateMovesPartForPointer(wv.getPointer(), true);
		res += "\n" + generateSetValuePartForPointer(wv);
		res = StringUtils.trim(res);
		return res;
	}

	private String generateSetValuePartForPointer(WriteValue wv) {
		String commandStart = "6" + wv.getValueType().getDataType().getPointerDataType() + "0" + registerToUse + "0000";
		return commandStart + " " + PointerUtils.padPointer(wv.getHexValue(), wv.getValueType().getDataType(), 16);

	}

	private String generateMovesPartForPointer(Pointer p, boolean global) {
		String res = "";
		if (!p.isPositionFirst()) {
			if (global) {
				if (p.getOffset() == null) {
					res += generateMovesPartForPointer(p.getPointer(), false);
				} else {
					res += generateMovesPartForPointer(p.getPointer(), false);
					res += "\n" + generatePointerMove(p);
				}
			} else {
				res += "\n" + generateMovesPartForPointer(p.getPointer(), false);
				res += "\n" + generatePointerJump(p, false);
			}
		} else {
			res = generatePointerJump(p, true);
		}

		return res;
	}

	private String generatePointerJump(Pointer p, boolean positionTypeFirst) {
		String res = null;

		String positionType = "";
		if (positionTypeFirst) {
			positionType = "0";
		} else {
			positionType = "1";
		}
		String commandStart = "58" + p.getInheritedAddressType().getPointerAdressType() + registerToUse + positionType
				+ "0";
		res = commandStart + PointerUtils.padPointer(p.getOffsetAsHex(), DataType.T64, 10);

		return res;
	}

	private String generatePointerMove(Pointer p) {
		String commandStart = "780" + registerToUse + p.getArithmeticType().getPointerArithmeticType() + "0";
		String res = commandStart + PointerUtils.padPointer(p.getOffsetAsHex(), DataType.T64, 10);
		return res;
	}

}
