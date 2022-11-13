package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.Address;
import me.mdbell.noexs.code.model.ECodeMemoryRegion;
import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.Flag;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.util.HexUtils;

@AOpCodeOperation(operation = EOperation.LOAD_REGISTER_WITH_MEMORY_VALUE)
public class OpCode5LoadRegisterWithMemory extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    private EDataType dataType;

    @AOpCodeFieldOrder(order = 2)
    private ECodeMemoryRegion region;

    @AOpCodeFieldOrder(order = 3)
    private Register register;

    @AOpCodeFieldOrder(order = 3)
    private Flag fromRegisterAddressEncoding;

    @AOpCodeFieldOrder(order = 4)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp = new NoOp("0");

    @AOpCodeFieldOrder(order = 5)
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
