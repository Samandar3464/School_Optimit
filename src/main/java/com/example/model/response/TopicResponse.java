package com.example.model.response;

import lombok.Data;

import java.util.List;

@Data
public class TopicResponse {

    private Integer id;

    private String name;

    private List<String> lessonFiles;

    private  List<String> useFullLinks;
}
