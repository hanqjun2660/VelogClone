package com.blog.velogclone.controller;

import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return "/user/regist";
    }

    @PostMapping("/regist")
    @ResponseBody
    public Map<String, String> registUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        int result = memberService.registUser(userDTO);

        if(result >= 1) {
            response.put("msg", "회원가입에 성공하였습니다.");
        } else {
            response.put("msg", "회원가입에 실패하였습니다.");
        }

        return response;
    }

    @PostMapping(value = "/updateimage", consumes = "multipart/form-data")
    @ResponseBody
    public String updateProfileImage(@RequestPart("profileImage") final MultipartFile profileImage) {
        if(profileImage.isEmpty()) {
           log.info("이미지가 없음");
        }

        String orgFileName = profileImage.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = orgFileName.substring(orgFileName.lastIndexOf(".") + 1);
        String saveFileName = uuid + "." + extension;
        String fileFullPath = Paths.get(uploadDir, saveFileName).toString();

        File dir = new File(uploadDir);
        if(dir.exists() == false) {     // 해당 경로가 없으면 경로에 맞게 폴더를 생성
            dir.mkdirs();
        }

        try {
            File uploadFile = new File(fileFullPath);
            profileImage.transferTo(uploadFile);
            return saveFileName;
        } catch (IOException e) {
            log.info("FileUpload Fail!!");
            throw new RuntimeException();
        }
    }

}
