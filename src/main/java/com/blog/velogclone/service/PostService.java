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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<PostDTO> findByUserUserNoAndPostTag(Long userNo, String postTag) {

        List<Object[]> findPostList = postRepository.findByUserUserNoAndPostTagAndPostStatus(userNo, postTag, "N");

        if(!findPostList.isEmpty()) {
            // 결과를 가공하여 Map에 저장
            Map<String, Integer> tagCountMap = new HashMap<>();
            for (Object[] row : findPostList) {
                String currentPostTag = (String) row[1]; // postTag
                int count = ((Number) row[2]).intValue(); // count(m.postTag)
                tagCountMap.put(currentPostTag, count);
            }

            // 중복된 postTag의 카운트를 설정하여 반환
            return findPostList.stream()
                    .map(row -> {
                        PostDTO postDTO = modelMapper.map(row[0], PostDTO.class); // 게시글 정보
                        // 중복된 postTag의 카운트를 설정
                        postDTO.setPostTagCount(tagCountMap.getOrDefault(postDTO.getPostTag(), 0));
                        return postDTO;
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
