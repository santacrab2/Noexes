package me.mdbell.noexs.core;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.mdbell.noexs.core.debugger.EDebCommand;
import me.mdbell.noexs.core.debugger.RDebAttachInput;
import me.mdbell.noexs.core.debugger.RDebCurrentPidOutput;
import me.mdbell.noexs.core.debugger.RDebGetAttachedPidOutput;
import me.mdbell.noexs.core.debugger.RDebGetPidsOutput;
import me.mdbell.noexs.core.debugger.RDebGetTitleIdInput;
import me.mdbell.noexs.core.debugger.RDebGetTitleIdOutput;
import me.mdbell.noexs.core.debugger.RDebPoke16Input;
import me.mdbell.noexs.core.debugger.RDebPoke32Input;
import me.mdbell.noexs.core.debugger.RDebPoke64Input;
import me.mdbell.noexs.core.debugger.RDebPoke8Input;
import me.mdbell.noexs.core.debugger.RDebSetBreakpointInput;
import me.mdbell.noexs.core.debugger.RDebStatusOutput;
import me.mdbell.noexs.misc.BreakpointFlagBuilder;
import me.mdbell.noexs.misc.BreakpointType;
import me.mdbell.noexs.misc.WatchpointFlagBuilder;
import me.mdbell.noexs.ui.NoexsApplication;
import me.mdbell.noexs.ui.models.DataType;
import me.mdbell.util.HexUtils;

public class Debugger implements Commands, Closeable {

    private static final Logger logger = LogManager.getLogger(Debugger.class);
    private IConnection conn;
    private MemoryInfo prev;
    private Semaphore semaphore = new Semaphore(1);
    private int protocolVersion;

    public static final int CURRENT_PROTOCOL_VERSION = (NoexsApplication.VERSION_MAJOR << 16)
            | (NoexsApplication.VERSION_MINOR) << 8;

    public Debugger(IConnection conn) {
        this.conn = conn;
    }

    public IConnection raw() {
        return conn;
    }

