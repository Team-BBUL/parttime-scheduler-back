package com.sidam_backend.security;

import com.sidam_backend.data.Account;
import com.sidam_backend.repo.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountRepository accountRepository;


    public CustomOAuth2UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Account account = saveOrUpdate(attributes);

        log.info(attributes.getNameAttributeKey());
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(account.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Account saveOrUpdate(OAuthAttributes attributes) {
        Account account = (Account) accountRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getProfile()))
                .orElse(attributes.toEntity());
        return accountRepository.save(account);
    }
}
