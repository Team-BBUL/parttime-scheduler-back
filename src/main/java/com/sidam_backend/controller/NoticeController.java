package com.sidam_backend.controller;

import com.sidam_backend.data.*;

import com.sidam_backend.resources.DTO.GetNotice;
import com.sidam_backend.resources.DTO.GetNoticeList;
import com.sidam_backend.resources.DTO.PostNotice;

import com.sidam_backend.resources.DTO.UpdateNotice;

import com.sidam_backend.security.AccountDetail;
import com.sidam_backend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final String uploadPath = "/home/ubuntu/server/sidam/images";


    // 공지사항 작성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> makeNotice(
            @PathVariable Long storeId,
            PostNotice input
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;
        Notice notice;

        log.info("post notice: " + storeId + "store subject length"
                + input.getSubject().length() + "/ content length" + input.getContent().length());

        try {
            store = noticeService.validatedStoreId(storeId);
            notice = input.toNotice(store);
            notice.setImage(noticeService.saveFile(input.getImages(), uploadPath, notice.getDate(), store));
            notice = noticeService.saveNotice(notice);

            // 알림 주기
            noticeService.employeeAlarmMaker(store, notice.getSubject(),
                    Alarm.Category.NOTICE, Alarm.State.ADD, notice.getId());

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (IOException e) {
            res.put("message", "file save failed.");
            return ResponseEntity.internalServerError().body(res);
        }
        res.put("id", notice.getId());
        res.put("timeStamp", notice.getDate());
        res.put("message", "notice save successful");
        return ResponseEntity.ok(res);
    }

    // 목록 조회
    @GetMapping(value = "/view/list", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getAllNotice(
            @PathVariable Long storeId,
            @RequestParam int last,
            @RequestParam int cnt,
            @RequestParam("role") Long roleId
    ) {

        Map<String, Object> res = new HashMap<>();

        Store store;
        long lastId = last;
        List<GetNoticeList> result;
        AccountRole role;

        log.info("get notice list: " + storeId + "store");

        try {
            store = noticeService.validatedStoreId(storeId);
            role = noticeService.validatedRoleId(roleId);

            if (last == 0) { lastId = (int) noticeService.getLastId(store) + 1; }
            log.info("last ID: " + lastId);

            result = noticeService.findAllList(store, lastId, cnt, role);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", result);
        return ResponseEntity.ok(res);
    }

    // 상세 조회
    @GetMapping(value = "/view/detail", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getDetail(
            @AuthenticationPrincipal AccountDetail accountDetail,
            @PathVariable Long storeId,
            @RequestParam("id") Long noticeId
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("notice detail: store" + storeId + " notice" + noticeId);

        Notice notice;
        GetNotice result;
        AccountRole role;

        try {
            noticeService.validatedStoreId(storeId);
            role = noticeService.validatedRoleId(accountDetail.getId());
            notice = noticeService.findId(noticeId);

            result = notice.toGetNotice("/api/notice/" + storeId + "/download/");

            noticeService.readCheck(notice, role);

        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }

        res.put("data", result);

        return ResponseEntity.ok(res);
    }

    // 공지사항 첨부파일 이미지 다운로드 url
    @GetMapping(value = "/download/{fileId}", produces = "application/json; charset=UTF-8")
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
            @RequestBody UpdateNotice body
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
