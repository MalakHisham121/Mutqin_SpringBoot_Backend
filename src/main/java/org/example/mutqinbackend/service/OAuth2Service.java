package org.example.mutqinbackend.service;

import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OAuth2Service extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        OAuth2User oAuth2User;
        try {
            oAuth2User = super.loadUser(userRequest);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException(e.getMessage());
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email is required");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(name);
            user.setRole("STUDENT");
            user.setPoints(0);
            user.setGoogleId(googleId);
            user.setProvider("GOOGLE");
            userRepository.save(user);
        } else {
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                userRepository.save(user);
            }
        }

        return oAuth2User;
    }
}