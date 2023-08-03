package com.sidam_backend.security;

import com.sidam_backend.data.Account;
import com.sidam_backend.data.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
@Builder
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String profile;
    private String email;
    private String oauth2Id;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        log.info("OAuthAttributes = {}", attributes);
        log.info("OAuthAttributes.attributes.id = {}", attributes.get("id"));
        log.info("userNameAttrubiteName = {}",userNameAttributeName);
        return OAuthAttributes.builder()
                .profile((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .oauth2Id(attributes.get("id").toString())
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Account toEntity() {
        Account account = new Account();
        account.setName(profile);
        account.setEmail(email);
        account.setRole(Role.EMPLOYEE);
        account.setOauth2Id(oauth2Id);
        account.setOnceVerified(false);
        return account;
    }
}
