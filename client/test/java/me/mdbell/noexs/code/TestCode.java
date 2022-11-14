package me.mdbell.noexs.code;

import me.mdbell.noexs.code.opcode.OpCode8BeginKeypressConditionalBlock;
import me.mdbell.noexs.code.opcode.model.EKeypad;

public class TestCode {

    // [main+4e056c0] + e56f8

    public static void main(String[] args) {

        doCodeFor("\"Pointer1\" [MAIN+0x00759E0000] +98C = (U32)0x7FFFFFFF");
        doCodeFor("\"Pointer2\"[HEAP+0x00759E0000] +98C = (U8)0x7F");
        doCodeFor("\"Pointer3\"[[MAIN+0x00759E0000] +98C] = (U64)0x7FFFFFFFFFFFFFFF");
        doCodeFor("\"Pointer4\"[MAIN+0x00759E0000] -98C = (U16)0x7FFF");
        doCodeFor("\"Pointer5\"[[[MAIN+0x0049800000] +420]+68] = (U8)0x80");
        doCodeFor("\"Pointer6\"[[MAIN+0x0049800000] +420]+68 = (U8)0x80");
        doCodeFor("\"Pointer7\"[[HEAP+0x0042000000] +690]+44 = (U16)0x8000");

        doCodeFor("\"Pointer8\"[[ALIAS+0x0042000000] +690]+44 = (U16)0x8000");
        doCodeFor("\"Pointer9\"[[ALIAS+0x0042000000] +690]+44 = (FLT)17.52");

        doCodeFor(
                "\"Block1\"{[[ALIAS+0x0042000000] +690]+44 = (FLT)17.52 ; [[MAIN+0x0049800000] +420]+68 = (U16)4246;}");

        doCodeFor(
                "\"Block2\"{[[MAIN+0x0042000000] +690]+44 = (FLT)17.52 ; [[MAIN+0x0049800000] +420]+68 = (U32)1234;}\n"
                        + "\"Block3\"IFBUT LEFT_STICK_LEFT {[[MAIN+0x0042000000] +690]+44 = (FLT)17.52 ; [[MAIN+0x0049800000] +420]+68 = (U8)42;}");

        EKeypad[] keyPads = { EKeypad.A, EKeypad.R, EKeypad.LEFT };
        System.out.println("Key test : " + OpCode8BeginKeypressConditionalBlock.beginKeypressConditionalBlock(keyPads));
    }

    private static void doCodeFor(String cheatLine) {

        System.out.println("-------------------------------------------");
        System.out.println(cheatLine);
        System.out.println(">>>");
        System.out.println(CheatCodeMaker.generateCodeFromString(cheatLine));
    }
}
