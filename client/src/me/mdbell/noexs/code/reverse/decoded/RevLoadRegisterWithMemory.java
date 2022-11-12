package me.mdbell.noexs.code.reverse.decoded;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.model.Address;
import me.mdbell.noexs.code.model.ECodeMemoryRegion;
import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.Flag;
import me.mdbell.noexs.code.model.NoOp;
import me.mdbell.noexs.code.model.Register;
import me.mdbell.noexs.code.reverse.annotation.ARevFieldOrder;
import me.mdbell.noexs.code.reverse.annotation.ARevOperation;
import me.mdbell.noexs.code.reverse.annotation.ARevPattern;
import me.mdbell.util.HexUtils;

@ARevOperation(operation = EOperation.LOAD_REGISTER_WITH_MEMORY_VALUE)
public class RevLoadRegisterWithMemory extends ADecodedOperation {

    @ARevFieldOrder(order = 1)
    private EDataType dataType;

    @ARevFieldOrder(order = 2)
    private ECodeMemoryRegion region;

    @ARevFieldOrder(order = 3)
    private Register register;

    @ARevFieldOrder(order = 3)
    private Flag fromRegisterAddressEncoding;

    @ARevFieldOrder(order = 4)
    @ARevPattern(pattern = "0")
    private NoOp noOp = new NoOp("0");

    @ARevFieldOrder(order = 5)
    private Address address;

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
