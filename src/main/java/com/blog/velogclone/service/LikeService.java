package com.blog.velogclone.service;

import com.blog.velogclone.entity.Like;
import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.repository.LikeRepository;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    final String DEFAULT_IMG = "/images/clog_text_logo.png";

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

    @Transactional
    public int countLike(Long postNo) {
        try {
            return Integer.parseInt(likeRepository.countByPostPostNo(postNo));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Transactional
    public List<PostDTO> findByUserUserNoAndPostStatus(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userNo = ((PrincipalDetails)authentication.getPrincipal()).getUserNo();

            List<Like> userLike = likeRepository.findByUserUserNo(userNo);
            List<PostDTO> list = new ArrayList<>();

            // 조회된 결과가 존재하면
            if (!userLike.isEmpty()) {
                // 해당되는 게시글 조회
                list = userLike.stream()
                        .flatMap(like -> {
                            Page<Post> findPost = postRepository.findByPostNoAndPostStatus(like.getPost().getPostNo(), "N", pageable);
                            return findPost.stream().map(post -> modelMapper.map(post, PostDTO.class));
                        })
                        .collect(Collectors.toList());

                list.stream().map(post -> {
                    // 게시글의 이미지 추출
                    String postBody = post.getPostBody();
                    Document doc = Jsoup.parse(postBody);
                    Elements imgTags = doc.select("img");

                    if (!imgTags.isEmpty()) {
                        Element firstImg = imgTags.first();
                        String srcAttribute = firstImg.attr("src");
                        post.setSrcAttr(srcAttribute);
                    } else {
                        post.setSrcAttr(DEFAULT_IMG);
                    }

                    // 조회된 댓글중 삭제되지 않은 댓글만
                    List<ReplyDTO> replyDTOList = post.getReplies().stream()
                            .filter(reply -> "N".equals(reply.getReplyStatus()))
                            .map(replyList -> modelMapper.map(replyList, ReplyDTO.class))
                            .collect(Collectors.toList());

                    post.setReplies(replyDTOList);

                    return modelMapper.map(post, PostDTO.class);
                })
                .collect(Collectors.toList());
            }

            return list;
        } catch (Exception e) {
            log.info("User Like Post : { }", e);
        }

        return Collections.emptyList();
    }
}
