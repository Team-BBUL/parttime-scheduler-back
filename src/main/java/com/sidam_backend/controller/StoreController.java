package com.sidam_backend.controller;

import com.sidam_backend.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStore(
            @RequestParam("input") String input
    ) {

        Map<String, Object> res = new HashMap<>();

        log.info("search store: input = " + input + ", " + input.length() + "L");

        if (input.length() < 2) {
            res.put("message", "input is too short.");
            return ResponseEntity.badRequest().body(res);
        }

        try {
            res.put("data", storeService.findStore(input));
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping("/search/all")
    public ResponseEntity<Map<String, Object>> searchAllStore() {

        Map<String, Object> res = new HashMap<>();

        log.info("search all store name");

        res.put("data", storeService.findAllStoreName());
        return ResponseEntity.ok(res);
    }
}
