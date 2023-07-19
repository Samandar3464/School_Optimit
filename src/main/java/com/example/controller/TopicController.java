package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.TopicRequestDto;
import com.example.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic/")
public class TopicController {

    private final TopicService topicService;

    @PostMapping("create")
    public ApiResponse save(@ModelAttribute TopicRequestDto topicRequestDto){
        return topicService.create(topicRequestDto);
    }

    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable UUID id){
        return topicService.getById(id);
    }

    @PutMapping("update")
    public ApiResponse update(@ModelAttribute TopicRequestDto topicRequestDto){
        return topicService.update(topicRequestDto);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable UUID id){
        return topicService.delete(id);
    }
}
