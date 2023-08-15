package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.ScoreDto;
import com.example.model.request.ScoreRequest;
import com.example.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/score")
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ScoreRequest scoreRequest) {
        return scoreService.create(scoreRequest);
    }

//    @GetMapping("/findByIdAndDeleteFalse/{id}")
//    public ApiResponse findByIdAndDeleteFalse(@PathVariable Integer id) {
//        return scoreService.findByIdAndDeleteFalse(id);
//    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody @Validated ScoreRequest scoreRequest) {
        return scoreService.update(scoreRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return scoreService.delete(id);
    }

}
