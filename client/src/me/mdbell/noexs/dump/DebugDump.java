package me.mdbell.noexs.dump;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class DebugDump {

    private static DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss.SSS");

    private File debugFile;

    private boolean exists;

    public DebugDump(File dumpFile, boolean exits) {
        super();
        this.exists = exists;
        if (exits) {
            this.debugFile = new File(FilenameUtils.removeExtension(dumpFile.getPath()) + ".rdmpdbg");
        } else {
            this.debugFile = new File(FilenameUtils.removeExtension(dumpFile.getPath()) + ".wdmpdbg");
        }
    }

    public void writeLine(String line) {
        if (false) {
            try {

                FileUtils.write(debugFile, LINE_DATE_FORMATTER.format(LocalDateTime.now()) + "|" + line + "\n", "UTF-8",
                        true);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
