package com.blog.velogclone.service;

import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
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

    @Transactional
    public int updateProfileImage(String fileFullPath) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

        Optional<User> findUser = userRepository.findById(userNo);
        User user;

        user = User.builder()
                .userId(findUser.get().getUserId())
                .userNo(userNo)
                .profileImg(fileFullPath)
                .userEmail(findUser.get().getUserEmail())
                .userNickname(findUser.get().getUserNickname())
                .userGithub(findUser.get().getUserGithub())
                .userHomepage(findUser.get().getUserHomepage())
                .userIntroduce(findUser.get().getUserIntroduce())
                .userTwitter(findUser.get().getUserTwitter())
                .userBlogName(findUser.get().getUserBlogName())
                .roleNo(findUser.get().getRoleNo())
                .userRegistDate(findUser.get().getUserRegistDate())
                .userStatus(findUser.get().getUserStatus())
                .build();
        try {
            User responseUser = userRepository.save(user);

            if(!ObjectUtils.isEmpty(responseUser)) {
                return 1;
            }
        } catch (DataIntegrityViolationException e) {
            log.info(String.valueOf(e));
        }

        return 0;
    }
}
