package com.blog.velogclone.service;

import com.blog.velogclone.entity.Like;
import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.repository.LikeRepository;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
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

    @Transactional
    public boolean likeCancle(PostDTO postDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();

            Like findLike = likeRepository.findByUser_UserNoAndPost_PostNo(userNo, postDTO.getPostNo());
            LikeDTO findDTO = modelMapper.map(findLike, LikeDTO.class);

            if(!findDTO.isNull() && findDTO.getPost().getPostNo() == postDTO.getPostNo() && findDTO.getUser().getUserNo() == userNo) {
                log.info("findDTO: {}", findDTO.getReadNo());
                likeRepository.deleteByReadNo(findDTO.getReadNo());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkLike(LikeDTO likeDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
                Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

                List<Like> findLike = likeRepository.findAllByUser_UserNo(userNo);
                Post post = postRepository.findById(likeDTO.getPostNo()).orElse(null);

                for(Like likeList : findLike) {
                    if(likeList.getPost().getPostNo().compareTo(post.getPostNo()) == 0) {
                        log.info("LikeService : {}", "읽기 목록에 존재함");
                        return true;
                    }
                }
            }
        } catch (AuthenticationException e) {
            log.info("로그인 상태 아님");
        } catch (EmptyResultDataAccessException e) {
            log.info("읽기 목록에 존재하지 않음");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PostDTO> findLikeList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long UserNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

        List<Like> likeEntityList = likeRepository.findByUserNo(UserNo);

        List<Post> postEntityList = new ArrayList<>();
        List<PostDTO> postList = new ArrayList<>();

        for(Like list : likeEntityList) {
            postEntityList.add(list.getPost());
        }

        for(Post list : postEntityList) {
            postList.add(modelMapper.map(list, PostDTO.class));
        }

        return postList;
    }

    public int countLike(Long postNo) {
        return Integer.parseInt(likeRepository.countByPostPostNo(postNo));
    }
}
