package me.mdbell.noexs.code.opcode.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.mdbell.noexs.code.opcode.AOpCode;
import me.mdbell.noexs.code.opcode.EOpCode;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFixValueSize;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeMask;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePadded;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EDataType;
import me.mdbell.noexs.code.opcode.model.ICodeFragment;
import me.mdbell.noexs.code.opcode.model.ICodeFragmentMask;
import me.mdbell.noexs.code.opcode.model.ICodeFragmentWithVariableLength;
import me.mdbell.util.HexUtils;

public class OpCodeOperation {

    private static final Logger logger = LogManager.getLogger(OpCodeOperation.class);

    private EOpCode operation;
    private String regexp;
    private Class<? extends AOpCode> cls;
    private List<OpCodeOperationFragment> operationFragments = new ArrayList<>();

    public OpCodeOperation(Class<? extends AOpCode> cls, EOpCode operation) {
        super();
        this.operation = operation;
        this.cls = cls;
    }

    public static OpCodeOperation fromRevClass(Class<? extends AOpCode> cls) {
        String pattern = "";
        AOpCodeOperation anOperation = cls.getAnnotation(AOpCodeOperation.class);
        EOpCode op = anOperation.operation();
        OpCodeOperation res = new OpCodeOperation(cls, op);
        pattern += op.getPattern();

        Field[] fields = FieldUtils.getAllFields(cls);

        Map<Integer, OpCodeOperationFragment> checkOrder = new HashMap<>();
        for (Field field : fields) {
            Class<?> fieldCls = field.getType();
            Class<?> fieldClsNoArray = field.getType();
            if (fieldClsNoArray.isArray()) {
                fieldClsNoArray = fieldClsNoArray.componentType();
            }
            AOpCodePattern anPattern = field.getAnnotation(AOpCodePattern.class);
            if (anPattern == null) {
                anPattern = fieldCls.getAnnotation(AOpCodePattern.class);
            }
            if (anPattern != null) {

                String frPattern = getFragmentPattern(anPattern);

                Method[] converters = MethodUtils.getMethodsWithAnnotation(fieldClsNoArray,
                        AOpCodeFragmentConversion.class);
                if (converters == null || converters.length != 1) {
                    logger.error("Somethings wrong with converter for class : {}", fieldCls);
                    throw new RuntimeException("Somethings wrong with converter for class : " + fieldCls);
                }
                OpCodeOperationFragment crof = new OpCodeOperationFragment(frPattern, field, converters[0]);
                OpCodeOperationFragment crofOrder = checkOrder.get(crof.getOrder());
                if (crofOrder != null) {
                    logger.error("Order already used : {} by {}. Illegal order for class : {} fragment {}",
                            crof.getOrder(), crofOrder, cls, crof);
                    throw new RuntimeException("Order already used : " + crof.getOrder() + " for class : " + cls);
                } else {
                    checkOrder.put(crof.getOrder(), crof);
                }

                res.operationFragments.add(crof);
                pattern += frPattern;
            } else {
                if (field.getAnnotation(AOpCodeFieldOrder.class) != null) {
                    logger.error("Ordered field without pattern on class : {} -> {}", cls, field);
                    throw new RuntimeException("Ordered field without pattern on class : " + cls);
                }
            }
        }
        Collections.sort(res.operationFragments,
                (p1, p2) -> (p1.getOrder() < p2.getOrder() ? -1 : (p1.getOrder() > p2.getOrder() ? 1 : 0)));
        res.regexp = pattern;

        return res;

    }

    public AOpCode readCodeCheat(String line) {
        AOpCode res = null;
        try {
            res = ConstructorUtils.invokeConstructor(cls);
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                for (int i = 0; i < operationFragments.size(); i++) {
                    String fragment = matcher.group(i + 1);
                    OpCodeOperationFragment crof = operationFragments.get(i);
                    Field field = crof.getField();
                    Method builder = crof.getBuilder();
                    Object objField = MethodUtils.invokeStaticMethod(field.getType(), builder.getName(), fragment);
                    FieldUtils.writeField(field, res, objField, true);
                }
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    public String buildCodeFromFragments(AOpCode decodedOperation) {
        StringBuilder res = null;
        try {
            res = new StringBuilder(operation.getCodeType());
            EDataType dataType = null;
            for (OpCodeOperationFragment operationFragment : operationFragments) {
                Object fieldValue = FieldUtils.readField(operationFragment.getField(), decodedOperation, true);
                if (fieldValue instanceof EDataType) {
                    dataType = (EDataType) fieldValue;
                }
                if (fieldValue instanceof ICodeFragment) {
                    ICodeFragment cf = (ICodeFragment) fieldValue;
                    res.append(cf.encode());
                } else if (fieldValue instanceof ICodeFragmentWithVariableLength) {
                    ICodeFragmentWithVariableLength cf = (ICodeFragmentWithVariableLength) fieldValue;
                    EDataType valueType = dataType;
                    AOpCodeFixValueSize valueTypeAnno = operationFragment.getField()
                            .getAnnotation(AOpCodeFixValueSize.class);
                    if (valueTypeAnno != null) {
                        valueType = valueTypeAnno.dataType();
                    }

                    res.append(cf.encode(valueType));
                } else if (fieldValue.getClass().isArray()) {
                    // TODO : test class type before doing transfo
                    AOpCodeMask mask = operationFragment.getField().getAnnotation(AOpCodeMask.class);
                    long value = 0;
                    int size = mask.size();
                    Object[] fieldValueArray = (Object[]) fieldValue;
                    for (Object arrayItem : fieldValueArray) {
                        ICodeFragmentMask cf = (ICodeFragmentMask) arrayItem;
                        value |= cf.getMask();
                    }

                    res.append(HexUtils.pad('0', size, Long.toHexString(value)));
                } else {
                    logger.error("Field not managed : {} missing ICodeFragment implmentation",
                            operationFragment.getField());
                    throw new RuntimeException("Field not managed : " + operationFragment.getField()
                            + "  missing ICodeFragment implmentation");
                }
            }
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String resStr = null;
        boolean padded = true;
        AOpCodePadded annotPadded = decodedOperation.getClass().getAnnotation(AOpCodePadded.class);
        if (annotPadded != null) {
            padded = annotPadded.padded();
        }

        if (padded) {
            resStr = HexUtils.formatBlockOf(res.toString(), 8);
        } else {
            resStr = res.toString();
        }

        return resStr;

    }

    private static String getFragmentPattern(AOpCodePattern anPattern) {
        String frPattern = "";
        try {
            if (anPattern.capturing()) {
                frPattern += "(";
            }
            frPattern += anPattern.pattern();
            if (anPattern.capturing()) {
                frPattern += ")";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return frPattern;
    }

    public EOpCode getOperation() {
        return operation;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
