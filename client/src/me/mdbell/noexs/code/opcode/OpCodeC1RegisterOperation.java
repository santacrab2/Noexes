package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.ERegisterOperation;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodeOperation(operation = EOperation.SAVE_OR_RESTORE_REGISTER)
public class OpCodeC1RegisterOperation extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp = new NoOp("0");;

    @AOpCodeFieldOrder(order = 2)
    private Register registerDestination;

    @AOpCodeFieldOrder(order = 3)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp2 = new NoOp("0");;

    @AOpCodeFieldOrder(order = 4)
    private Register registerSource;

    @AOpCodeFieldOrder(order = 5)
    private ERegisterOperation registerOperation;

    @AOpCodeFieldOrder(order = 6)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp3 = new NoOp("0");;

    public Register getRegisterDestination() {
        return registerDestination;
    }

    public Register getRegisterSource() {
        return registerSource;
    }

    public ERegisterOperation getRegisterOperation() {
        return registerOperation;
    }

    @Override
    public String abstractInstruction() {
        StringBuilder strB = new StringBuilder();

        if (previousOperation != null) {
            strB.append(previousOperation.abstractInstruction());
            strB.append("\n");
        }
        strB.append(registerOperation);
        strB.append(" FROM ");
        strB.append(registerSource.getRegister());
        strB.append(" TO ");
        strB.append(registerDestination.getRegister());

        return strB.toString();
    }

}
