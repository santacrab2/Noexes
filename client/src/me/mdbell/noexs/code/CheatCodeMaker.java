package me.mdbell.noexs.code;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import me.mdbell.noexs.code.model.Code;
import me.mdbell.noexs.code.model.CodeLines;
import me.mdbell.noexs.code.model.DataType;
import me.mdbell.noexs.code.model.Pointer;
import me.mdbell.noexs.code.model.WriteValue;
import me.mdbell.noexs.code.parser.CodeLexer;
import me.mdbell.noexs.code.parser.CodeParser;
import me.mdbell.noexs.code.parser.CodeParser.CodeContext;

public class CheatCodeMaker {

	private static char DEFAULT_REGISTER_TO_USE = 'F';

	private char registerToUse;

	private Code c;

	public CheatCodeMaker(Code c, char registerToUse) {
		super();
		this.registerToUse = registerToUse;
		this.c = c;
	}

	public CheatCodeMaker(Code c) {
		this(c, DEFAULT_REGISTER_TO_USE);
	}

	public CodeLines generateCode() {
		WriteValue wv = c.getWriteValue();
		CodeLines res = new CodeLines("[" + c.getLabel() + "]");
		res.addCodeLines(generateMovesPartForPointer(wv.getPointer(), true));
		res.addLineToEnd(generateSetValuePartForPointer(wv));
		return res;
	}

	private String generateSetValuePartForPointer(WriteValue wv) {
		return OperationBuilder.storeStaticValueToRegisterMemoryAddress(wv.getValueType().getDataType(), registerToUse,
				false, false, ' ', wv.getHexValue());
	}

	private CodeLines generateMovesPartForPointer(Pointer p, boolean global) {
		CodeLines res = new CodeLines();
		if (!p.isPositionFirst()) {
			if (global) {
				if (p.getOffset() == null) {
					res.addCodeLines(generateMovesPartForPointer(p.getPointer(), false));
				} else {
					res.addCodeLines(generateMovesPartForPointer(p.getPointer(), false));
					res.addLineToEnd(generatePointerMove(p));
				}
			} else {
				res.addCodeLines(generateMovesPartForPointer(p.getPointer(), false));
				res.addLineToEnd(generatePointerJump(p, false));
			}
		} else {
			res.addLineToEnd(generatePointerJump(p, true));
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

	public static String generateCodeFromString(String cheatSource) {

		CodePointCharStream input = CharStreams.fromString(cheatSource);
		CodeLexer lexer = new CodeLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CodeParser parser = new CodeParser(tokens);
		parser.setBuildParseTree(true);
		CodeContext tree = parser.code();
		CheatCodeMaker pc = new CheatCodeMaker(tree.c);
		CodeLines codeLines = pc.generateCode();
		return codeLines.toStringCode();

	}
}
