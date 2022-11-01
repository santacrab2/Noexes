package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CodeLines {

	private List<String> codeLines = new ArrayList<>();

	public CodeLines() {
	}

	public CodeLines(String codeLines) {
		super();
		this.codeLines.add(codeLines);
	}

	public void addLineToHead(String codeLine) {
		if (codeLine != null) {
			codeLines.add(0, codeLine);
		}
	}

	public void addLineToEnd(String codeLine) {
		if (codeLine != null) {
			codeLines.add(codeLine);
		}
	}

	public void addCodeLines(CodeLines codeLinesToAdd) {
		codeLines.addAll(codeLinesToAdd.codeLines);
	}

	public String toStringCode() {
		return StringUtils.join(codeLines.toArray(), "\n");
	}

}
