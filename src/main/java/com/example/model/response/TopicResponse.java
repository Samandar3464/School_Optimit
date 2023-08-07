package com.example.model.response;

import com.example.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponse {

    private Integer id;

    private String name;

    private List<String> lessonFiles;

    private  List<String> useFullLinks;


    public static TopicResponse from(Topic topic, List<String> lessonFiles) {
        return TopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonFiles(lessonFiles)
                .useFullLinks(topic.getUseFullLinks())
                .build();
    }
}
