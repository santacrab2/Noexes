package me.mdbell.noexs.code.reverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;
import me.mdbell.noexs.code.reverse.decoded.ADecodedOperation;

public class CodeReverse {

    private static Reflections reflections = new Reflections("me.mdbell.noexs.code.reverse");

    private static final Logger logger = LogManager.getLogger(CodeReverse.class);

    List<CodeReverseOperation> ops = new ArrayList<>();

    Map<EOperation, CodeReverseOperation> codeOperations = new HashMap<>();

    public static String[] splitLines(String codes) {
        return StringUtils.split(codes, "\n");
    }

    public static List<ADecodedOperation> decodeCheatCode(String[] cheatCodes) {

        List<ADecodedOperation> res = new ArrayList<>();
        CodeReverse cr = new CodeReverse();
        cr.init();

        for (String cheatCode : cheatCodes) {
            String cheat = StringUtils.trim(cheatCode);
            EOperation opCode = EOperation.valueFromFragment(cheat);
            CodeReverseOperation cro = cr.codeOperations.get(opCode);
            ADecodedOperation obj = cro.readCodeCheat(cheat);
            res.add(obj);
            logger.info("Cheat {} => {}", cheatCode, obj.toString());
        }

        chainOperations(res);

        return res;
    }

    public static void chainOperations(List<ADecodedOperation> operations) {
        ADecodedOperation last = null;

        for (ADecodedOperation operation : operations) {
            operation.setPreviousOperation(last);
            if (last != null) {
                last.setNextOperation(operation);
            }
            last = operation;
        }

    }

    public static List<String> encodeCheatCode(List<ADecodedOperation> decodedOperations) {
        List<String> res = new ArrayList<>();
        CodeReverse cr = new CodeReverse();
        cr.init();
        for (ADecodedOperation decodedOperation : decodedOperations) {
            EOperation op = decodedOperation.getOperation();
            CodeReverseOperation cro = cr.codeOperations.get(op);
            res.add(cro.buildCodeFromFragments(decodedOperation));
        }

        return res;
    }

    public static String abstractInstructions(List<ADecodedOperation> decodedOperations) {
        String res = "";
        if (!decodedOperations.isEmpty()) {
            res = decodedOperations.get(decodedOperations.size() - 1).abstractInstruction();
        }
        return res;
    }

    public void init() {

        Set<Class<?>> operations = reflections.get(Scanners.TypesAnnotated.with(ARevOperation.class).asClass());
        for (Class<?> cls : operations) {
            Class<? extends ADecodedOperation> clsDecoded = (Class<? extends ADecodedOperation>) cls;
            CodeReverseOperation op = CodeReverseOperation.fromRevClass(clsDecoded);
            ops.add(op);
            codeOperations.put(op.getOperation(), op);
        }
    }

}
