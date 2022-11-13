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
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFixValueSize;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFragmentConversion;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EDataType;
import me.mdbell.noexs.code.opcode.model.ICodeFragment;
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
        pattern += op.getCodeType();

        Field[] fields = FieldUtils.getAllFields(cls);

        Map<Integer, OpCodeOperationFragment> checkOrder = new HashMap<>();
        for (Field field : fields) {
            Class<?> fieldCls = field.getType();
            AOpCodePattern anPattern = field.getAnnotation(AOpCodePattern.class);
            if (anPattern == null) {
                anPattern = fieldCls.getAnnotation(AOpCodePattern.class);
            }
            if (anPattern != null) {

                String frPattern = getFragmentPattern(anPattern);
                Method[] converters = MethodUtils.getMethodsWithAnnotation(fieldCls, AOpCodeFragmentConversion.class);
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
                } else {
                    logger.error("Field not managed : {} missing ICodeFragment implmentation",
                            operationFragment.getField());
                }
            }
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return HexUtils.formatBlockOf(res.toString(), 8);

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
