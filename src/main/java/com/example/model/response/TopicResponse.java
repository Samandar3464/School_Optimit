package com.example.model.response;

import com.example.entity.SubjectLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponse {

    private Integer id;

    private String name;

    private SubjectLevel subjectLevel;

<<<<<<< HEAD
    private List<Integer> lessonFilesId;
=======
    private List<String> lessonFiles;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private  List<String> useFullLinks;
}
