package com.example.service;

import com.example.entity.TeachingHours;
import com.example.entity.TypeOfWork;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.model.response.TeachingHoursResponse;
import com.example.repository.TeachingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachingHoursService implements BaseService<TeachingHoursRequest, Integer> {

    private final TeachingHoursRepository teachingHoursRepository;
    private final TypeOfWorkService typeOfWorkService;
    private final UserService userService;

    @Override
    public ApiResponse create(TeachingHoursRequest teachingHoursRequest) {
        TeachingHours teachingHours = TeachingHours.toTeachingHours(teachingHoursRequest);
        checkTeacherId(teachingHoursRequest);
        teachingHours.setDate(toLocalDate(teachingHoursRequest.getDate()));
        teachingHours.setTypeOfWork(typeOfWorkService.checkById(teachingHoursRequest.getTypeOfWorkId()));
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse incrementHours(TeachingHoursRequest teachingHoursRequest) {
        TypeOfWork typeOfWork = typeOfWorkService.checkById(teachingHoursRequest.getTypeOfWorkId());
        TeachingHours teachingHours = teachingHoursRepository.findByTeacherIdAndDateAndTypeOfWork(teachingHoursRequest.getTeacherId(), toLocalDate(teachingHoursRequest.getDate()), typeOfWork);
        teachingHours.setLessonHours(teachingHours.getLessonHours() + 1);
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.teachingHoursDTO(teachingHours));
    }

    public ApiResponse decrementHours(TeachingHoursRequest teachingHoursRequest) {
        TypeOfWork typeOfWork = typeOfWorkService.checkById(teachingHoursRequest.getTypeOfWorkId());
        TeachingHours teachingHours = teachingHoursRepository.findByTeacherIdAndDateAndTypeOfWork(teachingHoursRequest.getTeacherId(), toLocalDate(teachingHoursRequest.getDate()), typeOfWork);
        if (teachingHours.getLessonHours() == 0) {
            throw new RecordNotFoundException(Constants.HOURS_NOT_ENOUGH);
        }
        teachingHours.setLessonHours(teachingHours.getLessonHours() - 1);
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.teachingHoursDTO(teachingHours));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(checkById(integer), true);
    }

    public ApiResponse getAll() {
        List<TeachingHoursResponse> all = new ArrayList<>();
        toAllHours(all, teachingHoursRepository.findAll());
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    public ApiResponse getByTeacherId(Integer id) {
        List<TeachingHoursResponse> all = new ArrayList<>();
        toAllHours(all, teachingHoursRepository.findAllByTeacherId(id));
        return new ApiResponse(Constants.FOUND, true, all);
    }

    public ApiResponse getByTeacherIdAndDate(Integer id, String date) {
        List<TeachingHoursResponse> all = new ArrayList<>();
        toAllHours(all, teachingHoursRepository.findAllByTeacherIdAndDate(id, toLocalDate(date)));
        return new ApiResponse(Constants.FOUND, true, all);
    }

    @Override
    public ApiResponse update(TeachingHoursRequest teachingHoursRequest) {
        checkById(teachingHoursRequest.getId());
        TeachingHours teachingHours = TeachingHours.toTeachingHours(teachingHoursRequest);
        checkTeacherId(teachingHoursRequest);
        teachingHours.setDate(toLocalDate(teachingHoursRequest.getDate()));
        teachingHours.setTypeOfWork(typeOfWorkService.checkById(teachingHoursRequest.getTypeOfWorkId()));
        teachingHours.setId(teachingHoursRequest.getId());
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(teachingHours, true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TeachingHoursResponse teachingHoursResponse = checkById(integer);
        teachingHoursRepository.deleteById(integer);
        return new ApiResponse(Constants.DELETED, true, teachingHoursResponse);
    }

    private void toAllHours(List<TeachingHoursResponse> all, List<TeachingHours> teachingHoursResponses) {
        teachingHoursResponses.forEach(teachingHours -> {
            all.add(TeachingHoursResponse.teachingHoursDTO(teachingHours));
        });
    }

    private void checkTeacherId(TeachingHoursRequest teachingHoursRequest) {
        User user = userService.checkUserExistById(teachingHoursRequest.getTeacherId());
        if (user == null) {
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
    }

    private LocalDate toLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    private TeachingHoursResponse checkById(Integer integer) {
        TeachingHours teachingHours = teachingHoursRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        return TeachingHoursResponse.teachingHoursDTO(teachingHours);
    }
}
