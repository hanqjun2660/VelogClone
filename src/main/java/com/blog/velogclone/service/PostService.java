package com.blog.velogclone.service;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PostDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    final String DEFAULT_IMG = "/images/clog_text_logo.png";

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public List<PostDTO> findAll(int page, int pageSize) {
        Page<Post> postList = postRepository.findAll(PageRequest.of(page, pageSize));
        List<PostDTO> list;

        if(!postList.isEmpty()) {
            list = postList.stream()
                    .map(post -> {
                        List<ReplyDTO> filteredReplies = post.getReplies().stream()
                                .filter(reply -> "N".equals(reply.getReplyStatus()))
                                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                                .collect(Collectors.toList());

                        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
                        postDTO.setReplies(filteredReplies);

                        return postDTO;
                    })
                    .collect(Collectors.toList());

            return list;
        }

        return Collections.emptyList();
    }

    public PostDTO findByPostNo(int postNo) {
        Post postEntity = postRepository.findByPostNo(postNo);
        return modelMapper.map(postEntity, PostDTO.class);
    }

    @Transactional
    public PostDTO savePost(PostDTO postDTO) {
        User requestUser = User.builder()
                .userNo(postDTO.getUserNo())
                .build();

        Post request;

        if(!ObjectUtils.isEmpty(postDTO.getPostNo())) {
            Post selectPost = postRepository.findByPostNo(Math.toIntExact(postDTO.getPostNo()));

            request = Post.builder()
                    .postNo(postDTO.getPostNo())
                    .postTitle(postDTO.getPostTitle())
                    .postTag(postDTO.getPostTag())
                    .postBody(postDTO.getPostBody())
                    .postStatus(selectPost.getPostStatus())
                    .postLike(selectPost.getPostLike())
                    .createDate(selectPost.getCreateDate())
                    .user(requestUser)
                    .build();
        } else {
            request = Post.builder()
                    .postTitle(postDTO.getPostTitle())
                    .postTag(postDTO.getPostTag())
                    .postBody(postDTO.getPostBody())
                    .user(requestUser)
                    .build();
        }

        Post response =  postRepository.save(request);

        return modelMapper.map(response, PostDTO.class);
    }

    @Transactional
    public PostDTO deletePost(PostDTO postDTO) {
        Post postEntity = postRepository.findByPostNo(Math.toIntExact(postDTO.getPostNo()));

        if(postEntity == null) {
            throw new NullPointerException("게시물이 존재하지 않습니다. postNo: " + postEntity.getPostNo());
        }

        postEntity = Post.builder()
                .postNo(postEntity.getPostNo())
                .postTag(postEntity.getPostTag())
                .postTitle(postEntity.getPostTitle())
                .postBody(postEntity.getPostBody())
                .postLike(postEntity.getPostViews())
                .createDate(postEntity.getCreateDate())
                .user(postEntity.getUser())
                .postStatus("Y")
                .build();

        postRepository.save(postEntity);

        return modelMapper.map(postEntity, PostDTO.class);
    }

    @Transactional
    public List<PostDTO> selectUserPost(Long userNo) {
        List<Post> findPost = postRepository.findByUserUserNoAndPostStatus(userNo, "N");

        if(!findPost.isEmpty()) {
            return findPost.stream().map(list -> modelMapper.map(list, PostDTO.class)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional
    public Map<String, Object> findByUserUserNoAndPostTag(Long userNo, String postTag) {

        List<Post> findPostList;

        if (!postTag.equals("all")) {
            findPostList = postRepository.findByUserUserNoAndPostTagAndPostStatus(userNo, postTag, "N");
        } else {
            findPostList = postRepository.findByUserUserNoAndPostStatus(userNo, "N");
        }

        Map<String, Object> response = new HashMap<>();

        if (!findPostList.isEmpty()) {

            List<PostDTO> postList = findPostList.stream()
                    .map(post -> {
                        List<ReplyDTO> filteredReplies = post.getReplies().stream()
                                .filter(reply -> "N".equals(reply.getReplyStatus()))
                                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                                .collect(Collectors.toList());

                        String likeCount = likeRepository.countByPostPostNo(post.getPostNo());

                        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
                        postDTO.setReplies(filteredReplies);
                        postDTO.setReplyCount(filteredReplies.size());
                        postDTO.setLikeCount(Integer.parseInt(likeCount));

                        return postDTO;
                    })
                    .collect(Collectors.toList());

            response.put("list", postList);
        }

        return response;
    }

    @Transactional
    public List<PostDTO> findByPostTitleAndPostStatus(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        try {
            Page<Post> findList = postRepository.findByPostTitleContainsAndPostStatus(keyword, "N", pageable);
            List<PostDTO> convertList = new ArrayList<>();

            if(!findList.isEmpty()) {
                convertList = findList.stream().map(post -> modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
                log.info("findList Response : {}", convertList);

                convertList.stream().map(post -> {
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

                    return post;
                })
                .collect(Collectors.toList());

                convertList.stream().map(post -> {
                    int likeCount = Integer.parseInt(likeRepository.countByPostPostNo(post.getPostNo()));
                    if(!(likeCount <= 0)) {
                        post.setLikeCount(likeCount);
                    } else {
                        post.setLikeCount(0);
                    }
                    return post;
                })
                .collect(Collectors.toList());

                return convertList;
            }
        } catch (Exception e) {
            log.error("find search post : { }", e);
        }

        return Collections.emptyList();
    }
}
