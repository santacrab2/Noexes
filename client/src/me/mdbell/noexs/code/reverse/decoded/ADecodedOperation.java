package me.mdbell.noexs.code.reverse.decoded;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;

public abstract class ADecodedOperation {

    transient ADecodedOperation nextOperation;

    transient ADecodedOperation previousOperation;

    public ADecodedOperation getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(ADecodedOperation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public ADecodedOperation getPreviousOperation() {
        return previousOperation;
    }

    public void setPreviousOperation(ADecodedOperation previousOperation) {
        this.previousOperation = previousOperation;
    }

    public EOperation getOperation() {
        return this.getClass().getAnnotation(ARevOperation.class).operation();
    }

    public abstract String abstractInstruction();

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, false);
    }

}
