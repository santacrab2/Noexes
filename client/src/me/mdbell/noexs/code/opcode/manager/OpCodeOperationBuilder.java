package me.mdbell.noexs.code.opcode.manager;

import org.apache.commons.lang3.StringUtils;

import me.mdbell.noexs.code.EOperation;
import me.mdbell.noexs.code.OperationUtils;
import me.mdbell.noexs.code.model.EArithmeticOperation;
import me.mdbell.noexs.code.model.ECodeMemoryRegion;
import me.mdbell.noexs.code.model.EDataType;
import me.mdbell.noexs.code.model.Keypad;
import me.mdbell.noexs.code.opcode.OpCode5LoadRegisterWithMemory;

public class OpCodeOperationBuilder {

//	### Code Type 0x0: Store Static Value to Memory
//	Code type 0x0 allows writing a static value to a memory address.
//
//	#### Encoding
//	`0TMR00AA AAAAAAAA VVVVVVVV (VVVVVVVV)`
//
//	+ T: Width of memory write (1, 2, 4, or 8 bytes).
//	+ M: Memory region to write to (0 = Main NSO, 1 = Heap, 2 = Alias, 3 = Aslr).
//	+ R: Register to use as an offset from memory region base.
//	+ A: Immediate offset to use from memory region base.
//	+ V: Value to write.
//
//	---
    /**
     * 
     * @param dataType      T
     * @param region        M
     * @param registerToUse R
     * @param offset        A
     * @param value         V
     * @return
     */
    public static String storeStaticValueToMemory(EDataType dataType, ECodeMemoryRegion region, char registerToUse,
            String offset, String value) {
        String res = buildOperationHead(EOperation.STORE_STATIC_VALUE_TO_MEMORY, dataType, region, registerToUse);
        res += "00";
        res += OperationUtils.padHexValue(offset, EDataType.ADDR);
        res += " " + OperationUtils.padHexValue(value, dataType);
        return res;
    }

//	### Code Type 0x2: End Conditional Block
//	Code type 0x2 marks the end of a conditional block (started by Code Type 0x1 or Code Type 0x8).
//
//	When an Else is executed, all instructions until the appropriate End conditional block terminator are skipped.
//
//	#### Encoding
//	`2X000000`
//
//	+ X: End type (0 = End, 1 = Else).
//
//	---	
    public static String endConditionalBlock(boolean elseEndType) {
        String res = "" + EOperation.END_CONDITIONAL_BLOCK.getCodeType();
        res += getFlagValue(elseEndType);
        res += "000000";
        return res;
    }

//	### Code Type 0x5: Load Register with Memory Value
//	Code type 0x5 allows loading a value from memory into a register, either using a fixed address or by dereferencing the destination register.
//
//	#### Load From Fixed Address Encoding
//	`5TMR00AA AAAAAAAA`
//
//	+ T: Width of memory read (1, 2, 4, or 8 bytes).
//	+ M: Memory region to write to (0 = Main NSO, 1 = Heap, 2 = Alias, 3 = Aslr).
//	+ R: Register to load value into.
//	+ A: Immediate offset to use from memory region base.
//
//	#### Load from Register Address Encoding
//	`5T0R10AA AAAAAAAA`
//
//	+ T: Width of memory read (1, 2, 4, or 8 bytes).
//	+ R: Register to load value into. (This register is also used as the base memory address).
//	+ A: Immediate offset to use from register R.
//
//	---	
    public static OpCode5LoadRegisterWithMemory loadRegisterWithMemoryValueFromFixedAddress(EDataType dataType,
            ECodeMemoryRegion region, char registerToUse, long offset) {
        return OpCode5LoadRegisterWithMemory.loadFromRegisterAddressEncoding(dataType, region,
                String.valueOf(registerToUse), offset);
    }

