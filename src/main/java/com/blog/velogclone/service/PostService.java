package com.blog.velogclone.service;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    public List<PostDTO> findAll() {
        List<Post> postList = postRepository.findAll();
        return postList.stream().map(post -> modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
    }
}
