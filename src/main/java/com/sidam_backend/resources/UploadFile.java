package com.sidam_backend.resources;

import com.sidam_backend.data.ImageFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Data
public class UploadFile {

    private String uploadName;
    private MultipartFile file;

    public ImageFile toImageFile(String filePath, String name) {
        ImageFile build = new ImageFile();

        build.setFileName(name);
        build.setOrigName(uploadName);
        build.setFilePath(filePath);

        try {
            file.transferTo(new File(filePath, name));
        } catch (IOException ex) {
            throw new IllegalArgumentException("server error: file save failed");
        }

        return build;
    }
}
