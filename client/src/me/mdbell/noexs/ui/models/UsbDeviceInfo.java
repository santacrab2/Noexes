package me.mdbell.noexs.ui.models;

import javax.usb.UsbDevice;

import me.mdbell.noexs.io.usb.UsbUtils;

public class UsbDeviceInfo {

    private UsbDevice device;

    public UsbDeviceInfo(UsbDevice device) {
        super();
        this.device = device;
    }

    public UsbDevice getDevice() {
        return device;
    }

    @Override
    public String toString() {
        String isSwitchDevice = "";
        if (isSwitch()) {
            isSwitchDevice = " [SWITCH]";
        }

        return device + isSwitchDevice;
    }

    public boolean isSwitch() {
        return UsbUtils.isSwitch(device);
    }
}
