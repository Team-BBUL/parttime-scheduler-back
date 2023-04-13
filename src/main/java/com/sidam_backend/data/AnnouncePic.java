package com.sidam_backend.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="announce_pic")
public class AnnouncePic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pictureId;

    @Lob
    private byte[] photo;

    @ManyToOne
    private Announcement announcementId;
}
