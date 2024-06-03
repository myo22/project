package com.example.board.dto;

import com.example.board.domain.Progress;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressDTO {

    private int totalVideos;

    private int totalAssignments;

    private int totalDiscussions;

    private int totalNotices;

    private int totalResources;

    public ProgressDTO(Progress progress){
        this.totalVideos = progress.getTotalVideos();
        this.totalAssignments = progress.getTotalAssignments();
        this.totalDiscussions = progress.getTotalDiscussions();
        this.totalNotices = progress.getTotalNotices();
        this.totalResources = progress.getTotalResources();
    }
}
