package me.mdbell.noexs.code.opcode;

import me.mdbell.noexs.code.opcode.annotation.AOpCodeFieldOrder;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeMask;
import me.mdbell.noexs.code.opcode.annotation.AOpCodeOperation;
import me.mdbell.noexs.code.opcode.annotation.AOpCodePattern;
import me.mdbell.noexs.code.opcode.model.EKeypad;

//Code Type 0x8: Begin Keypress Conditional Block
//
//Code type 0x8 enters or skips a conditional block based on whether a key combination is pressed.
//Encoding
//
//8kkkkkkk
//
//  k: Keypad mask to check against, see below.
//
//Note that for multiple button combinations, the bitmasks should be ORd together.
//Keypad Values
//
//Note: This is the direct output of hidKeysDown().
//
//  0000001: A
//  0000002: B
//  0000004: X
//  0000008: Y
//  0000010: Left Stick Pressed
//  0000020: Right Stick Pressed
//  0000040: L
//  0000080: R
//  0000100: ZL
//  0000200: ZR
//  0000400: Plus
//  0000800: Minus
//  0001000: Left
//  0002000: Up
//  0004000: Right
//  0008000: Down
//  0010000: Left Stick Left
//  0020000: Left Stick Up
//  0040000: Left Stick Right
//  0080000: Left Stick Down
//  0100000: Right Stick Left
//  0200000: Right Stick Up
//  0400000: Right Stick Right
//  0800000: Right Stick Down
//  1000000: SL
//  2000000: SR

@AOpCodeOperation(operation = EOpCode.BEGIN_KEYPRESS_CONDITIONAL_BLOCK)
public class OpCode8BeginKeypressConditionalBlock extends AOpCode {

    @AOpCodeFieldOrder(order = 1)
    @AOpCodeMask(size = 7)
    @AOpCodePattern(pattern = "[0-9A-F]{7}")
    private EKeypad[] keypads;

    @Override
    public String abstractInstruction() {
        // TODO Auto-generated method stub
        return null;
    }

    public static OpCode8BeginKeypressConditionalBlock beginKeypressConditionalBlock(EKeypad[] keypads) {
        OpCode8BeginKeypressConditionalBlock res = new OpCode8BeginKeypressConditionalBlock();
        res.keypads = keypads;
        return res;
    }

}
