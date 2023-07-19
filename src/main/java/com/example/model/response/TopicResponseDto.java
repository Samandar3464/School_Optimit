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
public class TopicResponseDto {

    private UUID id;

    private String name;

    private List<String> lessonFiles;

    private  String[] useFullLinks;


    public static TopicResponseDto from(Topic topic, List<String> lessonFiles, String[] useFullLinks) {
        return TopicResponseDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonFiles(lessonFiles)
                .useFullLinks(useFullLinks)
                .build();
    }
}
