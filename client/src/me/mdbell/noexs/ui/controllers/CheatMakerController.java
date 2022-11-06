package me.mdbell.noexs.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import me.mdbell.noexs.code.CheatCodeMaker;

public class CheatMakerController implements IController {

	private MainController mc;

	@FXML
	TextArea cheatSource;

	@FXML
	TextArea cheatCode;

	@FXML
	TextArea console;

	@Override
	public void setMainController(MainController mc) {
		this.mc = mc;

	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub

	}

	public void onSaveSource(ActionEvent event) {
		File f = mc.browseFile(true, null, null, "Save As...", "Cheat Source File", "*.csf");
		if (f == null) {
			return;
		}

		try {
			FileUtils.writeStringToFile(f, cheatSource.getText(), StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onLoadSource(ActionEvent event) {
		File f = mc.browseFile(false, null, null, "Load", "Cheat Source File", "*.csf");
		if (f == null) {
			return;
		}

		try {
			cheatSource.setText(FileUtils.readFileToString(f, StandardCharsets.UTF_8.name()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMakeCheat(ActionEvent event) {
		String res = CheatCodeMaker.generateCodeFromString(cheatSource.getText());
		cheatCode.setText(res);
	}

	public void onSaveCode(ActionEvent event) {
		File f = mc.browseFile(true, null, null, "Save As...", "Cheat Code File", "*.txt");
		if (f == null) {
			return;
		}

		try {
			FileUtils.writeStringToFile(f, cheatCode.getText(), StandardCharsets.US_ASCII.name());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onGetBookmark(ActionEvent event) {
		mc.getDebugger().getBookmark();
	}

}
