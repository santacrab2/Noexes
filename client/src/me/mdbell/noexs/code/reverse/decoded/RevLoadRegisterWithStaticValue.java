package me.mdbell.noexs.code.reverse.decoded;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.model.Value;
import me.mdbell.noexs.code.reverse.annotation.ARevFieldOrder;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;

@ARevOperation(operation = EOperation.LOAD_REGISTER_WITH_STATIC_VALUE)
public class RevLoadRegisterWithStaticValue extends ADecodedOperation {

    @ARevFieldOrder(order = 1)
    @ARevPattern(pattern = "00")
    private NoOp noOp = new NoOp("00");

    @ARevFieldOrder(order = 2)
    private Register register;

    @ARevFieldOrder(order = 3)
    @ARevPattern(pattern = "00000")
    private NoOp noOp2 = new NoOp("00000");

    @ARevFieldOrder(order = 3)
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
