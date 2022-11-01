package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

public class Block {

	private List<IInstruction> instructions = new ArrayList<>();

	public Block() {
	}

	public void addInstruction(IInstruction instruction) {
		instructions.add(instruction);
	}

	public List<IInstruction> getInstructions() {
		return instructions;
	}
}