    private void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ConnectionException(e);
        }
    }

    private void release() {
        semaphore.release();
    }

    public DebuggerStatus getStatus() {
        acquire();
        try {
            RDebStatusOutput statusOutput = DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_STATUS);

            int status = statusOutput.status();
            int major = statusOutput.major();
            int minor = statusOutput.minor();
            int patch = statusOutput.patch();

            this.protocolVersion = (major << 16) | (minor << 8);
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException("This is impossible, so you've done something terribly wrong", rc);
            }
            if (protocolVersion > CURRENT_PROTOCOL_VERSION) {
                throw new ConnectionException(String.format("Unsupported protocol version:%08X", protocolVersion));
            }
            protocolVersion |= patch; // we don't need to check the patch value, as it should always be backwards
                                      // compatible.
            return DebuggerStatus.forId(status);
        } finally {
            release();
        }
    }

    public void poke(DataType type, long addr, long value) {
        switch (type) {
            case BYTE:
                poke8(addr, (int) value);
                break;
            case SHORT:
                poke16(addr, (int) value);
                break;
            case INT:
                poke32(addr, (int) value);
                break;
            case LONG:
                poke64(addr, value);
                break;
        }
    }

    public void poke8(long addr, int value) {
        acquire();
        try {

            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_POKE8, new RDebPoke8Input(addr, (byte) value));
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException(rc);
            }
        } finally {
            release();
        }
    }

    private byte[] peekBuffer = new byte[8];

    public int peek8(long addr) {
        ByteBuffer b = readmem(addr, 1, peekBuffer).order(ByteOrder.LITTLE_ENDIAN);
        return b.get() & 0xFF;
    }

    public void poke16(long addr, int value) {
        acquire();
        try {
            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_POKE16, new RDebPoke16Input(addr, (short) value));
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException(rc);
            }
        } finally {
            release();
        }
    }

    public int peek16(long addr) {
        ByteBuffer b = readmem(addr, 2, peekBuffer).order(ByteOrder.LITTLE_ENDIAN);
        return b.getShort() & 0xFFFF;

    }

    public void poke32(long addr, int value) {
        acquire();
        try {
            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_POKE32, new RDebPoke32Input(addr, (int) value));
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException(rc);
            }
        } finally {
            release();
        }
    }

    public int peek32(long addr) {
        ByteBuffer b = readmem(addr, 4, peekBuffer).order(ByteOrder.LITTLE_ENDIAN);
        return b.getInt();
    }

    public void poke64(long addr, long value) {
        acquire();
        try {
            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_POKE64, new RDebPoke64Input(addr, (long) value));
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException(rc);
            }
        } finally {
            release();
        }
    }

    public long peek64(long addr) {
        ByteBuffer b = readmem(addr, 8, peekBuffer).order(ByteOrder.LITTLE_ENDIAN);
        return b.getLong();
    }

    public Result setWatchpoint(boolean read, boolean write, long addr) {
        acquire();
        try {
            Result rc;

            WatchpointFlagBuilder.MatchType t;
            if (read & write) {
                t = WatchpointFlagBuilder.MatchType.ALL;
            } else if (read) {
                t = WatchpointFlagBuilder.MatchType.LOAD;
            } else if (write) {
                t = WatchpointFlagBuilder.MatchType.STORE;
            } else {
                throw new IllegalArgumentException("No flags set for watchpoint");
            }
            System.out.println(t);
            int size = 4;
            int id = 0;
            int bpId = id + 4;
            int offset = (int) (addr - (addr & ~3));
            int mask = ((1 << size) - 1) << offset;

            BreakpointFlagBuilder bp = new BreakpointFlagBuilder().setEnabled(true).setAddressSelect(0xF)
                    .setBreakpointType(BreakpointType.LINKED_CONTEXT_IDR_MATCH);
            WatchpointFlagBuilder wp = new WatchpointFlagBuilder().setEnabled(true).setAccessContol(t)
                    .setAddressSelect(mask).setLinkedBreakpointNumber(bpId);
            rc = setBreakpoint(bpId, bp.getFlag(), 0);
            System.out.println("bp:" + rc);
            if (rc.succeeded()) {
                rc = setBreakpoint(0x10 + id, wp.getFlag(), addr); // wp
                System.out.println("wp:" + rc);
            }
            return rc;
        } finally {
            release();
        }
    }

    public Result setBreakpoint(int id, long flags, long addr) {
        acquire();
        try {
            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_SET_BREAKPOINT,
                    new RDebSetBreakpointInput(id, addr, flags));
            return conn.readResult();
        } finally {
            release();
        }
    }

    public Result writemem(byte[] data, long addr) {
        return writemem(data, 0, data.length, addr);
    }

    public Result writemem(byte[] data, int off, int len, long addr) {
        acquire();
        try {
            conn.writeCommand(COMMAND_WRITE);
            conn.writeLong(addr);
            conn.writeInt(len);
            conn.flush();
            Result r = conn.readResult();
            if (r.succeeded()) {
                conn.write(data, off, len);
                conn.flush();
            } else {
                conn.readResult();
                return r;
            }
            return conn.readResult();
        } finally {
            release();
        }
    }

    public void readmem(MemoryInfo info, OutputStream to) throws IOException {
        if (!info.isReadable()) {
            return;
        }
        readmem(info.getAddress(), (int) info.getSize(), to);
    }

    public void readmem(long start, int size, OutputStream to) throws IOException {
        acquire();
        try {
            conn.writeCommand(COMMAND_READ);
            conn.writeLong(start);
            conn.writeInt(size);
            conn.flush();

            Result rc = conn.readResult();
            if (rc.succeeded()) {
                //try (BufferedOutputStream bufferedWriter = new BufferedOutputStream(to)) {
                    byte[] buffer = new byte[2048 * 4];
                    while (size > 0) {
                        rc = conn.readResult();
                        if (rc.failed()) {
                            conn.readResult();
                            throw new ConnectionException(rc);
                        }
                        int len = readCompressed(buffer);
                        // to.write(buffer, 0, len);
                        to.write(buffer, 0, len);
                        size -= len;
                    }
                //}
            }
            conn.readResult();
        } finally {
            release();
        }
    }

    public ByteBuffer readmem(long addr, int size, byte[] bytes) {
        acquire();
        try {
            conn.writeCommand(COMMAND_READ);
            conn.writeLong(addr);
            conn.writeInt(size);
            conn.flush();
            Result rc = conn.readResult();

            if (rc.failed()) {
                conn.readResult(); // ignored
                throw new ConnectionException(rc);
            }

            if (bytes == null) {
                bytes = new byte[size];
            }

            int pos = 0;
            byte[] buffer = new byte[2048 * 4];
            while (pos < size) {
                rc = conn.readResult();
                if (rc.failed()) {
                    conn.readResult();
                    throw new ConnectionException(rc);
                }
                int len = readCompressed(buffer);
                System.arraycopy(buffer, 0, bytes, pos, len);
                pos += len;
            }
            conn.readResult(); // ignored
            return ByteBuffer.wrap(bytes);
        } finally {
            release();
        }
    }

    public Result resume() {
        return getResult(EDebCommand.COMMAND_CONTINUE);
    }

    public Result pause() {
        return getResult(EDebCommand.COMMAND_PAUSE);
    }

    public Result attach(long pid) {
        logger.debug("COMMAND : Attach to pid :{}", pid);
        acquire();
        try {
            DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_ATTACH, new RDebAttachInput(pid));
            return conn.readResult();
        } finally {
            release();
        }
    }

    public Result detach() {
        return getResult(EDebCommand.COMMAND_DETATCH);
    }

    public MemoryInfo query(long address) {
        logger.debug("COMMAND : Query Memory  address:{}", HexUtils.formatAddress(address));
        acquire();
        try {
            if (prev != null && prev.getAddress() != 0 && address >= prev.getAddress()
                    && address < prev.getNextAddress()) {
                return prev;
            }

            conn.writeCommand(COMMAND_QUERY_MEMORY);
            conn.writeLong(address);
            conn.flush();
            prev = readInfo();
            logger.debug("COMMAND Result :{} ", prev);
            return prev;
        } finally {
            release();
        }
    }

    public MemoryInfo[] query(long start, int max) {
        logger.debug("COMMAND : Query Memory Multi start:{}, max:{}", start, max);
        acquire();
        try {
            conn.writeCommand(COMMAND_QUERY_MEMORY_MULTI);
            conn.writeLong(start);
            conn.writeInt(max);
            conn.flush();

            MemoryInfo[] res = new MemoryInfo[max];
            int count;
            for (count = 0; count < max; count++) {
                MemoryInfo info = readInfo();
                res[count] = info;
                if (info.getType() == MemoryType.RESERVED) {
                    break;
                }
            }
            conn.readResult(); // ignored here, it gets checked in readInfo()
            logger.debug("COMMAND Result :{} memory info", count);
            return Arrays.copyOf(res, count);
        } finally {
            release();
        }
    }

    public long getCurrentPid() {
        acquire();
        try {
            RDebCurrentPidOutput currentPid = DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_CURRENT_PID);
            long pid = currentPid.pid();
            Result rc = conn.readResult();
            if (rc.failed()) {
                pid = 0;
            }
            logger.debug("COMMAND Result : pid={}", pid);
            return pid;
        } finally {
            release();
        }
    }

    public long getAttachedPid() {
        acquire();
        try {
            RDebGetAttachedPidOutput attachedPid = DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_GET_ATTACHED_PID);
            long pid = attachedPid.pid();
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException("This is impossible, so you've done something terribly wrong", rc);
            }
            return pid;
        } finally {
            release();
        }
    }

    public long[] getPids() {
        acquire();
        try {

            RDebGetPidsOutput getPids = DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_GET_PIDS);
            long[] pids = getPids.pids();
            Result rc = conn.readResult();
            if (rc.failed()) {
                throw new ConnectionException(rc);
            }
            return pids;
        } finally {
            release();
        }
    }

    public long getTitleId(long pid) {
        acquire();
        try {
            RDebGetTitleIdOutput res = DebuggerUtils.runCommand(conn, EDebCommand.COMMAND_GET_TITLEID,
                    new RDebGetTitleIdInput(pid));
            long tid = res.tid();
            Result rc = conn.readResult();
            if (rc.failed()) {
                // TODO throw? idk
            }
            return tid;
        } finally {
            release();
        }
    }

    private void disconnect() {
        Result rc = getResult(EDebCommand.COMMAND_DISCONNECT);
        if (rc.failed()) {
            throw new ConnectionException("This is impossible, so you've done something terribly wrong", rc);
        }
    }

    public void getBookmark() {
        acquire();
        try {
            conn.writeCommand(COMMAND_GET_BOOKMARK);
            conn.flush();
            int count = conn.readInt();
            System.out.println("Bookmark count : " + count);
            conn.writeCommand(COMMAND_STATUS);
            conn.flush();
            int count2 = conn.readInt();
            System.out.println("Bookmark count2 : " + count2);

            Result rc = conn.readResult();

            if (rc.failed()) {
                conn.readResult(); // ignored
                throw new ConnectionException(rc);
            }

        } finally {
            release();
        }
    }

    public long getCurrentTitleId() {
        long pid = getCurrentPid();
        if (pid == 0) {
            return 0;
        }
        return getTitleId(pid);
    }

    public boolean attached() {
        return getAttachedPid() != 0;
    }

    public boolean connected() {
        return conn.connected();
    }

    @Override
    public void close() throws IOException {
        if (connected()) {
            detach();
            disconnect();
        }
        conn.close();
    }

    private byte[] compressedBuffer = new byte[2048 * 4 * 6];

    private int readCompressed(byte[] buffer) {
        int compressedFlag = conn.readByte();
        int decompressedLen = conn.readInt();

        if (compressedFlag == 0) {
            conn.readFully(buffer, 0, decompressedLen);
        } else {
            int compressedLen = conn.readInt();
            conn.readFully(compressedBuffer, 0, compressedLen);
            int pos = 0;
            for (int i = 0; i < compressedLen; i += 2) {
                byte value = compressedBuffer[i];
                int count = compressedBuffer[i + 1] & 0xFF;

                // Arrays.fill(buffer, pos, pos + count, value);

                byte[] a = buffer;
                int fromIndex = pos;
                int toIndex = pos + count;
                byte val = value;

                for (int fi = fromIndex; fi < toIndex; fi++) {
                    a[fi] = val;
                }

                pos += count;
            }
        }
        return decompressedLen;
    }

    private MemoryInfo readInfo() {
        long addr = conn.readLong();
        long size = conn.readLong();
        int type = conn.readInt();
        int perm = conn.readInt();
        Result rc = conn.readResult();
        if (rc.failed()) {
            throw new ConnectionException(rc);
        }
        return new MemoryInfo(addr, size, type, perm);
    }

    private Result getResult(EDebCommand cmd) {
        acquire();
        try {
            DebuggerUtils.runCommand(conn, cmd);
            return conn.readResult();
        } finally {
            release();
        }
    }

    public long peek(DataType type, long addr) {
        switch (type) {
            case BYTE:
                return peek8(addr) & 0xffL;
            case SHORT:
                return peek16(addr) & 0xffffL;
            case INT:
                return peek32(addr) & 0xffffffffL;
            case LONG:
                return peek64(addr);
        }
        throw new IllegalArgumentException("Illegal data type:" + type);
    }
}