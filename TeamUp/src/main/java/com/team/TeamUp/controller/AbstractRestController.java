package com.team.TeamUp.controller;

import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.utils.UserValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public abstract class AbstractRestController {
    protected TeamRepository teamRepository;
    protected UserRepository userRepository;
    protected TaskRepository taskRepository;
    protected ProjectRepository projectRepository;
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;

    protected DTOsConverter dtOsConverter;
    protected UserValidationUtils userValidationUtils;

    @Autowired
    public AbstractRestController(TeamRepository teamRepository, UserRepository userRepository,
                             TaskRepository taskRepository, ProjectRepository projectRepository,
                             CommentRepository commentRepository, PostRepository postRepository,
                             UserValidationUtils userValidationUtils) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userValidationUtils = userValidationUtils;

        this.dtOsConverter = new DTOsConverter(userRepository, teamRepository,
                taskRepository, projectRepository, postRepository, commentRepository);
    }

}
