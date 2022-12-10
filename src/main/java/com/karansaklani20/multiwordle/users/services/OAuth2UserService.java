package com.karansaklani20.multiwordle.users.services;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom service just incase there are further validations at some other point
 */
@Service
@AllArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        log.info("loadUser: request received");
        OAuth2User user = super.loadUser(oAuth2UserRequest);
        log.info("loadUser: user loaded from oAuth request", user);
        return user;
    }
}
