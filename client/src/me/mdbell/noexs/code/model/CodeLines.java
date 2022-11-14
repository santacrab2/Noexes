package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.opcode.AOpCode;
import me.mdbell.noexs.code.opcode.manager.OpCodeManager;

public class CodeLines {

    private List<AOpCode> opCodes = new ArrayList<>();

    public CodeLines() {
    }

    public CodeLines(AOpCode opCode) {
        super();
        this.opCodes.add(opCode);
    }

    public void addLineToHead(AOpCode opCode) {
        if (opCodes != null) {
            opCodes.add(0, opCode);
        }
    }

    public void addLineToEnd(AOpCode decodedOperation) {
        if (decodedOperation != null) {
            opCodes.add(decodedOperation);
        }
    }

    public void addCodeLines(CodeLines codeLinesToAdd) {
        opCodes.addAll(codeLinesToAdd.opCodes);
    }

    public String toStringCode() {

        List<String> strLines = OpCodeManager.encodeCheatCode(opCodes);

        return StringUtils.join(strLines.toArray(), "\n");
    }

}
