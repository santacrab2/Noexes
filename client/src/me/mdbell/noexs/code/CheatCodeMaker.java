package me.mdbell.noexs.code;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import me.mdbell.noexs.code.model.Block;
import me.mdbell.noexs.code.model.Code;
import me.mdbell.noexs.code.model.CodeLines;
import me.mdbell.noexs.code.model.Codes;
import me.mdbell.noexs.code.model.ConditionPressButton;
import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.ElseCondition;
import me.mdbell.noexs.code.model.EndCondition;
import me.mdbell.noexs.code.model.IInstruction;
import me.mdbell.noexs.code.model.Pointer;
import me.mdbell.noexs.code.model.WriteValue;
import me.mdbell.noexs.code.opcode.AOpCode;
import me.mdbell.noexs.code.opcode.OpCode2EndConditionalBlock;
import me.mdbell.noexs.code.opcode.OpCode5LoadRegisterWithMemory;
import me.mdbell.noexs.code.opcode.OpCode6StoreStaticValueToRegisterMemoryAddress;
import me.mdbell.noexs.code.opcode.OpCode7LegacyArithmetic;
import me.mdbell.noexs.code.opcode.manager.OpCodeOperationBuilder;
import me.mdbell.noexs.code.parser.CodeLexer;
import me.mdbell.noexs.code.parser.CodeParser;
import me.mdbell.noexs.code.parser.CodeParser.CodesContext;

public class CheatCodeMaker {

    private static String DEFAULT_REGISTER_TO_USE = "F";

    private String registerToUse;

    private Codes codes;

    public CheatCodeMaker(Codes codes, String registerToUse) {
        super();
        this.registerToUse = registerToUse;
        this.codes = codes;
    }

    public CheatCodeMaker(Codes codes) {
        this(codes, DEFAULT_REGISTER_TO_USE);
    }

    private CodeLines generateCode(Code c) {

        CodeLines res = new CodeLines("[" + c.getLabel() + "]");

        WriteValue wv = c.getWriteValue();
        if (wv != null) {
            res.addCodeLines(generateInstructionWriteValue(wv));
        }

        Block block = c.getBlock();
        if (block != null) {
            res.addCodeLines(generateInstructionBlock(block));
        }
        return res;
    }

    private CodeLines generateCode() {
        CodeLines res = new CodeLines();
        List<Code> codesToGenerate = codes.getCodes();
        for (Code c : codesToGenerate) {
            res.addCodeLines(generateCode(c));
        }
        return res;
    }

    private CodeLines generateInstructionWriteValue(WriteValue wv) {
        CodeLines res = new CodeLines();
        res.addCodeLines(generateMovesPartForPointer(wv.getPointer(), true));
        res.addLineToEnd(generateSetValuePartForPointer(wv));
        return res;
    }

    private CodeLines generateInstructionBlock(Block block) {
        CodeLines res = new CodeLines();
        List<IInstruction> instructions = block.getInstructions();
        for (IInstruction instruction : instructions) {
            if (instruction instanceof WriteValue) {
                res.addCodeLines(generateInstructionWriteValue((WriteValue) instruction));
            } else if (instruction instanceof EndCondition) {
                res.addLineToEnd(OpCode2EndConditionalBlock.endConditionalEnd());
            } else if (instruction instanceof ElseCondition) {
                res.addLineToEnd(OpCode2EndConditionalBlock.endConditionalElse());
            } else if (instruction instanceof ConditionPressButton) {
                res.addLineToEnd(OpCodeOperationBuilder
                        .beginKeypressConditionalBlock(((ConditionPressButton) instruction).getKeypad()));
            }

            else {
                System.err.println("Unkonwn instruction : " + instruction);

            }

            /**
             * CodeLines codeLines = switch (instruction) { case WriteValue wv
             * ->generateInstructionWriteValue(WriteValue wv); }
             **/
        }
        return res;
    }

    private OpCode6StoreStaticValueToRegisterMemoryAddress generateSetValuePartForPointer(WriteValue wv) {
        return OpCode6StoreStaticValueToRegisterMemoryAddress.storeStaticValueToRegisterMemoryAddress(
                wv.getValueType().getDataType(), registerToUse, false, false, "0", wv.getLongValue());
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

    private AOpCode generatePointerJump(Pointer p, boolean positionTypeFirst) {
        AOpCode res = null;

        if (positionTypeFirst) {
            res = OpCode5LoadRegisterWithMemory.loadFromRegisterAddressEncoding(EDataType.T64,
                    p.getInheritedMemoryRegion(), registerToUse, p.getOffset());

        } else {
            res = OpCode5LoadRegisterWithMemory.loadFromFixedAddressEncoding(EDataType.T64, registerToUse,
                    p.getOffset());
        }

        return res;
    }

    private OpCode7LegacyArithmetic generatePointerMove(Pointer p) {
        return OpCode7LegacyArithmetic.legacyArithmetic(EDataType.T32, registerToUse, p.getArithmeticOperation(),
                p.getOffset());
    }

    public static String generateCodeFromString(String cheatSource) {

        CodePointCharStream input = CharStreams.fromString(cheatSource);
        CodeLexer lexer = new CodeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CodeParser parser = new CodeParser(tokens);
        parser.setBuildParseTree(true);
        CodesContext tree = parser.codes();
        // parser.setErrorHandler(null);
        CheatCodeMaker pc = new CheatCodeMaker(tree.cs);
        CodeLines codeLines = pc.generateCode();

        if (tree.exception != null) {
            throw new RuntimeException("Error while genearting code", tree.exception);
        }
        return codeLines.toStringCode();

    }
}
