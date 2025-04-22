package me.mdbell.noexs.ui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import me.mdbell.noexs.io.usb.UsbUtils;
import me.mdbell.util.HexUtils;


import java.util.List;

public class UtilsController implements IController {

    public Label infoLabel;
    private MainController mc;

   

   

   

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

    public void onRefresh(ActionEvent event)  {
       
    }

    
}
