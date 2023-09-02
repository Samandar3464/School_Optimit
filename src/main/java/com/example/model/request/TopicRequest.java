package com.example.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequest {

    private Integer id;

    private String name;

<<<<<<< HEAD
    private List<Integer> lessonFilesIds;
=======
    private List<MultipartFile> lessonFiles;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private List<String> useFullLinks;

    private Integer subjectLevelId;
}
