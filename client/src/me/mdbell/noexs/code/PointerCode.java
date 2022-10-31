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
		return OperationBuilder.storeStaticValueToRegisterMemoryAddress(wv.getValueType().getDataType(), registerToUse,
				false, false, ' ', wv.getHexValue());
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

		if (positionTypeFirst) {
			res = OperationBuilder.loadRegisterWithMemoryValueFromFixedAddress(DataType.T64,
					p.getInheritedMemoryRegion(), registerToUse, p.getOffsetAsHex());
		} else {
			res = OperationBuilder.loadRegisterWithMemoryValueFromRegisterAddress(DataType.T64, registerToUse,
					p.getOffsetAsHex());
		}
		return res;
	}

	private String generatePointerMove(Pointer p) {
		return OperationBuilder.legacyArithmetic(DataType.T32, registerToUse, p.getArithmeticOperation(),
				p.getOffsetAsHex());
	}

}
