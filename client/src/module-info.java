module JNoexes {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.google.gson;
	requires org.objectweb.asm;
	requires java.desktop;
	requires java.prefs;
	
	requires javaxusb;
	
	exports me.mdbell;
	exports me.mdbell.noexs.ui;

	opens me.mdbell.noexs.ui.controllers to javafx.fxml;
	opens me.mdbell.javafx.control to javafx.fxml;
}