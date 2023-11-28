package com.blog.velogclone.service;

import com.blog.velogclone.entity.Like;
import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.repository.LikeRepository;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    public LikeDTO likeAdd(LikeDTO likeDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

            List<Like> findLike = likeRepository.findAllByUser_UserNo(userNo);
            Post post = postRepository.findById(likeDTO.getPostNo()).orElse(null);

            for(Like likeList : findLike) {
                if(likeList.getPost().getPostNo().compareTo(post.getPostNo()) == 0) {
                    log.info("LikeService : {}", "이미 읽기 목록에 추가되어 있음");
                    return new LikeDTO();
                }
            }

            likeDTO.setUserNo(userNo);

            Like request = Like.builder()
                    .post(Post.builder().postNo(Objects.requireNonNull(post).getPostNo()).build())
                    .user(User.builder().userNo(likeDTO.getUserNo()).build())
                    .build();

            Like responseEntity = likeRepository.save(request);

            return modelMapper.map(responseEntity, LikeDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LikeDTO();
    }
}
