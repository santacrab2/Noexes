package me.mdbell.noexs.code.model;

import me.mdbell.noexs.code.opcode.model.EKeypad;

public class ConditionPressButton extends Condition {

    private EKeypad[] keypad;

    // TODO : boutton multiple
    public ConditionPressButton(EKeypad keypad) {
        super();
        this.keypad = new EKeypad[] { keypad };
    }

    public EKeypad[] getKeypad() {
        return keypad;
    }
}
