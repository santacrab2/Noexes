package me.mdbell.noexs.code.opcode.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;

public class OpCodeOperationFragment {

    private String fragmentPattern;
    private Field field;
    private Method builder;
    private int order;

    public OpCodeOperationFragment(String fragmentPattern, Field field, Method builder) {
        super();
        this.fragmentPattern = fragmentPattern;
        this.field = field;
        this.builder = builder;
        this.order = field.getAnnotation(AOpCodeFieldOrder.class).order();

    }

    public String getFragmentPattern() {
        return fragmentPattern;
    }

    public Field getField() {
        return field;
    }

    public Method getBuilder() {
        return builder;
    }

    public int getOrder() {
        return order;
    }
}
