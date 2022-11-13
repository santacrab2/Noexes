package me.mdbell.noexs.code.opcode.model;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;

@AOpCodePattern(pattern = "[01]")
public enum EEndType implements ICodeFragment {
    END(0), ELSE(1);

    private int registerOperationCode;

    private EEndType(int registerOperationCode) {
        this.registerOperationCode = registerOperationCode;
    }

    public int getRegisterOperationCode() {
        return registerOperationCode;
    }

    @AOpCodeFragmentConversion
    public static EEndType valueFromFragment(String fragment) {
        EEndType res = null;
        for (EEndType dt : EEndType.values()) {
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
