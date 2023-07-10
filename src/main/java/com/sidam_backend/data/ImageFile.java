package com.sidam_backend.data;

import com.sidam_backend.resources.GetImage;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="image_file")
public class ImageFile {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String origName;

    @NotNull
    private String fileName;

    @NotNull
    private String filePath;

    public GetImage toGetImage(String url) {

        GetImage image = new GetImage();

        image.setFileName(fileName);
        image.setDownloadUrl(url + fileName);

        return image;
    }
}
