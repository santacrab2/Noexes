package me.mdbell.noexs.code;

public enum Operation {
	STORE_STATIC_VALUE_TO_MEMORY(0), BEGIN_CONDITIONAL_BLOCK(1), END_CONDITIONAL_BLOCK(2), LOOP(3),
	LOAD_REGISTER_WITH_STATIC_VALUE(4), LOAD_REGISTER_WITH_MEMORY_VALUE(5),
	STORE_STATIC_VALUE_TO_REGISTER_MEMORY_ADDRESS(6), LEGACY_ARITHMETIC(7), BEGIN_KEYPRESS_CONDITIONAL_BLOCK(8),
	PERFORM_ARITHMETIC(9);

	private int codeType;

	private Operation(int codeType) {
		this.codeType = codeType;
	}

	public int getCodeType() {
		return codeType;
	}

}
