package me.mdbell.noexs.core;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.mdbell.noexs.core.debugger.EDebDataType;
import me.mdbell.noexs.core.debugger.EDebCommand;

public class DebuggerUtils {

    private static final Logger logger = LogManager.getLogger(DebuggerUtils.class);

    public static <T extends Record> T readRecord(IConnection connection, Class<T> recordCls) {
        RecordComponent[] recordComponents = recordCls.getRecordComponents();
        List<Object> values = new ArrayList<>();
        for (RecordComponent rc : recordComponents) {
            EDebDataType dt = EDebDataType.getDatatype(rc.getType());
            Object value = dt.getReadValueMethod().apply(connection);
            values.add(value);
            logger.debug("Field Read : {}->{}:{}={}", rc.getName(), rc.getType(), dt, value);
        }

        T res = null;
        try {
            res = ConstructorUtils.invokeConstructor(recordCls, values.toArray());
        } catch (Throwable e) {
            logger.error("Error while contruct record", e);
        }
        return res;
    }

    public static <T extends Record> void writeRecord(IConnection connection, Class<T> recordCls, Object record) {
        RecordComponent[] recordComponents = recordCls.getRecordComponents();
        try {
            for (RecordComponent rc : recordComponents) {
                EDebDataType dt = EDebDataType.getDatatype(rc.getType());
                Object fieldValue = rc.getAccessor().invoke(record);
                dt.getWriteValueMethod().accept(connection, fieldValue);
                logger.debug("Field Write : {}->{}:{}={}", rc.getName(), rc.getType(), dt, fieldValue);
            }
        } catch (Throwable e) {
            logger.error("Error while writing value", e);
        }
    }

    public static <T extends Record> T runCommand(IConnection connection, EDebCommand command, Object input) {
        logger.info("Running command : {}", command);
        connection.writeCommand(command.getCode());
        if (command.getInputRecord() != null) {
            DebuggerUtils.writeRecord(connection, command.getInputRecord(), input);
        }
        connection.flush();
        T res = null;
        if (command.getOutputRecord() != null) {
            res = (T) DebuggerUtils.readRecord(connection, command.getOutputRecord());
        }
        return res;
    }

    public static <T extends Record> T runCommand(IConnection connection, EDebCommand command) {
        return runCommand(connection, command, null);
    }

}
