package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.StringUtils;

public enum AddressType {
	MAIN((short) 0), HEAP((short) 1), BASE((short) 2);

	private short pointerAdressType;

	private AddressType(short pointerAdressType) {
		this.pointerAdressType = pointerAdressType;
	}

	public short getPointerAdressType() {
		return pointerAdressType;
	}

	public static AddressType getAddressType(String addressType) {
		return AddressType.valueOf(StringUtils.upperCase(addressType));
	}

}
