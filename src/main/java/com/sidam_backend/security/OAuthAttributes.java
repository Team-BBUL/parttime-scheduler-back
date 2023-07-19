package com.sidam_backend.security;

import com.sidam_backend.data.Account;
import com.sidam_backend.data.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String profile;
    private String email;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuthAttributes.builder()
                .profile((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Account toEntity() {
        Account account = new Account();
        account.setName(profile);
        account.setEmail(email);
        account.setRole(Role.ROLE_USER);
        return account;
    }
}
