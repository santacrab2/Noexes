package me.mdbell.noexs.code.reverse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.mdbell.noexs.code.reverse.annotation.ARevFieldOrder;

public class CodeReverseOperationFragment {

    private String fragmentPattern;
    private Field field;
    private Method builder;
    private int order;

    public CodeReverseOperationFragment(String fragmentPattern, Field field, Method builder) {
        super();
        this.fragmentPattern = fragmentPattern;
        this.field = field;
        this.builder = builder;
        this.order = field.getAnnotation(ARevFieldOrder.class).order();

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
