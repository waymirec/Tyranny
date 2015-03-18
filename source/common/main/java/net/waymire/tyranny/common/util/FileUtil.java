package net.waymire.tyranny.common.util;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    public static final String SYSTEM_TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static File createTemporaryDirectory()
    {
        File baseDir = new File(SYSTEM_TEMP_DIR);
        File tmpDir = new File(baseDir,UUID.randomUUID().toString());
        tmpDir.mkdir();
        return tmpDir;
    }

    private FileUtil() { }
}