    public static OpCode5LoadRegisterWithMemory loadRegisterWithMemoryValueFromRegisterAddress(EDataType dataType,
            char registerToUse, long offset) {
        return OpCode5LoadRegisterWithMemory.loadFromFixedAddressEncoding(dataType, String.valueOf(registerToUse),
                offset);
    }

//	### Code Type 0x6: Store Static Value to Register Memory Address
//	Code type 0x6 allows writing a fixed value to a memory address specified by a register.
//	
//	#### Encoding
//	`6T0RIor0 VVVVVVVV VVVVVVVV`
//	
//	+ T: Width of memory write (1, 2, 4, or 8 bytes).
//	+ R: Register used as base memory address.
//	+ I: Increment register flag (0 = do not increment R, 1 = increment R by T).
//	+ o: Offset register enable flag (0 = do not add r to address, 1 = add r to address).
//	+ r: Register used as offset when o is 1.
//	+ V: Value to write to memory.
//	
//	---
    public static String storeStaticValueToRegisterMemoryAddress(EDataType dataType, char registerToUse,
            boolean incrementRegisterFlag, boolean offsetRegisterEnable, char registerToUseAsOffset, String hexValue) {
        String res = "" + EOperation.STORE_STATIC_VALUE_TO_REGISTER_MEMORY_ADDRESS.getCodeType();
        res += dataType.getDataTypeCode();
        res += "0";
        res += registerToUse;
        res += getFlagValue(incrementRegisterFlag);
        res += getFlagValue(offsetRegisterEnable);
        res += getOptionalValue(offsetRegisterEnable, String.valueOf(registerToUseAsOffset), "0");
        res += "0";
        res += " " + OperationUtils.padHexValue(hexValue, dataType, 16);
        return res;

    }

//	### Code Type 0x7: Legacy Arithmetic
//	Code type 0x7 allows performing arithmetic on registers.
//
//	However, it has been deprecated by Code type 0x9, and is only kept for backwards compatibility.
//
//	#### Encoding
//	`7T0RC000 VVVVVVVV`
//
//	+ T: Width of arithmetic operation (1, 2, 4, or 8 bytes).
//	+ R: Register to apply arithmetic to.
//	+ C: Arithmetic operation to apply, see below.
//	+ V: Value to use for arithmetic operation.
//
//	#### Arithmetic Types
//	+ 0: Addition
//	+ 1: Subtraction
//	+ 2: Multiplication
//	+ 3: Left Shift
//	+ 4: Right Shift
//
//	---	

    public static String legacyArithmetic(EDataType dataType, char registerToUse, EArithmeticOperation arithmetic,
            String hexValue) {
        String res = "" + EOperation.LEGACY_ARITHMETIC.getCodeType();
        res += dataType.getDataTypeCode();
        res += "0";
        res += registerToUse;
        res += arithmetic.getArithmeticOperationCode();
        res += "000";
        res += " " + OperationUtils.padHexValue(hexValue, dataType);
        return res;

    }

//	Code Type 0x8: Begin Keypress Conditional Block
//
//	Code type 0x8 enters or skips a conditional block based on whether a key combination is pressed.
//	Encoding
//
//	8kkkkkkk
//
//	    k: Keypad mask to check against, see below.
//
//	Note that for multiple button combinations, the bitmasks should be ORd together.
//	Keypad Values
//
//	Note: This is the direct output of hidKeysDown().
//
//	    0000001: A
//	    0000002: B
//	    0000004: X
//	    0000008: Y
//	    0000010: Left Stick Pressed
//	    0000020: Right Stick Pressed
//	    0000040: L
//	    0000080: R
//	    0000100: ZL
//	    0000200: ZR
//	    0000400: Plus
//	    0000800: Minus
//	    0001000: Left
//	    0002000: Up
//	    0004000: Right
//	    0008000: Down
//	    0010000: Left Stick Left
//	    0020000: Left Stick Up
//	    0040000: Left Stick Right
//	    0080000: Left Stick Down
//	    0100000: Right Stick Left
//	    0200000: Right Stick Up
//	    0400000: Right Stick Right
//	    0800000: Right Stick Down
//	    1000000: SL
//	    2000000: SR

    public static String beginKeypressConditionalBlock(Keypad[] keypads) {
        String res = "" + EOperation.BEGIN_KEYPRESS_CONDITIONAL_BLOCK.getCodeType();

        long mask = 0;
        for (Keypad keypad : keypads) {
            mask |= keypad.getKeypadMask();
        }
        res += OperationUtils.padHexValue(Long.toHexString(mask), EDataType.T32, 7);
        return res;
    }

    private static String getOptionalValue(boolean toBeSet, String value, String defaultValue) {
        String res;
        if (toBeSet) {
            res = value;
        } else {
            res = defaultValue;
        }
        return res;
    }

    private static String getFlagValue(boolean flag) {
        String res;
        if (flag) {
            res = "1";
        } else {
            res = "0";
        }
        return res;
    }

    private static String buildOperationHead(EOperation op, EDataType dataType, ECodeMemoryRegion region,
            char registerToUse) {
        String regionStr = "0";
        if (region != null) {
            regionStr = Integer.toString(region.getPointerAdressType());
        }

        return StringUtils.join(op.getCodeType(), dataType.getDataTypeCode(), regionStr, registerToUse);
    }
}
