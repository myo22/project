package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "assignmentfile")
public class AssignmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignmentfile_id")
    private Integer assignmentFileId;

    @Column(name = "orig_filename")
    private String origFilename;

    private Integer score;

    @Column(name = "assignment_name")
    private String assignmentName;

    @Column(name = "assignment_path")
    private String assignmentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Builder
    public AssignmentFile(int assignmentFileId, String origFilename ,String assignmentName, int score, String assignmentPath, User user, Assignment assignment){
        this.assignmentFileId = assignmentFileId;
        this.assignmentName = assignmentName;
        this.assignmentPath = assignmentPath;
        this.origFilename = origFilename;
        this.score = score;
        this.user = user;
        this.assignment = assignment;
    }
}
