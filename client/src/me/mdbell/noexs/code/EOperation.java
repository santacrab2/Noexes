package me.mdbell.noexs.code;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.reverse.annotation.ARevFragmentConversion;

public enum EOperation {
    STORE_STATIC_VALUE_TO_MEMORY("0"),
    BEGIN_CONDITIONAL_BLOCK("1"),
    END_CONDITIONAL_BLOCK("2"),
    LOOP("3"),
    LOAD_REGISTER_WITH_STATIC_VALUE("4"),
    LOAD_REGISTER_WITH_MEMORY_VALUE("5"),
    STORE_STATIC_VALUE_TO_REGISTER_MEMORY_ADDRESS("6"),
    LEGACY_ARITHMETIC("7"),
    BEGIN_KEYPRESS_CONDITIONAL_BLOCK("8"),
    PERFORM_ARITHMETIC("9"),
    STORE_REGISTER_TO_MEMORY_ADDRESS("A"),
    BEGIN_REGISTER_CONDITIONAL_BLOCK("C0"),
    SAVE_OR_RESTORE_REGISTER("C1"),
    SAVE_OR_RESTORE_REGISTER_WITH_MASK("C2"),
    READ_OR_WRITE_STATIC_REGISTER("C3");

    private String codeType;

    private EOperation(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeType() {
        return codeType;
    }

    @ARevFragmentConversion
    public static EOperation valueFromFragment(String fragment) {
        EOperation res = null;
        for (EOperation dt : EOperation.values()) {
            if (StringUtils.startsWithIgnoreCase(fragment, dt.codeType)) {
                res = dt;
                break;
            }
        }
        return res;
    }

}
