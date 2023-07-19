package com.sidam_backend.service;

import com.sidam_backend.data.ImageFile;
import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import com.sidam_backend.data.AccountRole;
import com.sidam_backend.repo.ImageFileRepository;
import com.sidam_backend.repo.NoticeRepository;
import com.sidam_backend.repo.StoreRepository;
import com.sidam_backend.repo.AccountRoleRepository;

import com.sidam_backend.resources.GetNotice;
import com.sidam_backend.resources.GetNoticeList;
import com.sidam_backend.resources.UpdateNotice;
import com.sidam_backend.resources.UploadFile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;
    private final AccountRoleRepository roleRepository;
    private final ImageFileRepository imageFileRepository;

    public Store validatedStoreId(Long id) {

        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " store is not exist."));
    }

    public AccountRole validatedUserRoleId(Long id) {

        AccountRole role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " role is not exist."));

        if (role.getSalary()) {
            throw new IllegalArgumentException(id + " user is not store manager.");
        }

        return role;
    }

    public String generateFileName(LocalDateTime now, int cnt, Store store) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddTHHmmss");

        return now.format(formatter) + "i" + cnt + "s" + store.getId();
    }

    public void deleteImage(ImageFile image, String path) {

        File file = new File(path + image.getFileName());

        if (file.exists()) {
            if (file.delete()) {
                log.info(path + image.getFileName() + " delete success.");
            } else {
                log.warn(path + image.getFileName() + " delete failed.");
            }
        } else {
            log.warn(path + image.getFileName() + " is not exist. delete failed.");
        }
    }

    public void saveNotice(Notice content) {

        imageFileRepository.saveAll(content.getImage());
        noticeRepository.save(content);

        noticeRepository.findById(content.getId())
                .orElseThrow(() -> new IllegalArgumentException("save failed"));
    }

    public List<GetNoticeList> findAllList(Store store, int lastId) {

        List<Notice> list = noticeRepository.selectAllAfterLast(lastId, store, 10);
        List<GetNoticeList> resultNotice = new ArrayList<>();

        for (Notice notice : list) {
            resultNotice.add(notice.toGetNoticeList());
        }

        return resultNotice;
    }

    public GetNotice findId(Long id, String url) {

        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " notice is not exist."))
                .toGetNotice(url);
    }

    public void deleteNotice(Long id) {

        noticeRepository.deleteById(id);
    }

    @Transactional
    public void updateNotice(UpdateNotice notice, String path, Store store) {

        Notice data;

        if (notice.getSubject().length() / 2 > 20) {
            throw new IllegalArgumentException("number of subject characters exceeded.");
        }

        if (notice.getContent().length() / 2 > 200) {
            throw new IllegalArgumentException("number of content characters exceeded.");
        }

        data = noticeRepository.findById(notice.getId())
                .orElseThrow(() -> new IllegalArgumentException(notice.getId() + "notice is not exist."));

        data.setSubject(notice.getSubject());
        data.setContent(notice.getContent());

        for (ImageFile image : data.getImage()) {
            deleteImage(image, path);
        }
        imageFileRepository.deleteAll(data.getImage());

        int i = 0;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<ImageFile> images = new ArrayList<>();
        for (UploadFile file : notice.getPhoto()) {
            images.add(file.toImageFile(path, generateFileName(now, i, store)));
            i++;
        }
        data.setImage(images);
    }
}
