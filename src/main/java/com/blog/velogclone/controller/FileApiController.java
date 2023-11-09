package com.blog.velogclone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/tui-editor")
public class FileApiController {

    // 파일 업로드 경로
    private final String uploadDir = Paths.get("C:", "tui-editor", "upload").toString();

    @PostMapping("/image-upload")
    public String uploadEditorImage(@RequestParam final MultipartFile image) {
        if(image.isEmpty()) {
            return "";
        }

        String orgFileName = image.getOriginalFilename();
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
            image.transferTo(uploadFile);
            return saveFileName;
        } catch (IOException e) {
            log.info("FileUpload Fail!!");
            throw new RuntimeException();
        }
    }

    @GetMapping(value = "/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] printEditorImage(@RequestParam final String filename) {
        // 업로드된 파일의 전체 경로
        String fileFullPath = Paths.get(uploadDir, filename).toString();

        // 파일이 없는 경우 예외 throw
        File uploadedFile = new File(fileFullPath);
        if (uploadedFile.exists() == false) {
            throw new RuntimeException();
        }

        try {
            // 이미지 파일을 byte[]로 변환 후 반환
            byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
            return imageBytes;

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }
}
