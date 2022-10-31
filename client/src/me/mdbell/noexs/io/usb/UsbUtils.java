package me.mdbell.noexs.io.usb;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.apache.commons.lang3.ArrayUtils;

public final class UsbUtils {

	private static final int SWITCH_VENDOR_ID = 0x57E;
	private static final short[] SWITCH_PROD_IDS = { 0x2000, 0x3000 };

	private UsbUtils() {

	}

	public static UsbDevice findSwitch() throws UsbException {
		return findDevice(SWITCH_VENDOR_ID, SWITCH_PROD_IDS);
	}

	public static UsbDevice findDevice(int vendorId, short[] productIds) throws UsbException {
		return findDevice(getRootHub(), (short) vendorId, productIds);
	}

	// Adapted from usb4java examples
	public static UsbDevice findDevice(UsbHub hub, short vendorId, short[] productIds) {
		for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
			if (isSwitch(device))
				return device;
			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device, vendorId, productIds);
				if (device != null)
					return device;
			}

		}
		return null;
	}

	public static UsbHub getRootHub() throws UsbException {
		UsbServices services = UsbHostManager.getUsbServices();
		return services.getRootUsbHub();
	}

	public static boolean isSwitch(UsbDevice d) {
		UsbDeviceDescriptor desc = d.getUsbDeviceDescriptor();
		return desc.idVendor() == SWITCH_VENDOR_ID && ArrayUtils.contains(SWITCH_PROD_IDS, desc.idProduct());
	}

}
