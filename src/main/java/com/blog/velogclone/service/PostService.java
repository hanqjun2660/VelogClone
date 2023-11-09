package com.blog.velogclone.service;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public List<PostDTO> findAll() {
        List<Post> postList = postRepository.findAllPost();
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

        Post request = Post.builder()
                        .postTitle(postDTO.getPostTitle())
                        .postTag(postDTO.getPostTag())
                        .postBody(postDTO.getPostBody())
                        .user(requestUser)
                        .build();

        Post response =  postRepository.save(request);

        return modelMapper.map(response, PostDTO.class);
    }
}
