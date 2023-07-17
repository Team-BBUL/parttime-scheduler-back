package com.sidam_backend.service;

import com.sidam_backend.data.*;
import com.sidam_backend.repo.*;

import com.sidam_backend.resources.FileUtils;
import com.sidam_backend.resources.DTO.GetNotice;
import com.sidam_backend.resources.DTO.GetNoticeList;
import com.sidam_backend.resources.DTO.UpdateNotice;
import com.sidam_backend.service.base.UsingAlarmService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NoticeService extends UsingAlarmService {

    public NoticeService(
            AlarmRepository alarmRepository,
            UserRoleRepository userRoleRepository,
            NoticeRepository noticeRepository,
            StoreRepository storeRepository,
            ImageFileRepository imageFileRepository,
            AlarmReceiverRepository receiverRepository
            ) {
        super(alarmRepository, userRoleRepository, receiverRepository);
        this.noticeRepository = noticeRepository;
        this.imageFileRepository = imageFileRepository;
        this.storeRepository = storeRepository;
    }

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;
    private final ImageFileRepository imageFileRepository;


    public Store validatedStoreId(Long id) {

        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " store is not exist."));
    }

    public List<ImageFile> saveFile(List<MultipartFile> images, String path, LocalDateTime now, Store store)
            throws IOException, IllegalArgumentException {

        List<ImageFile> imageFiles = new ArrayList<>();

        if (images == null) {
            return imageFiles;
        }

        for (int i = 0; i < images.size(); i++) {

            String name = FileUtils.generateFileName(now, i, store.getId());
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

    public long getLastId(Store store) {
        return noticeRepository.selectLastId(store.getId())
                .orElseThrow(() -> new IllegalArgumentException("notice table is empty"));
    }

    public void saveNotice(Notice content, Store store) {

        if (content.getImage() != null && content.getImage().size() > 0) {
            imageFileRepository.saveAll(content.getImage());
        }

        noticeRepository.save(content);

        employeeAlarmMaker(store, content.getSubject(), Alarm.Category.NOTICE, Alarm.State.ADD);

        noticeRepository.findById(content.getId())
                .orElseThrow(() -> new IllegalArgumentException("save failed"));

        log.debug("공지 저장 완료");

        employeeAlarmMaker(store, content.getSubject(), Alarm.Category.NOTICE, Alarm.State.ADD);
        log.debug("공지 알림 저장 완료");
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
            FileUtils.deleteImage(image.getFilePath(), image.getFileName());
        }

        List<ImageFile> imageFiles = notice.getImage();
        notice.setImage(new ArrayList<>());
        imageFileRepository.deleteAll(imageFiles);

        notice.setValid(false);
        notice.setDate(LocalDateTime.now().withNano(0));

        log.debug("공지 삭제 완료");
    }

    @Transactional
    public void updateNotice(UpdateNotice notice, String path, Store store)
            throws IOException, IllegalArgumentException{

        Notice data;

        // 입력값의 유효성 확인
        if (notice.getSubject().length() > 20 || notice.getSubject().length() == 0) {
            throw new IllegalArgumentException("number of subject characters exceeded.");
        }
        if (notice.getContent().length() > 200) {
            throw new IllegalArgumentException("number of content characters exceeded.");
        }

        // id에 맞는 게시글 검색
        data = noticeRepository.findById(notice.getId())
                .orElseThrow(() -> new IllegalArgumentException(notice.getId() + "notice is not exist."));

        // 게시글이 유효한지 확인
        if (data.isValid()) {
            throw new IllegalArgumentException("notice is invalid.");
        }

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
            FileUtils.deleteImage(tmp.getFilePath(), tmp.getFileName());
        }
        // 파일 삭제 (DB)
        imageFileRepository.deleteAllById(ids);

        // 알림 생성
        employeeAlarmMaker(store, notice.getSubject(), Alarm.Category.NOTICE, Alarm.State.UPDATE);
    }

    public ImageFile findImageById(Long id) {

        return imageFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " image is not exist."));
    }
}
