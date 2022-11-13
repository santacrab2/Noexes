package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.Address;
import me.mdbell.noexs.code.opcode.model.ECodeMemoryRegion;
import me.mdbell.noexs.code.opcode.model.EDataType;
import me.mdbell.noexs.code.opcode.model.Flag;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.Register;
import me.mdbell.util.HexUtils;

//### Code Type 0x5: Load Register with Memory Value
//Code type 0x5 allows loading a value from memory into a register, either using a fixed address or by dereferencing the destination register.
//
//#### Load From Fixed Address Encoding
//`5TMR00AA AAAAAAAA`
//
//+ T: Width of memory read (1, 2, 4, or 8 bytes).
//+ M: Memory region to write to (0 = Main NSO, 1 = Heap, 2 = Alias, 3 = Aslr).
//+ R: Register to load value into.
//+ A: Immediate offset to use from memory region base.
//
//#### Load from Register Address Encoding
//`5T0R10AA AAAAAAAA`
//
//+ T: Width of memory read (1, 2, 4, or 8 bytes).
//+ R: Register to load value into. (This register is also used as the base memory address).
//+ A: Immediate offset to use from register R.
//
//--- 

@AOpCodeOperation(operation = EOpCode.LOAD_REGISTER_WITH_MEMORY_VALUE)
public class OpCode5LoadRegisterWithMemory extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    private EDataType dataType;

    @AOpCodeFieldOrder(order = 2)
    private ECodeMemoryRegion region;

    @AOpCodeFieldOrder(order = 3)
    private Register register;

    @AOpCodeFieldOrder(order = 4)
    private Flag fromRegisterAddressEncoding;

    @AOpCodeFieldOrder(order = 5)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp = new NoOp("0");

    @AOpCodeFieldOrder(order = 6)
    private Address address;

    public static OpCode5LoadRegisterWithMemory loadFromRegisterAddressEncoding(EDataType dataType,
            ECodeMemoryRegion region, String register, long address) {
        OpCode5LoadRegisterWithMemory res = new OpCode5LoadRegisterWithMemory();
        res.dataType = dataType;
        res.region = region;
        res.register = new Register(register);
        res.fromRegisterAddressEncoding = new Flag(false);
        res.address = new Address(address);
        return res;
    }

    public static OpCode5LoadRegisterWithMemory loadFromFixedAddressEncoding(EDataType dataType, String register,
            long address) {
        OpCode5LoadRegisterWithMemory res = new OpCode5LoadRegisterWithMemory();
        res.dataType = dataType;
        res.region = ECodeMemoryRegion.MAIN;
        res.register = new Register(register);
        res.fromRegisterAddressEncoding = new Flag(true);
        res.address = new Address(address);
        return res;
    }

    @Override
    public String abstractInstruction() {
        StringBuilder res = new StringBuilder();
        if (fromRegisterAddressEncoding.getFlag()) {
            res.append("[");
            res.append(previousOperation.abstractInstruction());
            res.append("]");
            res.append(" + ");
            res.append(HexUtils.formatAddress(address.getAddress()));
        } else {
            res.append(region.name());
            res.append(" + ");
            res.append(HexUtils.formatAddress(address.getAddress()));
        }
        return res.toString();
    }

}
