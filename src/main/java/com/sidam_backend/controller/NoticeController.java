package com.sidam_backend.controller;

import com.sidam_backend.data.ImageFile;
import com.sidam_backend.data.Notice;
import com.sidam_backend.data.Store;

import com.sidam_backend.resources.DTO.GetNotice;
import com.sidam_backend.resources.DTO.GetNoticeList;
import com.sidam_backend.resources.DTO.PostNotice;

import com.sidam_backend.resources.DTO.UpdateNotice;
import com.sidam_backend.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/notice/{storeId}")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final String uploadPath = "C:\\notice\\images\\";


    // 공지사항 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> makeNotice(
            @PathVariable Long storeId,
            PostNotice input
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;
        Notice notice;

        log.info("post notice: " + storeId + "store subject length"
                + input.getSubject().length() + "/ content length" + input.getBody().length());

        try {
            store = noticeService.validatedStoreId(storeId);
            notice = input.toNotice(store);
            notice.setImage(noticeService.saveFile(input.getImages(), uploadPath, notice.getDate(), store));
            noticeService.saveNotice(notice);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (IOException e) {
            res.put("message", "file save failed.");
            return ResponseEntity.internalServerError().body(res);
        }

        // 공지사항 작성 알림 주기

        res.put("message", "notice save successful");
        return ResponseEntity.ok(res);
    }

    // 목록 조회
    @GetMapping("/view/list")
    public ResponseEntity<Map<String, Object>> getAllNotice(
            @PathVariable Long storeId,
            @RequestParam int last
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;
        int lastId = last;
        List<GetNoticeList> result;

        log.info("get notice list: " + storeId + "store");

        try {
            store = noticeService.validatedStoreId(storeId);

            if (last == 0) { lastId = (int) noticeService.getLastId(store) + 1; }
            log.info("last ID: " + lastId);

            result = noticeService.findAllList(store, lastId);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", result);
        return ResponseEntity.ok(res);
    }

    // 상세 조회
    @GetMapping("/view/detail")
    public ResponseEntity<Map<String, Object>> getDetail(
            @PathVariable Long storeId,
            @RequestParam("id") Long noticeId
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("notice detail: store" + storeId + " notice" + noticeId);

        GetNotice notice;

        try {
            noticeService.validatedStoreId(storeId);
            notice = noticeService.findId(noticeId, "/api/notice/" + storeId + "/download/");
        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", notice);

        return ResponseEntity.ok(res);
    }

    // 공지사항 첨부파일 이미지 다운로드 url
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId,
            @PathVariable Long storeId,
            @RequestParam String filename
    ) {

        log.info(filename + " download");

        UrlResource resource;
        Resource defaultImage = new ClassPathResource("images/default-image.jpg");

        ImageFile image;
        String uploadName;

        try {
            noticeService.validatedStoreId(storeId);
            image = noticeService.findImageById(fileId);
            resource = new UrlResource("file:" + image.getFilePath());

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(defaultImage);
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }

        uploadName = image.getOrigName();
        String encodedUploadName = UriUtils.encode(uploadName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    // 수정
    @PutMapping("/view/detail")
    public ResponseEntity<Map<String, Object>> editNotice(
            @PathVariable Long storeId,
            UpdateNotice body
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("update notice: " + storeId + "store, " + body.getId() + "notice");

        Store store;

        try {
            store = noticeService.validatedStoreId(storeId);
            noticeService.updateNotice(body, uploadPath, store);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (IOException ex) {
            log.error(ex.getMessage());
            res.put("message", "file processing failed");
            return ResponseEntity.internalServerError().body(res);
        }

        // 공지사항 수정 알림 주기

        res.put("message", "update successful");
        return ResponseEntity.ok(res);
    }

    // 삭제
    @DeleteMapping("/view/detail")
    public ResponseEntity<Map<String, Object>> deleteNotice(
            @PathVariable Long storeId,
            @RequestParam("id") Long noticeId
    ) {
        Map<String, Object> res = new HashMap<>();

        log.info("delete notice: " + storeId + "store, " + noticeId + "notice");

        try {
            noticeService.validatedStoreId(storeId);
            noticeService.deleteNotice(noticeId);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("message", "delete successful");
        return ResponseEntity.ok(res);
    }
}
