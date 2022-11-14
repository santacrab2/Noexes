package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePadded;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.StringValue;

@AOpCodePadded(padded = false)
@AOpCodeOperation(operation = EOpCode.LABEL)
public class OpCodeLabel extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    StringValue stringValue;

    @AOpCodeFieldOrder(order = 2)
    @AOpCodePattern(pattern = "\\]")
    private NoOp noOp = new NoOp("]");

    @Override
    public String abstractInstruction() {
        return "\"" + stringValue + "\"";
    }

    public static OpCodeLabel label(String label) {
        OpCodeLabel res = new OpCodeLabel();
        res.stringValue = new StringValue(label);
        return res;
    }
}
