package me.mdbell.noexs.ui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.lang3.StringUtils;

public class NoexesFiles {

    
    private static DateTimeFormatter FILENAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    
    private NoexesFiles() {

    }

    private static final File tmp = new File("./tmp");

    static {
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
    }

    public static File createTempFile(TemporalAccessor time, String suffix, String ext) throws IOException {
                
        String filename = "" + FILENAME_DATE_FORMATTER.format(time);

               // System.currentTimeMillis();
        if (StringUtils.isNotBlank(suffix)) {
            filename += "_" + suffix;
        }
        filename += "." + ext;

        File res = new File(tmp, filename);
        res.createNewFile();
        return res;
    }

    public static File createTempDir() {
        File res = new File(tmp, "" + System.currentTimeMillis());
        res.mkdirs();
        return res;
    }

    public static File getTempDir() {
        return tmp;
    }
}
