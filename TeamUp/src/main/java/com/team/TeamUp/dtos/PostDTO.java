package com.team.TeamUp.dtos;

import com.team.TeamUp.domain.Comment;

import java.util.List;

public class PostDTO {

    private int id;
    private TaskDTO taskDTO;
    private List<CommentDTO> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskDTO getTaskDTO() {
        return taskDTO;
    }

    public void setTaskDTO(TaskDTO taskDTO) {
        this.taskDTO = taskDTO;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", taskDTO=" + taskDTO +
                ", comments=" + comments +
                '}';
    }
}
