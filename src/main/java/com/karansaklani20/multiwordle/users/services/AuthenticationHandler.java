package com.karansaklani20.multiwordle.users.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.karansaklani20.multiwordle.users.dto.FindOrCreateUserRequest;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationHandler implements AuthenticationSuccessHandler {
        private final UserService userService;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                log.info("onAuthenticationSuccess: request received");

                OAuth2User oAuthUser = (OAuth2User) authentication.getPrincipal();
                System.out.println(oAuthUser.getAttributes());
                String email = oAuthUser.getAttribute("email");
                String id = oAuthUser.getAttribute("sub");
                log.info("onAuthenticationSuccess: authenticating for oAuthUser with email={}, id={}", email, id);

                User user = this.userService.findOrCreateUser(FindOrCreateUserRequest
                                .builder()
                                .email(oAuthUser.getAttribute("email"))
                                .name(oAuthUser.getAttribute("name"))
                                .givenName(oAuthUser.getAttribute("givenName"))
                                .picture(oAuthUser.getAttribute("picture"))
                                .build());
                log.info("onAuthenticationSuccess: user created for email={} with id={}", email, user.getId());

                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                String accessToken = JWT
                                .create()
                                .withSubject(user.getEmail())
                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000))
                                .withClaim("roles",
                                                oAuthUser.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                                                .toList())
                                .sign(algorithm);
                log.info("onAuthenticationSuccess: accessToken {}", accessToken);

                response.sendRedirect(String.format("http://localhost:3000/auth?token=%s", accessToken));
        }

}
