package org.example.mutqinbackend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.mutqinbackend.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.oauth2.redirect-uri:http://localhost:8080/api/auth/success}")
    private String redirectUri;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String action = request.getParameter("action");

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String googleId = oauth2User.getAttribute("sub");


            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email is required for OAuth2 authentication");
            }
            if ("login".equals(action) && !oAuth2Service.emailExists(email)) {
                String errorUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/auth/error")
                        .queryParam("error", "user_not_found")
                        .queryParam("message", URLEncoder.encode("User does not exist", StandardCharsets.UTF_8))
                        .build().toUriString();
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
                return;
            }

            String token = jwtTokenProvider.generateTokenForOAuth2User(email);
            String tempURL = "http://localhost:8080/api/auth/success";// temp till front integration

            String targetUrl = UriComponentsBuilder.fromUriString(tempURL)
                    .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                    .queryParam("name", URLEncoder.encode(name != null ? name : "", StandardCharsets.UTF_8))
                    .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                    .queryParam("googleId", URLEncoder.encode(googleId != null ? googleId : "", StandardCharsets.UTF_8))
                    .build().toUriString();

           clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {

            String errorUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/auth/error")
                    .queryParam("error", "oauth2_processing_error")
                    .queryParam("message", URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8))
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}