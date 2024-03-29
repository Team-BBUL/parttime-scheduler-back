package com.sidam_backend.resources.DTO;

import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostNotice {

    private String subject;
    private String content;
    private List<MultipartFile> images;

    public Notice toNotice(Store store) {

        Notice notice = new Notice();

        // 글자 수 검사 - 제목
        if (subject.length() > 20 || subject.length() == 0) {
            throw new IllegalArgumentException("number of subject characters exceeded.");
        }
        // 내용
        if (content.length() > 200) {
            throw new IllegalArgumentException("number of content characters exceeded.");
        }

        notice.setSubject(subject);
        notice.setContent(content);
        notice.setStore(store);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        notice.setDate(now);

        return notice;
    }

}
