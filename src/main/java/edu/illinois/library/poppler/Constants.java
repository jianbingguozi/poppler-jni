package edu.illinois.library.poppler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

final class Constants {

    static final String LIBRARY_NAME = "libpoppler-jni";

    private Constants() {}

    public static void loadFromJar() {
        // we need to put both DLLs to temp dir
        String path = "AC_" + new Date().getTime();
        loadLib(path, Constants.LIBRARY_NAME);
    }

    /**
     * Puts library to temp dir and loads to memory
     */
    private static void loadLib(String path, String name) {
        name = name + ".so";
        try {
            // have to use a stream
            try (InputStream in = PopplerDocument.class.getClassLoader().getResourceAsStream(name)) {
                // always write to different location
                File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + path + name);
                byte[] buffer = new byte[1024];
                int off = 0;
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    while ((off = in.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, off);
                        fileOutputStream.flush();
                    }
                }
                System.load(fileOut.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load required DLL", e);
        }
    }
}
