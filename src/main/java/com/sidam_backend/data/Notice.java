package com.sidam_backend.data;

import com.sidam_backend.resources.GetImage;
import com.sidam_backend.resources.GetNotice;
import com.sidam_backend.resources.GetNoticeList;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="notice")
public class Notice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime date;

    @OneToMany
    private List<ImageFile> image;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    public GetNoticeList toGetNoticeList() {

        GetNoticeList noticeList = new GetNoticeList();

        noticeList.setId(id);
        noticeList.setSubject(subject);
        noticeList.setDate(date);

        return noticeList;
    }

    public GetNotice toGetNotice(String url) {

        GetNotice notice = new GetNotice();
        List<GetImage> images = new ArrayList<>();

        notice.setId(id);
        notice.setSubject(subject);
        notice.setContent(content);
        notice.setTimeStamp(date);

        for (ImageFile file : image) {
            images.add(file.toGetImage(url));
        }

        notice.setPhoto(images);

        return notice;
    }
}