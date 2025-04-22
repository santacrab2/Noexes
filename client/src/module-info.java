module JNoexes {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.objectweb.asm;
    requires java.desktop;
    requires java.prefs;


    requires org.reflections;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires org.apache.commons.collections4;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    requires org.antlr.antlr4.runtime;

    exports me.mdbell.noexs.ui;

    opens me.mdbell.noexs.ui.controllers to javafx.fxml, com.google.gson;
    opens me.mdbell.noexs.ui.services to com.google.gson, org.apache.commons.lang3;
    opens me.mdbell.noexs.ui.models to com.google.gson;
    opens me.mdbell.javafx.control to javafx.fxml;
    opens me.mdbell.noexs.code.model to org.apache.commons.lang3, com.google.gson;
    opens me.mdbell.noexs.code.opcode.model to org.apache.commons.lang3, com.google.gson;

    opens me.mdbell.noexs.core.debugger to org.apache.commons.lang3;
    opens me.mdbell.noexs.code.opcode.manager to org.reflections, org.apache.commons.lang3;
    opens me.mdbell.noexs.code.opcode to org.reflections, org.apache.commons.lang3;
    opens me.mdbell.noexs.code.opcode.annotation to org.reflections, org.apache.commons.lang3;

}