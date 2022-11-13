package me.mdbell.noexs.code.opcode;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.model.Value;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodeOperation(operation = EOperation.LOAD_REGISTER_WITH_STATIC_VALUE)
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
