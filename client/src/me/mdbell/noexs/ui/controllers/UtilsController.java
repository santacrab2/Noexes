package me.mdbell.noexs.ui.controllers;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHub;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import me.mdbell.noexs.io.usb.UsbUtils;
import me.mdbell.noexs.ui.models.ConversionType;
import me.mdbell.noexs.ui.models.UsbDeviceInfo;
import me.mdbell.util.HexUtils;

public class UtilsController implements IController {

    private static final Logger logger = LogManager.getLogger(UtilsController.class);

    public Label infoLabel;
    private MainController mc;

    public TreeView<UsbDeviceInfo> deviceTree;

    @FXML
    public TextField sourceToConvert;

    @FXML
    public TextField destConvert;

    @FXML
    ChoiceBox<ConversionType> conversionTypeSource;

    @FXML
    ChoiceBox<ConversionType> conversionTypeDest;

    @FXML
    CheckBox destHex;

    @FXML
    public void initialize() {
        deviceTree.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<UsbDeviceInfo>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<UsbDeviceInfo>> observable,
                            TreeItem<UsbDeviceInfo> oldValue, TreeItem<UsbDeviceInfo> newValue) {
                        if (newValue == null) {
                            return;
                        }
                        UsbDevice d = newValue.getValue().getDevice();
                        updateInfo(d);
                    }
                });

        conversionTypeSource.getItems().addAll(ConversionType.values());
        conversionTypeSource.getSelectionModel().select(ConversionType.U32);
        conversionTypeSource.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue.getLength() > conversionTypeDest.getValue().getLength()) {
                        ConversionType dst = null;
                        for (ConversionType ct : ConversionType.values()) {
                            if (newValue.getLength() <= ct.getLength()) {
                                dst = ct;
                                break;
                            }
                        }
                        if (dst != null) {
                            conversionTypeDest.getSelectionModel().select(dst);
                        }
                    }
                    filterNumberInputValue();
                });

        conversionTypeDest.getItems().addAll(ConversionType.values());
        conversionTypeDest.getSelectionModel().select(ConversionType.U32);
        conversionTypeDest.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            convertValue();
        });

    }

    private void updateInfo(UsbDevice d) {
        if (d == null) {
            infoLabel.setText("");
            return;
        }
        try {
            StringBuilder sb = new StringBuilder("[");
            if (d.isUsbHub()) {
                sb.append("HUB");
            } else {
                sb.append("DEVICE");
            }
            sb.append("] ");
            UsbDeviceDescriptor desc = d.getUsbDeviceDescriptor();
            if (d.isUsbHub()) {
                UsbHub hub = (UsbHub) d;
                sb.append("Attached Devices: ").append(hub.getAttachedUsbDevices().size()).append(" Total Ports: ")
                        .append(hub.getNumberOfPorts());
            } else {
                sb.append("Device Id:")
                        .append(HexUtils.pad('0', 4, Integer.toUnsignedString(desc.idVendor() & 0xFFFF, 16)))
                        .append(":")
                        .append(HexUtils.pad('0', 4, Integer.toUnsignedString(desc.idProduct() & 0xFFFF, 16)));
                sb.append(" Is Switch:").append(UsbUtils.isSwitch(d));
            }
            infoLabel.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMainController(MainController c) {
        this.mc = c;
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect() {

    }

    public void onRefresh(ActionEvent event) throws UsbException {
        updateInfo(null);
        UsbHub hub = UsbUtils.getRootHub();
        TreeItem<UsbDeviceInfo> root = new TreeItem<>(new UsbDeviceInfo(hub));
        scanHub(root, hub);
        deviceTree.setRoot(root);
    }

    private void scanHub(TreeItem<UsbDeviceInfo> root, UsbHub hub) {
        root.setExpanded(true);
        List<UsbDevice> deviceList = hub.getAttachedUsbDevices();
        for (UsbDevice d : deviceList) {
            TreeItem<UsbDeviceInfo> node = new TreeItem<>(new UsbDeviceInfo(d));
            if (d.isUsbHub()) {
                scanHub(node, (UsbHub) d);
            }
            root.getChildren().add(node);
        }

    }

    public void onTypeToConvertChange(MouseEvent event) {

        filterNumberInputValue();
    }

    public void onKeyPressedToConvertChange(KeyEvent event) {

    }

    public void onInputToConvertChange(KeyEvent event) {

        filterNumberInputValue();

    }

    private void filterNumberInputValue() {
        String input = sourceToConvert.getText();
        String res = null;

        if (StringUtils.startsWithIgnoreCase(input, "0x")) {
            res = StringUtils.substring(input, 0, 2)
                    + RegExUtils.replaceAll(StringUtils.substring(input, 2, input.length()), "[^\\-0-9A-Fa-f]", "");
        } else {
            String regExp = null;
            switch (conversionTypeSource.getValue()) {
                case FLT:
                    regExp = "[^\\-0-9.]";
                    break;
                default:
                    regExp = "[^\\-0-9]";
                    break;
            }

            res = RegExUtils.replaceAll(input, regExp, "");
        }

        if (StringUtils.equals(input, res)) {
            convertValue();
        } else {
            sourceToConvert.setText(res);
            sourceToConvert.positionCaret(res.length());
        }
    }

    private void convertValue() {

        boolean converstionError = false;
        try {
            String sourceText = sourceToConvert.getText();
            String dest = null;

            if (StringUtils.isNotBlank(sourceText)) {
                ConversionType sourceType = conversionTypeSource.getValue();

                ConversionType destType = conversionTypeDest.getValue();
                boolean toHex = destHex.isSelected();

                Long intermediate = ConversionUtils.sourceToLong(sourceType, sourceText);

                if (intermediate != null) {

                    dest = ConversionUtils.longToString(sourceType, destType, intermediate, toHex, true);

                    logger.trace("Converstion : '{}'({}) -[{}]-> '{}'({},hex:{})", sourceText, sourceType.name(),
                            intermediate, dest, destType.name(), toHex);
                    destConvert.setText(dest);
                } else {
                    converstionError = true;
                }
            } else {
                dest = "";
            }

        } catch (Exception e) {
            converstionError = true;
            // logger.error("Error while converting", e);
        }

        if (converstionError) {
            destConvert.setText("Conversion error");
        }

    }

    public void onExchangeConvertFields(ActionEvent event) {
        logger.info("Exchange values");

        destHex.setSelected(StringUtils.startsWithIgnoreCase(sourceToConvert.getText(), "0x"));

        sourceToConvert.setText(destConvert.getText());
        ConversionType bufferConv = conversionTypeSource.getValue();
        conversionTypeSource.setValue(conversionTypeDest.getValue());
        conversionTypeDest.setValue(bufferConv);

        filterNumberInputValue();
    }

    public void onCopyToClipboard(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(destConvert.getText());
        clipboard.setContent(content);
    }

}
