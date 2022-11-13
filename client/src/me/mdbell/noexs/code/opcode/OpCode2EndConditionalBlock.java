package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EEndType;
import me.mdbell.noexs.code.opcode.model.NoOp;

//### Code Type 0x2: End Conditional Block
//Code type 0x2 marks the end of a conditional block (started by Code Type 0x1 or Code Type 0x8).
//
//When an Else is executed, all instructions until the appropriate End conditional block terminator are skipped.
//
//#### Encoding
//`2X000000`
//
//+ X: End type (0 = End, 1 = Else).
//
//--- 

@AOpCodeOperation(operation = EOpCode.END_CONDITIONAL_BLOCK)
public class OpCode2EndConditionalBlock extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    private EEndType endType;

    @AOpCodeFieldOrder(order = 2)
    @AOpCodePattern(pattern = "000000")
    private NoOp noOp = new NoOp("000000");

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

    public static OpCode2EndConditionalBlock endConditionalEnd() {
        OpCode2EndConditionalBlock res = new OpCode2EndConditionalBlock();
        res.endType = EEndType.END;
        return res;
    }

    public static OpCode2EndConditionalBlock endConditionalElse() {
        OpCode2EndConditionalBlock res = new OpCode2EndConditionalBlock();
        res.endType = EEndType.ELSE;
        return res;
    }

}
