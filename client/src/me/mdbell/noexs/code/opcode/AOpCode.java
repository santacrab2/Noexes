package me.mdbell.noexs.code.opcode;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;

public abstract class AOpCode {

    transient AOpCode nextOperation;

    transient AOpCode previousOperation;

    public AOpCode getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(AOpCode nextOperation) {
        this.nextOperation = nextOperation;
    }

    public AOpCode getPreviousOperation() {
        return previousOperation;
    }

    public void setPreviousOperation(AOpCode previousOperation) {
        this.previousOperation = previousOperation;
    }

    public EOperation getOperation() {
        return this.getClass().getAnnotation(AOpCodeOperation.class).operation();
    }

    public abstract String abstractInstruction();

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, false);
    }

}
