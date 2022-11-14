package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.Address;
import me.mdbell.noexs.code.opcode.model.ECodeMemoryRegion;
import me.mdbell.noexs.code.opcode.model.EDataType;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.Register;
import me.mdbell.noexs.code.opcode.model.Value;

//### Code Type 0x0: Store Static Value to Memory
//Code type 0x0 allows writing a static value to a memory address.
//
//#### Encoding
//`0TMR00AA AAAAAAAA VVVVVVVV (VVVVVVVV)`
//
//+ T: Width of memory write (1, 2, 4, or 8 bytes).
//+ M: Memory region to write to (0 = Main NSO, 1 = Heap, 2 = Alias, 3 = Aslr).
//+ R: Register to use as an offset from memory region base.
//+ A: Immediate offset to use from memory region base.
//+ V: Value to write.
//
//---
@AOpCodeOperation(operation = EOpCode.STORE_STATIC_VALUE_TO_MEMORY)
public class OpCode0StoreStaticValueToMemory extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    private EDataType dataType;

    @AOpCodeFieldOrder(order = 2)
    private ECodeMemoryRegion region;

    @AOpCodeFieldOrder(order = 3)
    private Register register;

    @AOpCodeFieldOrder(order = 4)
    @AOpCodePattern(pattern = "00")
    private NoOp noOp = new NoOp("00");

    @AOpCodeFieldOrder(order = 5)
    private Address address;

    @AOpCodeFieldOrder(order = 6)
    private Value value;

    public static OpCode0StoreStaticValueToMemory storeStaticValueToMemory(EDataType dataType, ECodeMemoryRegion region,
            String register, long address, long value) {
        OpCode0StoreStaticValueToMemory res = new OpCode0StoreStaticValueToMemory();
        res.dataType = dataType;
        res.region = region;
        res.register = new Register(register);
        res.address = new Address(address);
        res.value = new Value(value);

        return res;
    }

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

}
