package me.mdbell.noexs.core.debugger;

public enum EDebCommand {

    COMMAND_STATUS(0x01, null, RDebStatusOutput.class),
    COMMAND_POKE8(0x02, RDebPoke8Input.class, null),
    COMMAND_POKE16(0x03, RDebPoke16Input.class, null),
    COMMAND_POKE32(0x04, RDebPoke32Input.class, null),
    COMMAND_POKE64(0x05, RDebPoke64Input.class, null),
    // int COMMAND_READ = 0x06;
    // int COMMAND_WRITE = 0x07;
    COMMAND_CONTINUE(0x08),
    COMMAND_PAUSE(0x09),
    // int COMMAND_ATTACH = 0x0A;
    COMMAND_DETATCH(0x0B),
    // int COMMAND_QUERY_MEMORY = 0x0C;
    // int COMMAND_QUERY_MEMORY_MULTI = 0x0D;
    // int COMMAND_CURRENT_PID = 0x0E;
    // int COMMAND_GET_ATTACHED_PID = 0x0F;
    // int COMMAND_GET_PIDS = 0x10;
    // int COMMAND_GET_TITLEID = 0x11;
    COMMAND_DISCONNECT(0x12);
    // int COMMAND_READ_MULTI = 0x13;
    // int COMMAND_SET_BREAKPOINT = 0x14;

    private int code;

    private Class<? extends Record> inputRecord;
    private Class<? extends Record> outputRecord;

    private EDebCommand(int code) {
        this(code, null, null);
    }

    private EDebCommand(int code, Class<? extends Record> inputRecord, Class<? extends Record> outputRecord) {
        this.code = code;
        this.inputRecord = inputRecord;
        this.outputRecord = outputRecord;
    }

    public int getCode() {
        return code;
    }

    public Class<? extends Record> getInputRecord() {
        return inputRecord;
    }

    public Class<? extends Record> getOutputRecord() {
        return outputRecord;
    }

}
