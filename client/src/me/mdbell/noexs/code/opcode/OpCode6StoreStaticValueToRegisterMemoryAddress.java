package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFixValueSize;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EDataType;
import me.mdbell.noexs.code.opcode.model.Flag;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.Register;
import me.mdbell.noexs.code.opcode.model.Value;

//### Code Type 0x6: Store Static Value to Register Memory Address
//Code type 0x6 allows writing a fixed value to a memory address specified by a register.
//
//#### Encoding
//`6T0RIor0 VVVVVVVV VVVVVVVV`
//
//+ T: Width of memory write (1, 2, 4, or 8 bytes).
//+ R: Register used as base memory address.
//+ I: Increment register flag (0 = do not increment R, 1 = increment R by T).
//+ o: Offset register enable flag (0 = do not add r to address, 1 = add r to address).
//+ r: Register used as offset when o is 1.
//+ V: Value to write to memory.
//
//---

@AOpCodeOperation(operation = EOpCode.STORE_STATIC_VALUE_TO_REGISTER_MEMORY_ADDRESS)
public class OpCode6StoreStaticValueToRegisterMemoryAddress extends AOpCode {
    @AOpCodeFieldOrder(order = 1)
    private EDataType dataType;

    @AOpCodeFieldOrder(order = 2)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp = new NoOp("0");

    @AOpCodeFieldOrder(order = 3)
    private Register register;

    @AOpCodeFieldOrder(order = 4)
    private Flag incrementRegister;

    @AOpCodeFieldOrder(order = 5)
    private Flag offsetRegisterEnable;

    @AOpCodeFieldOrder(order = 6)
    private Register offsetRegister;

    @AOpCodeFieldOrder(order = 7)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp2 = new NoOp("0");

    @AOpCodeFixValueSize(dataType = EDataType.T64)
    @AOpCodeFieldOrder(order = 8)
    private Value value;

    public static OpCode6StoreStaticValueToRegisterMemoryAddress storeStaticValueToRegisterMemoryAddress(
            EDataType dataType, String register, boolean incrementRegister, boolean offsetRegisterEnable,
            String offsetRegister, long value) {
        OpCode6StoreStaticValueToRegisterMemoryAddress res = new OpCode6StoreStaticValueToRegisterMemoryAddress();
        res.dataType = dataType;
        res.register = new Register(register);
        res.incrementRegister = new Flag(incrementRegister);
        res.offsetRegisterEnable = new Flag(offsetRegisterEnable);
        res.offsetRegister = new Register(offsetRegister);
        res.value = new Value(value);

        return res;
    }

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

}
