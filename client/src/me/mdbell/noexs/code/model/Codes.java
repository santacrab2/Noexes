package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

public class Codes {

	private List<Code> codes = new ArrayList<>();

	public Codes() {
	}

	public void addCode(Code code) {
		codes.add(code);
	}

	public List<Code> getCodes() {
		return codes;
	}
}
