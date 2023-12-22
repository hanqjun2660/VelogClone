package com.blog.velogclone.controller;

import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/user")
public class MemberController {
    private final String uploadDir = Paths.get("C:", "profile", "upload").toString();

    private final MemberService memberService;

    @GetMapping("/regist")
    public String regist(Model model) {
        model.addAttribute("title", "회원가입");
        return "user/regist";
    }

    @PostMapping("/regist")
    @ResponseBody
    public Map<String, String> registUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        int result = memberService.registUser(userDTO);

        if(result >= 1) {
            response.put("status", "200");
            response.put("msg", "회원가입에 성공하였습니다. 로그인 해주세요.");
            return response;
        } else if(result == 0) {
            response.put("msg", "회원가입에 실패하였습니다.");
        } else {
            response.put("msg", "중복된 아이디 입니다.");
        }

        response.put("status", "500");

        return response;
    }

    @PostMapping(value = "/updateimage", consumes = "multipart/form-data")
    @ResponseBody
    public Map<String, Object> updateProfileImage(@RequestPart("profileImage") final MultipartFile profileImage) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (profileImage.isEmpty()) {
                log.info("이미지가 없음");
                responseMap.put("msg", "update fail - no image");
                return responseMap;
            }

            String orgFileName = profileImage.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String extension = orgFileName.substring(orgFileName.lastIndexOf(".") + 1);
            String saveFileName = uuid + "." + extension;
            String fileFullPath = Paths.get(uploadDir, saveFileName).toString();

            File dir = new File(uploadDir);
            // 해당 경로가 없으면 경로에 맞게 폴더를 생성
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    log.info("디렉토리 생성 성공: {}", uploadDir);
                } else {
                    log.error("디렉토리 생성 실패: {}", uploadDir);
                    responseMap.put("msg", "update fail - directory creation failed");
                    return responseMap;
                }
            }

            File uploadFile = new File(fileFullPath);
            profileImage.transferTo(uploadFile);

            int result = memberService.updateProfileImage(convertToWebPath(fileFullPath));
            if (result >= 1) {
                PrincipalDetails userDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                userDetails.getUser().updateProfileImagePath(convertToWebPath(fileFullPath));
                responseMap.put("msg", "update success");
            } else {
                responseMap.put("msg", "update fail - database update failed");
            }

            return responseMap;
        } catch (IOException e) {
            log.error("FileUpload Fail!!", e);
            responseMap.put("msg", "update fail - file upload failed");
            return responseMap;
        }
    }

    @PostMapping("/deleteimage")
    @ResponseBody
    public Map<String, Object> deleteProfileImage(Authentication authentication) {
        Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();
        Map<String, Object> responseMap = new HashMap<>();
        int result = memberService.deleteProfileImage(userNo);
        if (result > 0) {
            responseMap.put("msg", "delete profile Image success");
        } else {
            responseMap.put("msg", "delete profile Image fail");
        }

        return responseMap;
    }

    @PostMapping("/updateProfile")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestBody UserDTO userDTO) {
        log.info(userDTO.toString());
        int result = memberService.updateProfile(userDTO);

        Map<String, Object> response = new HashMap<>();

        if(result > 0) {
            response.put("msg", "profileUpdate Success");
        } else {
            response.put("msg", "profileUpdate fali");
        }
        return response;
    }

    @PostMapping("/updateBlogName")
    @ResponseBody
    public Map<String, Object> updateBlogName(@RequestBody UserDTO userDTO) {
        log.info(userDTO.toString());
        int result = memberService.updateBlogName(userDTO);

        Map<String, Object> response = new HashMap<>();

        if(result > 0) {
            response.put("msg", "updateBlogName Success");
        } else if(result == -1) {
            response.put("status", "400");
            response.put("msg", "중복된 클로그명이 존재합니다.");
        } else {
            response.put("msg", "updateBlogName fali");
        }

        return response;
    }

    @PostMapping("/updateSocialInfo")
    @ResponseBody
    public Map<String, Object> updateSocialInfo(@RequestBody UserDTO userDTO) {
        log.info(userDTO.toString());

        int result = memberService.updateSocialInfo(userDTO);

        Map<String, Object> response = new HashMap<>();

        if(result > 0) {
            response.put("msg", "updateSocialInfo Success");
        } else {
            response.put("msg", "updateSocialInfo fali");
        }

        return response;
    }

    @PostMapping("/withdrawal")
    @ResponseBody
    public Map<String, String> withDrawal(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

        String result = memberService.withDrawal(userNo);
        response.put("status", result);

        return response;
    }

    public static String convertToWebPath(String localPath) {
        return localPath.replace("C:\\profile\\upload\\", "/profile/upload/");
    }

}
