package me.mdbell.noexs.code;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import me.mdbell.noexs.code.opcode.AOpCode;
import me.mdbell.noexs.code.opcode.manager.OpCodeManager;

class TestCodeReverse {

    private static final Logger logger = LogManager.getLogger(TestCodeReverse.class);

    String code = """
            580F0000 04BA9228
            580F1000 000000B8
            580F1000 00000018
            C1000F10
            """;

    @Test
    void test() {
        try {
            List<AOpCode> decodedOperations = OpCodeManager.decodeCheatCode(OpCodeManager.splitLines(code));
            List<String> cheatCodes = OpCodeManager.encodeCheatCode(decodedOperations);
            logger.info("Res => {}", StringUtils.join(cheatCodes, "\n"));

            logger.info("Abstact => {}", OpCodeManager.abstractInstructions(decodedOperations));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
