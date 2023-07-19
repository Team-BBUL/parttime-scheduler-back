package com.sidam_backend.resources;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static String generateFileName(LocalDateTime now, int cnt, Long store) {

        // file name 형식 : store ID가 1인 곳에서 2023년 6월 12일 15시 32분 43초에 올린 글로 사진이 둘일 경우,
        // 20230612153243i0s1
        // 20230612153243i1s1
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "i" + cnt + "s" + store + ".jpg";
    }

    public static void deleteImage(String path, String name) {

        File file = new File(path);

        log.debug("file path = " + path + " file name = " + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info(path + " delete success.");
            } else {
                log.warn(path + " delete failed.");
            }
        } else {
            log.warn(path + " is not exist. delete failed.");
        }
    }
}
