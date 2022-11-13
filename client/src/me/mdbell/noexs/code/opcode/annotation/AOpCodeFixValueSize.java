package me.mdbell.noexs.code.opcode.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.core.debugger.EDebDataType;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AOpCodeFixValueSize {
    public EDataType dataType();
}
