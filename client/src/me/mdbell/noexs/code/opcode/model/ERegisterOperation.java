package me.mdbell.noexs.code.opcode.model;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodePattern(pattern = "[0123]")
public enum ERegisterOperation implements ICodeFragment {
    RESTORE(0), SAVE(1), CLEAR_SAVED_VALUE(2), CLEAR_REGISTER(3);

    private int registerOperationCode;

    private ERegisterOperation(int registerOperationCode) {
        this.registerOperationCode = registerOperationCode;
    }

    public int getRegisterOperationCode() {
        return registerOperationCode;
    }

    @AOpCodeFragmentConversion
    public static ERegisterOperation valueFromFragment(String fragment) {
        ERegisterOperation res = null;
        for (ERegisterOperation dt : ERegisterOperation.values()) {
            if (dt.registerOperationCode == Integer.parseInt(fragment)) {
                res = dt;
                break;
            }
        }
        return res;
    }

    @Override
    public String encode() {
        return Integer.toString(registerOperationCode);
    }

}
