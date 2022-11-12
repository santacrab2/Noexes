package me.mdbell.noexs.code.reverse.decoded;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.ERegisterOperation;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.reverse.annotation.ARevFieldOrder;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;

@ARevOperation(operation = EOperation.SAVE_OR_RESTORE_REGISTER)
public class RevRegisterOperation extends ADecodedOperation {

    @ARevFieldOrder(order = 1)
    @ARevPattern(pattern = "0")
    private NoOp noOp = new NoOp("0");;

    @ARevFieldOrder(order = 2)
    private Register registerDestination;

    @ARevFieldOrder(order = 3)
    @ARevPattern(pattern = "0")
    private NoOp noOp2 = new NoOp("0");;

    @ARevFieldOrder(order = 4)
    private Register registerSource;

    @ARevFieldOrder(order = 5)
    private ERegisterOperation registerOperation;

    @ARevFieldOrder(order = 6)
    @ARevPattern(pattern = "0")
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
