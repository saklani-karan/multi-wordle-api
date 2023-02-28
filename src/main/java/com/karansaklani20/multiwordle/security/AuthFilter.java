package com.karansaklani20.multiwordle.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    private final String[] whiteList = {
            "^/oauth/*",
            "^/ws/*"
    };
    private UserDetailsService userDetailsService;

    public AuthFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("doFilterInterval: request recieved for path {}", request.getServletPath());
        if (this.isPublic(request.getServletPath())) {
            log.info("doFilterInterval: request path {} public", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }
        String authToken = request.getHeader("Authorization");
        log.info("doFilterInterval: authToken {}", authToken);
        if ((authToken != null) && (authToken.startsWith("Bearer"))) {
            log.info("doFilterInterval: authToken {} format accepted", authToken);
            try {
                authToken = authToken.split(" ")[1];
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(authToken);

                log.info("doFilterInterval: decoded jwt successfully");
                String email = decodedJWT.getSubject();
                log.info("doFilterInterval: username {}", email);
                List<String> roles = List.of(decodedJWT.getClaim("roles").asArray(String.class));
                log.info("doFilterInterval: roles", roles.toString());
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                roles.stream().forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                if (userDetails == null) {
                    throw new Exception("User not found with email");
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        email, null, authorities);
                log.info("doFilterInterval: created authenticationToken");
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("doFilterInterval setAuthentication for SecurityContextHolder");
            } catch (Exception exception) {
                log.error("doFilterInterval: error while authenticating request", exception);
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("message", exception.getMessage());
                error.put("code", "E_AUTH_FILTER_001");
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
                return;
            }
            filterChain.doFilter(request, response);
        } else {
            log.info("doFilterInterval: invalid authToken {}", authToken);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Missing or invalid auth token");
            error.put("code", "E_AUTH_FILTER_001");
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return;
        }
    }

    private boolean isPublic(String servletName) {
        log.info("isPublic received servletName {}", servletName);
        for (int i = 0; i < this.whiteList.length; i++) {
            Pattern pattern = Pattern.compile(this.whiteList[i]);
            if (pattern.matcher(servletName).find()) {
                log.info("found match for pattern {}", this.whiteList[i]);
                return true;
            }
        }
        return false;
    }
}
