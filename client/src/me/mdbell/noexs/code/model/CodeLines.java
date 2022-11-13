package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.opcode.AOpCode;
import me.mdbell.noexs.code.opcode.manager.OpCodeManager;

public class CodeLines {

    // TODO ne plus avoir de String
    private List<String> codeLines = new ArrayList<>();

    public CodeLines() {
    }

    public CodeLines(String codeLines) {
        super();
        this.codeLines.add(codeLines);
    }

    public void addLineToHead(String codeLine) {
        if (codeLine != null) {
            codeLines.add(0, codeLine);
        }
    }

    public void addLineToEnd(String codeLine) {
        if (codeLine != null) {
            codeLines.add(codeLine);
        }
    }

    public void addLineToEnd(AOpCode decodedOperation) {
        if (decodedOperation != null) {
            codeLines.add(OpCodeManager.encodeCheatCode(decodedOperation));
        }
    }

    public void addCodeLines(CodeLines codeLinesToAdd) {
        codeLines.addAll(codeLinesToAdd.codeLines);
    }

    public String toStringCode() {
        return StringUtils.join(codeLines.toArray(), "\n");
    }

}
