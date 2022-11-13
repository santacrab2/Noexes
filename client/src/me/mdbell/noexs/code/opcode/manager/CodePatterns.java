package me.mdbell.noexs.code.opcode.manager;

import java.util.ArrayList;
import java.util.List;

import me.mdbell.noexs.code.opcode.EOpCode;

public class CodePatterns {

    private List<CodePattern> patterns = new ArrayList<>();
    private EOpCode operation;

    public CodePatterns(EOpCode operation) {
        super();
        this.operation = operation;
    }

    public CodePatterns add(String pattern) {
        patterns.add(new CodePattern(pattern));
        return this;
    }

    public CodePatterns add(String pattern, boolean capturing) {
        patterns.add(new CodePattern(pattern));
        return this;
    }

    public String getRegExp() {
        String res = "";
        for (CodePattern cp : patterns) {
            res += cp.getRegExp();
        }
        return res;
    }

    public static CodePatterns fromOperation(EOpCode operation) {
        return new CodePatterns(operation);
    }

}
