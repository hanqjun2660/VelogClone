package com.blog.velogclone.service;

import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

        Optional<User> findId = userRepository.findByUserId(userDTO.getUserId());

        if(findId.isPresent()) {
            return -1;
        }

        userDTO.setUserPw(passwordEncoder.encode(userDTO.getUserPw()));

        User user;

        user = User.builder()
                .userId(userDTO.getUserId())
                .userPw(userDTO.getUserPw())
                .userNickname(userDTO.getUserNickname())
                .userEmail(userDTO.getUserEmail())
                .userBlogName(userDTO.getUserId())
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

    @Transactional
    public int deleteProfileImage(Long userNo) {
        int result = userRepository.deleteProfileImage(userNo);

        if (result > 0) {
            // 삭제 성공한 경우에만 동작
            userRepository.findById(userNo)
                    .ifPresent(user -> {
                        PrincipalDetails userDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        userDetails.getUser().updateProfileImagePath(user.getProfileImg());
                    });
        } else {
            log.warn("프로필 이미지 삭제 실패 - 해당 유저가 존재하지 않음");
        }

        return result;
    }

    @Transactional
    public int updateProfile(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

        Optional<User> findUser = userRepository.findById(userNo);
        User user;

        user = User.builder()
                .userId(findUser.get().getUserId())
                .userNo(userNo)
                .profileImg(findUser.get().getProfileImg())
                .userEmail(findUser.get().getUserEmail())
                .userNickname(userDTO.getUserNickname())
                .userGithub(findUser.get().getUserGithub())
                .userHomepage(findUser.get().getUserHomepage())
                .userIntroduce(userDTO.getUserIntroduce())
                .userTwitter(findUser.get().getUserTwitter())
                .userBlogName(findUser.get().getUserBlogName())
                .userFacebook(findUser.get().getUserFacebook())
                .roleNo(findUser.get().getRoleNo())
                .userRegistDate(findUser.get().getUserRegistDate())
                .userStatus(findUser.get().getUserStatus())
                .build();

        try {
            User responseUser = userRepository.save(user);

            if(!ObjectUtils.isEmpty(responseUser)) {
                userRepository.findById(userNo)
                        .ifPresent(finduser -> {
                            PrincipalDetails userDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                            userDetails.getUser().updateNickname(finduser.getUserNickname());
                            userDetails.getUser().updateIntroduce(finduser.getUserIntroduce());
                        });
                return 1;
            }
        } catch (DataIntegrityViolationException e) {
            log.info(String.valueOf(e));
        }

        return 0;
    }

    @Transactional
    public int updateBlogName(UserDTO userDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

            Optional<User> optionalUser = userRepository.findById(userNo);

            Optional<User> findBlogName = userRepository.findByUserBlogNameAndUserStatus(userDTO.getUserBlogName(), "N");
            if(findBlogName.isPresent()) {
                return -1;
            }

            optionalUser.ifPresent(user -> {
                user.setUserBlogName(userDTO.getUserBlogName());
                userRepository.save(user);
            });

            Optional<User> updatedUser = userRepository.findById(userNo);

            updatedUser.ifPresent(updateduser -> {
                PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
                userDetails.getUser().setUserBlogName(updateduser.getUserBlogName());
            });

            return 1;

        } catch (Exception e) {
            log.error("updateBlogName Service : { }", e);
        }

        return 0;
    }

    @Transactional
    public int updateSocialInfo(UserDTO userDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

            Optional<User> optionalUser = userRepository.findById(userNo);

            optionalUser.ifPresent(findUser -> {
                findUser.setUserEmail(userDTO.getUserEmail());
                findUser.setUserGithub(userDTO.getUserGithub());
                findUser.setUserFacebook(userDTO.getUserFacebook());
                findUser.setUserTwitter(userDTO.getUserTwitter());
                findUser.setUserHomepage(userDTO.getUserHomepage());
                userRepository.save(findUser);
            });

            Optional<User> updatedUser = userRepository.findById(userNo);

            updatedUser.ifPresent(updateduser -> {
                PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
                userDetails.getUser().setUserEmail(updateduser.getUserEmail());
                userDetails.getUser().setUserGithub(updateduser.getUserGithub());
                userDetails.getUser().setUserFacebook(updateduser.getUserFacebook());
                userDetails.getUser().setUserTwitter(updateduser.getUserTwitter());
                userDetails.getUser().setUserHomepage(updateduser.getUserHomepage());
            });

            return 1;

        } catch (Exception e) {
            log.error("updateSocialInfo Service : { }", e);
        }

        return 0;
    }

    @Transactional
    public UserDTO selectUser(String blogname) {
        Optional<User> findUser = userRepository.findByUserBlogNameAndUserStatus(blogname, "N");

        if (findUser.isPresent()) {
            return modelMapper.map(findUser, UserDTO.class);
        } else {
            throw new EntityNotFoundException("User not found with blogname: " + blogname);
        }
    }

    public Long findUserNo(String blogName) {
        Optional<User> optionalUser = userRepository.findByUserBlogNameAndUserStatus(blogName, "N");

        if(optionalUser.isPresent()) {
            UserDTO userDTO = modelMapper.map(optionalUser.get(), UserDTO.class);
            return userDTO.getUserNo();
        } else {
            return 0L;
        }
    }

    @Transactional
    public String withDrawal(Long userNo) {

        Optional<User> optionalUser = userRepository.findByUserNo(userNo);

        if(optionalUser.isPresent()) {
            optionalUser.get().setUserStatus("Y");
            try {
                userRepository.save(optionalUser.get());
                return "Y";
            } catch(IllegalArgumentException e) {
                log.error("withDrawal IllegalArgumentException : { }", e);
            }
        }

        return "N";
    }
}
