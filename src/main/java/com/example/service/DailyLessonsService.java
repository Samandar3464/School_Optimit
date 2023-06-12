package com.example.service;

import com.example.entity.DailyLessons;
import com.example.model.common.ApiResponse;
import com.example.model.request.DailyLessonsDto;
import com.example.repository.DailyLessonsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyLessonsService implements BaseService<DailyLessonsDto,Integer>{

    private final DailyLessonsRepository dailyLessonsRepository;

    @Override
    public ApiResponse create(DailyLessonsDto dailySalaryReport) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DailyLessonsDto dailySalaryReport) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }

    public List<DailyLessons> checkAllById(List<Integer> dailyLessons) {
        return null;
    }
}
