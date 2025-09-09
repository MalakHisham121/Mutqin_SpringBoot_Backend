package org.example.mutqinbackend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication; // ✅ CORRECT IMPORT
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ CORRECT @Value usage with property name and default value
    @Value("${app.oauth2.redirect-uri:http://localhost:3000/auth/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException { // ✅ Added ServletException

        logger.info("OAuth2 authentication successful");

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            logger.info("Generating JWT token for OAuth2 user: {}", email);

            // Generate JWT token for the OAuth2 user
            String token = jwtTokenProvider.generateTokenForOAuth2User(email);

            // Build redirect URL with token and user info
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                    .queryParam("name", URLEncoder.encode(name != null ? name : "", StandardCharsets.UTF_8))
                    .queryParam("email", URLEncoder.encode(email != null ? email : "", StandardCharsets.UTF_8))
                    .build().toUriString();

            logger.info("Redirecting OAuth2 user to: {}", targetUrl);

            // Clear authentication attributes from session
            clearAuthenticationAttributes(request);

            // Redirect to frontend with token
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            logger.error("Error during OAuth2 authentication success handling", e);

            // Redirect to error page if something goes wrong
            String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "oauth2_error")
                    .queryParam("message", URLEncoder.encode("Authentication failed", StandardCharsets.UTF_8))
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}