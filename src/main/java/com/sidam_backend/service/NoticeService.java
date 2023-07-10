package com.sidam_backend.service;

import com.sidam_backend.data.ImageFile;
import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;
import com.sidam_backend.repo.ImageFileRepository;
import com.sidam_backend.repo.NoticeRepository;
import com.sidam_backend.repo.StoreRepository;

import com.sidam_backend.resources.FileUtils;
import com.sidam_backend.resources.GetNotice;
import com.sidam_backend.resources.GetNoticeList;
import com.sidam_backend.resources.UpdateNotice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final ImageFileRepository imageFileRepository;

    public Store validatedStoreId(Long id) {

        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " store is not exist."));
    }

    public String generateFileName(LocalDateTime now, int cnt, Long store) {

        // file name 형식 : store ID가 1인 곳에서 2023년 6월 12일 15시 32분 43초에 올린 글로 사진이 둘일 경우,
        // 20230612153243i0s1
        // 20230612153243i1s1
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "i" + cnt + "s" + store + ".jpg";
    }

    public List<ImageFile> saveFile(List<MultipartFile> images, String path, LocalDateTime now, Store store)
            throws IOException, IllegalArgumentException {

        List<ImageFile> imageFiles = new ArrayList<>();

        if (images == null) {
            return imageFiles;
        }

        for (int i = 0; i < images.size(); i++) {

            String name = generateFileName(now, i, store.getId());
            String originName = images.get(i).getOriginalFilename();

            // ImageFile로 변환
            ImageFile tmp = new ImageFile();
            tmp.setFilePath(path);
            tmp.setFileName(name);

            // 파일 이름 유효성 검사
            originName = FileUtils.validFileName(originName);
            tmp.setOrigName(originName);
            imageFiles.add(tmp);

            // 파일 확장자 유효성 검사
            if (!FileUtils.validImgFile(images.get(i).getInputStream())) {
                throw new IllegalArgumentException("only image files can upload");
            }
            // 저장
            images.get(i).transferTo(
                    new File(path, name)
            );
        }

        imageFileRepository.saveAll(imageFiles);

        return imageFiles;
    }

    public void deleteImage(String path, String name) {

        File file = new File(path + name);

        if (file.exists()) {
            if (file.delete()) {
                log.info(path + name + " delete success.");
            } else {
                log.warn(path + name + " delete failed.");
            }
        } else {
            log.warn(path + name + " is not exist. delete failed.");
        }
    }

    public long getLastId(Store store) {
        return noticeRepository.selectLastId(store.getId())
                .orElseThrow(() -> new IllegalArgumentException("notice table is empty"));
    }

    public void saveNotice(Notice content) {

        if (content.getImage() != null && content.getImage().size() > 0) {
            imageFileRepository.saveAll(content.getImage());
        }

        noticeRepository.save(content);

        noticeRepository.findById(content.getId())
                .orElseThrow(() -> new IllegalArgumentException("save failed"));
    }

    public List<GetNoticeList> findAllList(Store store, int lastId) {

        List<Notice> list = noticeRepository.selectAllAfterLast(lastId, store.getId(), 10);
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

    @Transactional
    public void deleteNotice(Long id) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " notice is not exist."));

        for (ImageFile image : notice.getImage()) {
            image.setValid(false);
        }

        notice.setValid(false);
        notice.setDate(LocalDateTime.now().withNano(0));
    }

    @Transactional
    public void updateNotice(UpdateNotice notice, String path, Store store)
            throws IOException, IllegalArgumentException{

        Notice data;

        // 입력값의 유효성 확인
        if (notice.getSubject().length() / 2 > 20 || notice.getSubject().length() == 0) {
            throw new IllegalArgumentException("number of subject characters exceeded.");
        }
        if (notice.getContent().length() / 2 > 200) {
            throw new IllegalArgumentException("number of content characters exceeded.");
        }

        // id에 맞는 게시글 검색
        data = noticeRepository.findById(notice.getId())
                .orElseThrow(() -> new IllegalArgumentException(notice.getId() + "notice is not exist."));

        // 게시글 제목, 내용 수정
        data.setSubject(notice.getSubject());
        data.setContent(notice.getContent());

        // 삭제할 파일 조회
        List<Long> ids = new ArrayList<>();
        for (ImageFile img : data.getImage()) {
            ids.add(img.getId());
        }

        // 수정 파일 추가
        List<ImageFile> imageFiles = saveFile(notice.getPhoto(), path, data.getDate(), store);
        data.setImage(imageFiles);

        // 파일 삭제 (disk)
        for (Long id : ids) {
            ImageFile tmp = imageFileRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(id + " image is not exist."));
            deleteImage(tmp.getFilePath(), tmp.getFileName());
        }
        // 파일 삭제 (DB)
        imageFileRepository.deleteAllById(ids);
    }

    public ImageFile findImageById(Long id) {

        return imageFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " image is not exist."));
    }
}
