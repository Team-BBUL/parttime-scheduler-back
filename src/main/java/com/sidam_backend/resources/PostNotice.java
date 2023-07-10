package com.sidam_backend.resources;

import com.sidam_backend.data.ImageFile;
import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostNotice {

    private String subject;
    private String body;
    private List<UploadFile> photo;

    public Notice toNotice(Store store, String filePath) {

        Notice notice = new Notice();

        // 글자 수 검사 - 제목
        if (subject.length() / 2 > 20) {
            throw new IllegalArgumentException("number of subject characters exceeded.");
        }
        // 내용
        if (body.length() / 2 > 200) {
            throw new IllegalArgumentException("number of content characters exceeded.");
        }

        notice.setSubject(subject);
        notice.setContent(body);

        notice.setStore(store);

        List<ImageFile> images = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddTHHmmss");
        LocalDateTime now = LocalDateTime.now();
        int i = 0;
        // file name 형식 : store ID가 1인 곳에서 2023년 6월 12일 15시 32분 43초에 올린 글로 사진이 둘일 경우,
        // 20230612T153243i0s1
        // 20230612T153243i1s1
        for (UploadFile file : photo) {
            images.add(file.toImageFile(filePath,
                    now.format(formatter) + "i" + i + "s" + store.getId() ));
        }
        notice.setImage(images);

        return notice;
    }

}
