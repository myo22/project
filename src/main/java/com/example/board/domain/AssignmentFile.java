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
    public AssignmentFile(String assignmentName, String origFilename, String assignmentPath, User user, Assignment assignment){
        this.assignmentName = assignmentName;
        this.assignmentPath = assignmentPath;
        this.origFilename = origFilename;
        this.user = user;
        this.assignment = assignment;
    }
}
