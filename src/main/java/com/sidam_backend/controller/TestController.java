package com.sidam_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/")
    public String hello() {
        log.info("TestController hello");
        return "hello world!";
    }

    @GetMapping("/test")
    public Long testRequestParam(
            @AuthenticationPrincipal Long id,
            @RequestParam("id") Long roleId
    ) {
        log.info("Tescontroller.testRequestParam() ={}, {}", id,roleId);
        return roleId;
    }

    @GetMapping("/test/{roleId}")
    public Long testPathVariable(
            @AuthenticationPrincipal Long id,
            @PathVariable Long roleId
    ) {
        log.info("Tescontroller.testPathVariable() ={} , {}", id, roleId);
        return roleId;
    }

    @GetMapping("/test/{roleId}/{storeId}")
    public Long testWithStoreId(
            @AuthenticationPrincipal Long id,
            @PathVariable Long roleId,
            @PathVariable Long storeId
    ) {
        log.info("Tescontroller.testWithStoreId() ={}, {}, {}", id, roleId, storeId);
        return storeId;
    }
}
