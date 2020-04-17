package com.team.teamup.service;

import com.team.teamup.domain.Post;
import com.team.teamup.domain.Task;
import com.team.teamup.dtos.PostDTO;
import com.team.teamup.persistence.PostRepository;
import com.team.teamup.utils.DTOsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PostService {

    private PostRepository postRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public PostService(PostRepository postRepository,
                       DTOsConverter dtOsConverter){
        this.postRepository = postRepository;
        this.dtOsConverter = dtOsConverter;
    }


    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public List<PostDTO> getAllDTOS(){
        return getAll().stream().map(dtOsConverter::getDTOFromPost).collect(Collectors.toList());
    }

    public Post getByID(int postId) {
        return postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
    }

    public PostDTO getDTOByID(int id){
        return dtOsConverter.getDTOFromPost(getByID(id));
    }

    public Optional<Post> getByTask(Task task) {
        return postRepository.findByTask(task);
    }

    public Post save(Post post){
        return postRepository.save(post);
    }
}
