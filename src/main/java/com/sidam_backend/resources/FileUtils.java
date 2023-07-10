package com.sidam_backend.resources;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileUtils {

    private static final Tika tika = new Tika();

    public static boolean validImgFile(InputStream img) {

        try {
            List<String> validTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

            String mimeType = tika.detect(img);
            log.info(mimeType + " type uploaded.");

            return validTypes.stream().anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));

        } catch (IOException ex) {
            return false;
        }
    }

    public static String validFileName(String name) throws IllegalArgumentException {

        name = name.replace("..", "");
        name = name.replace("/", "");
        name = name.replace("\\$", "");

        if (name.contains("\\")) {
            log.warn(name + " invalid name.");
            throw new IllegalArgumentException(name + " is invalid file name.");
        }

        return name;
    }
}
