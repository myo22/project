package com.example.board.domain;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "orig_filename")
    private String origFilename;

    private String filename;


    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder
    public AttachedFile(String origFilename, String filename, String filePath, Course course) {
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.course = course;
    }
}