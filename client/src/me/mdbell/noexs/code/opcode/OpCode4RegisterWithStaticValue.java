package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.Register;
import me.mdbell.noexs.code.opcode.model.Value;

//Code Type 0x4: Load Register with Static Value
//
//Code type 0x4 allows setting a register to a constant value.
//Encoding
//
//400R0000 VVVVVVVV VVVVVVVV
//
//    R: Register to use.
//    V: Value to load.
//
//--- 

@AOpCodeOperation(operation = EOpCode.END_CONDITIONAL_BLOCK)
public class OpCode4RegisterWithStaticValue extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    @AOpCodePattern(pattern = "00")
    private NoOp noOp = new NoOp("00");

    @AOpCodeFieldOrder(order = 2)
    private Register register;

    @AOpCodeFieldOrder(order = 3)
    @AOpCodePattern(pattern = "00000")
    private NoOp noOp2 = new NoOp("00000");

    @AOpCodeFieldOrder(order = 3)
    private Value value;

    public Register getRegister() {
        return register;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

}
