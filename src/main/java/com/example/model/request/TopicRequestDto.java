package com.example.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequestDto {

    private UUID id;

    private String name;

    private List<MultipartFile> lessonFiles;

    private List<String> useFullLinks;

    private Integer subjectId;
}
