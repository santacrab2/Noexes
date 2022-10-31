package me.mdbell.noexs.code.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import me.mdbell.util.HexUtils;

public class Pointer {

	private Pointer pointer;

	private AddressType addressType;

	private ArithmeticType arithmeticType;

	private Long offset;

	public Pointer() {

	}

	public Pointer(Pointer pointer) {
		super();
		this.pointer = pointer;
	}

	public Pointer(Pointer pointer, ArithmeticType arithmeticType, String offsetStr) {
		super();
		this.pointer = pointer;
		this.arithmeticType = arithmeticType;
		this.offset = HexUtils.fromString(offsetStr);
	}

	public Pointer(AddressType addressType, String offsetStr) {
		super();
		this.addressType = addressType;
		this.arithmeticType = ArithmeticType.ADDITION;
		this.offset = HexUtils.fromString(offsetStr);
	}

	public Pointer getPointer() {
		return pointer;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public ArithmeticType getArithmeticType() {
		return arithmeticType;
	}

	public Long getOffset() {
		return offset;
	}

	public String getOffsetAsHex() {
		return Long.toHexString(offset);
	}

	public boolean isPositionFirst() {
		return pointer == null;
	}

	public AddressType getInheritedAddressType() {
		AddressType res = addressType;
		if (res == null) {
			res = pointer.getInheritedAddressType();
		}

		return res;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
