package com.sidam_backend.data;

import com.sidam_backend.resources.DTO.GetImage;
import com.sidam_backend.resources.DTO.GetNotice;
import com.sidam_backend.resources.DTO.GetNoticeList;
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
@Table(name="notice_tbl")
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

    @NotNull
    private boolean valid = true;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    public GetNoticeList toGetNoticeList(boolean check) {

        GetNoticeList noticeList = new GetNoticeList();

        noticeList.setId(id);
        noticeList.setSubject(subject);
        noticeList.setTimeStamp(date);
        noticeList.setCheck(check);

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
