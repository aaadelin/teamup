package com.team.teamup.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO {

    private int id;
    private String title;
    private String content;
    private UserDTO creator;
    private int parent;
    private LocalDateTime datePosted;
    private int postId;
    private List<CommentDTO> replies = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creator=" + creator +
                ", datePosted=" + datePosted +
                ", replies=" + replies +
                '}';
    }
}
