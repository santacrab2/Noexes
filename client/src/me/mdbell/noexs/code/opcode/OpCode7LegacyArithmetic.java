package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EArithmeticOperation;
import me.mdbell.noexs.code.opcode.model.NoOp;
import me.mdbell.noexs.code.opcode.model.Register;
import me.mdbell.noexs.code.opcode.model.Value;

//Code Type 0x7: Legacy Arithmetic

//
//Code type 0x7 allows performing arithmetic on registers.
//
//However, it has been deprecated by Code type 0x9, and is only kept for backwards compatibility.
//Encoding
//
//7T0RC000 VVVVVVVV
//
//    T: Width of arithmetic operation (1, 2, 4, or 8 bytes).
//    R: Register to apply arithmetic to.
//    C: Arithmetic operation to apply, see below.
//    V: Value to use for arithmetic operation.
//
//Arithmetic Types
//
//    0: Addition
//    1: Subtraction
//    2: Multiplication
//    3: Left Shift
//    4: Right Shift
//
//--- 
@AOpCodeOperation(operation = EOpCode.LEGACY_ARITHMETIC)
public class OpCode7LegacyArithmetic extends AOpCode {
    @AOpCodeFieldOrder(order = 1)
    private EDataType dataType;

    @AOpCodeFieldOrder(order = 2)
    @AOpCodePattern(pattern = "0")
    private NoOp noOp = new NoOp("0");

    @AOpCodeFieldOrder(order = 3)
    private Register register;

    @AOpCodeFieldOrder(order = 4)
    @AOpCodePattern(pattern = "[01234]")
    EArithmeticOperation arithmeticOperation;

    @AOpCodeFieldOrder(order = 5)
    @AOpCodePattern(pattern = "000")
    private NoOp noOp2 = new NoOp("000");

    @AOpCodeFieldOrder(order = 6)
    private Value value;

    public static OpCode7LegacyArithmetic legacyArithmetic(EDataType dataType, String register,
            EArithmeticOperation arithmeticOperation, long value) {
        OpCode7LegacyArithmetic res = new OpCode7LegacyArithmetic();
        res.dataType = dataType;
        res.register = new Register(register);
        res.arithmeticOperation = arithmeticOperation;
        res.value = new Value(value);
        return res;
    }

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }
}
