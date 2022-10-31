package me.mdbell.noexs.code;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import me.mdbell.noexs.code.parser.CodeLexer;
import me.mdbell.noexs.code.parser.CodeParser;
import me.mdbell.noexs.code.parser.CodeParser.CodeContext;

public class TestCode {

	// [main+4e056c0] + e56f8

	public static void main(String[] args) {

		doCodeFor("[MAIN+0x00759E0000] +98C = (U32)0x7FFFFFFF");
		doCodeFor("[HEAP+0x00759E0000] +98C = (U8)0x7F");
		doCodeFor("[[MAIN+0x00759E0000] +98C] = (U64)0x7FFFFFFFFFFFFFFF");
		doCodeFor("[MAIN+0x00759E0000] -98C = (U16)0x7FFF");
		doCodeFor("[[[MAIN+0x0049800000] +420]+68] = (U8)0x80");
		doCodeFor("[[MAIN+0x0049800000] +420]+68 = (U8)0x80");
		doCodeFor("[[HEAP+0x0042000000] +690]+44 = (U16)0x8000");
	}

	private static void doCodeFor(String cheatLine) {
		CodePointCharStream input = CharStreams.fromString(cheatLine);

		// CodePointCharStream input = CharStreams.fromString("[MAIN+0x00759E0000] +98C
		// = (U32)0x7FFFFFFF");

		CodeLexer lexer = new CodeLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CodeParser parser = new CodeParser(tokens);
		parser.setBuildParseTree(true);
		CodeContext tree = parser.code();
		System.out.println(cheatLine);

		PointerCode pc = new PointerCode(tree.c);
		
		System.out.println(pc.generateCode() + "\n");
	}
}
