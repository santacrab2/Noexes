package me.mdbell.noexs.code.reverse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.ICodeFragment;
import me.mdbell.noexs.code.model.ICodeFragmentWithVariableLength;
import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;
import me.mdbell.noexs.code.reverse.decoded.ADecodedOperation;
import me.mdbell.util.HexUtils;

public class CodeReverseOperation {

    private EOperation operation;
    private String regexp;
    private Class<? extends ADecodedOperation> cls;
    private List<CodeReverseOperationFragment> operationFragments = new ArrayList<>();

    public CodeReverseOperation(Class<? extends ADecodedOperation> cls, EOperation operation) {
        super();
        this.operation = operation;
        this.cls = cls;
    }

    public static CodeReverseOperation fromRevClass(Class<? extends ADecodedOperation> cls) {
        String pattern = "";
        ARevOperation anOperation = cls.getAnnotation(ARevOperation.class);
        EOperation op = anOperation.operation();
        CodeReverseOperation res = new CodeReverseOperation(cls, op);
        pattern += op.getCodeType();

        Field[] fields = FieldUtils.getAllFields(cls);
        for (Field field : fields) {
            Class<?> fieldCls = field.getType();
            ARevPattern anPattern = field.getAnnotation(ARevPattern.class);
            if (anPattern == null) {
                anPattern = fieldCls.getAnnotation(ARevPattern.class);
            }
            if (anPattern != null) {

                String frPattern = getFragmentPattern(anPattern);
                Method[] converters = MethodUtils.getMethodsWithAnnotation(fieldCls, ARevFragmentConversion.class);
                CodeReverseOperationFragment crof = new CodeReverseOperationFragment(frPattern, field, converters[0]);

                res.operationFragments.add(crof);
                pattern += frPattern;
            }
        }
        Collections.sort(res.operationFragments,
                (p1, p2) -> (p1.getOrder() < p2.getOrder() ? -1 : (p1.getOrder() > p2.getOrder() ? 1 : 0)));
        res.regexp = pattern;

        return res;

    }

    public ADecodedOperation readCodeCheat(String line) {
        ADecodedOperation res = null;
        try {
            res = ConstructorUtils.invokeConstructor(cls);
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                for (int i = 0; i < operationFragments.size(); i++) {
                    String fragment = matcher.group(i + 1);
                    CodeReverseOperationFragment crof = operationFragments.get(i);
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

    public String buildCodeFromFragments(ADecodedOperation decodedOperation) {
        StringBuilder res = null;
        try {
            res = new StringBuilder(operation.getCodeType());
            EDataType dataType = null;
            for (CodeReverseOperationFragment operationFragment : operationFragments) {
                Object fieldValue = FieldUtils.readField(operationFragment.getField(), decodedOperation, true);
                if (fieldValue instanceof EDataType) {
                    dataType = (EDataType) fieldValue;
                }
                if (fieldValue instanceof ICodeFragment) {
                    ICodeFragment cf = (ICodeFragment) fieldValue;
                    res.append(cf.encode());
                } else if (fieldValue instanceof ICodeFragmentWithVariableLength) {
                    ICodeFragmentWithVariableLength cf = (ICodeFragmentWithVariableLength) fieldValue;
                    res.append(cf.encode(dataType));
                }
            }
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return HexUtils.formatBlockOf(res.toString(), 8);

    }

    private static String getFragmentPattern(ARevPattern anPattern) {
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

    public EOperation getOperation() {
        return operation;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
