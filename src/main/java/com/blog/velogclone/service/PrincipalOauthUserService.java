package com.blog.velogclone.service;

import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes: {}", oAuth2User.getAttributes());

        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        Map<String, Object> account = oAuth2User.getAttribute("kakao_account");

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("id").toString();
        String loginId = provider + "_" + providerId;
        String email = (String) account.get("email");
        String profileImg = (String) properties.get("profile_image");

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserIdAndUserStatus(loginId, "N"));
        User user;

        // 로그인을 시도한 계정이 DB에 존재하지 않는다면
        if(optionalUser.isEmpty()) {
            user = User.builder()       // oAuth 정보를 이용해 회원가입을 진행시킨다.
                    .userId(loginId)
                    .userEmail(email)
                    .provider(provider)
                    .profileImg(profileImg)
                    .userNickname((String) properties.get("nickname"))
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
