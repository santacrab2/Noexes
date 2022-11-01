module JNoexes {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.google.gson;
	requires org.objectweb.asm;
	requires java.desktop;
	requires java.prefs;

	requires javaxusb;

	requires org.apache.commons.lang3;
	requires org.apache.commons.io;
	requires org.apache.commons.collections4;

	requires org.antlr.antlr4.runtime;

	exports me.mdbell.noexs.ui;

	opens me.mdbell.noexs.ui.controllers to javafx.fxml;
	opens me.mdbell.javafx.control to javafx.fxml;
	opens me.mdbell.noexs.code.model to org.apache.commons.lang3;
}