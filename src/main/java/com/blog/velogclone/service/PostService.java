package com.blog.velogclone.service;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public List<PostDTO> findAll(int page, int pageSize) {
        Page<Post> postList = postRepository.findAll(PageRequest.of(page, pageSize));
        return postList.stream().map(post -> modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
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

    public List<PostDTO> selectUserPost(Long userNo) {
        List<Post> findPost = postRepository.findByUserUserNoAndPostStatus(userNo, "N");

        if(!findPost.isEmpty()) {
            return findPost.stream().map(list -> modelMapper.map(list, PostDTO.class)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional
    public Map<String, Object> findByUserUserNoAndPostTag(Long userNo, String postTag) {

        List<Post> findPostList = postRepository.findByUserUserNoAndPostTagAndPostStatus(userNo, postTag, "N");

        Map<String, Long> tagCountMap = new HashMap<>();
        Map<String, Object> response = new HashMap<>();

        if(!findPostList.isEmpty()) {
            List<PostDTO> postList =
                findPostList.stream().map(list -> modelMapper.map(list, PostDTO.class)).collect(Collectors.toList());

            response.put("list", postList);
        }

        return response;
    }
}
