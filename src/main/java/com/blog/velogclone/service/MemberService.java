package com.blog.velogclone.service;

import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    public int registUser(UserDTO userDTO) {

        userDTO.setUserPw(passwordEncoder.encode(userDTO.getUserPw()));

        User user;

        user = User.builder()
                .userId(userDTO.getUserId())
                .userPw(userDTO.getUserPw())
                .userNickname(userDTO.getUserNickname())
                .userEmail(userDTO.getUserEmail())
                .build();

        userRepository.save(user);

        return Integer.parseInt(String.valueOf(user.getUserNo()));
    }
}
