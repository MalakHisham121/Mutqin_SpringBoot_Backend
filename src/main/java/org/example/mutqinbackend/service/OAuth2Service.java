package org.example.mutqinbackend.service;

import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User  loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
        OAuth2User oAuth2User= super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");
        User user = userRepository.findByEmail(email).get();
        if (user == null) {
            // Create new user if doesn't exist
            user = new User();
            user.setUsername(email); // Use email as username for OAuth2 users
            user.setEmail(email);
            user.setPassword(""); // No password for OAuth2 users
            user.setRole("STUDENT"); // Default role
            user.setPoints(0);
            user.setGoogleId(googleId); // Store Google ID
            user.setProvider("GOOGLE"); // Store provider info
            userRepository.save(user);
        } else {
            // Update existing user's Google ID if not set
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                userRepository.save(user);
            }
        }

        return oAuth2User;
    }
}
