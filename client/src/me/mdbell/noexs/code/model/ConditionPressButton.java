package me.mdbell.noexs.code.model;

public class ConditionPressButton extends Condition {

    private Keypad[] keypad;

    // TODO : boutton multiple
    public ConditionPressButton(Keypad keypad) {
        super();
        this.keypad = new Keypad[] { keypad };
    }

    public Keypad[] getKeypad() {
        return keypad;
    }
}
