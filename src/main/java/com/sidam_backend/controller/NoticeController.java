package com.sidam_backend.controller;

import com.sidam_backend.data.Store;

import com.sidam_backend.resources.GetNotice;
import com.sidam_backend.resources.GetNoticeList;
import com.sidam_backend.resources.PostNotice;

import com.sidam_backend.resources.UpdateNotice;
import com.sidam_backend.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/notice/{storeId}")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final String uploadPath = "/notice/images/";

    // 공지사항 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> makeNotice(
            @PathVariable Long storeId,
            @RequestParam PostNotice input
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;

        log.info("post notice: " + storeId + "store "
                + input.getSubject().length() + "/" + input.getBody().length());

        try {
            store = noticeService.validatedStoreId(storeId);
            noticeService.saveNotice(input.toNotice(store, uploadPath));

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

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
        List<GetNoticeList> result;

        log.info("get notice list: " + storeId + "store");

        try {
            store = noticeService.validatedStoreId(storeId);
            result = noticeService.findAllList(store, last);

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
            @RequestParam Long noticeId
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("notice detail: store" + storeId + " notice" + noticeId);

        GetNotice notice;

        try {
            noticeService.validatedStoreId(storeId);
            notice = noticeService.findId(noticeId, "/api/notice/{storeId}/download/");
        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", notice);

        return ResponseEntity.ok(res);
    }

    // 공지사항 첨부파일 이미지 다운로드 url
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            @PathVariable String storeId
    ) throws IOException {

        log.info(filename + " download");

        Resource resource;
        Resource defaultImage = new ClassPathResource("images/default-image.jpg");

        try {
            resource = new UrlResource(uploadPath + filename);

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(defaultImage);
            }

        } catch (MalformedURLException ex) {

            return ResponseEntity.badRequest().build();
        }
    }

    // 수정
    @PostMapping("/view/detail")
    public ResponseEntity<Map<String, Object>> editNotice(
            @PathVariable Long storeId,
            @RequestBody UpdateNotice body
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;

        try {
            store = noticeService.validatedStoreId(storeId);
            noticeService.updateNotice(body, uploadPath, store);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("message", "update success");
        return ResponseEntity.ok(res);
    }

    // 삭제
    @DeleteMapping("/view/detail")
    public ResponseEntity<Map<String, Object>> deleteNotice(
            @PathVariable Long storeId,
            @RequestParam Long noticeId
    ) {
        Map<String, Object> res = new HashMap<>();

        Store store;

        try {
            store = noticeService.validatedStoreId(storeId);
            noticeService.deleteNotice(noticeId);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        return ResponseEntity.ok(res);
    }
}
