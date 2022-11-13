package me.mdbell.noexs.code.opcode.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.mdbell.noexs.code.opcode.EOpCode;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AOpCodeOperation {
    public EOpCode operation();
}
