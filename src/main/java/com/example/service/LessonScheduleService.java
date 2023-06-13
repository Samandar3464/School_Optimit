package com.example.service;

import com.example.entity.*;
import com.example.model.common.ApiResponse;
import com.example.model.request.LessonScheduleDto;
import com.example.model.response.LessonScheduleResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.module.ResolutionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class LessonScheduleService implements BaseService<List<LessonScheduleDto>, Integer> {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final RoomRepository roomRepository;
    private final StudentClassRepository studentClassRepository;
    private final LessonScheduleRepository lessonScheduleRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(List<LessonScheduleDto> scheduleDtoList) {
        List<LessonSchedule> lessonScheduleList = new ArrayList<>();
        Map<LessonScheduleDto, String> errorResponse = new HashMap<>();
        scheduleDtoList.forEach(schd -> {
            Optional<LessonSchedule> teacherBusy = lessonScheduleRepository.findByBranchIdAndTeacherIdAndStartTime(schd.getBranchId(), schd.getTeacherId(), schd.getStartTime());
            Optional<LessonSchedule> studentClassBusy = lessonScheduleRepository.findByBranchIdAndStudentClassIdAndStartTime(schd.getBranchId(), schd.getStudentClassId(), schd.getStartTime());
            Optional<LessonSchedule> roomBusy = lessonScheduleRepository.findByBranchIdAndRoomIdAndStartTime(schd.getBranchId(), schd.getRoomId(), schd.getEndTime());
            if (teacherBusy.isPresent()) {
                errorResponse.put(schd, "teacher busy this time");
                if (studentClassBusy.isPresent()) {
                    errorResponse.put(schd, "teacher and class busy this time");
                }
                if (roomBusy.isPresent()) {
                    errorResponse.put(schd, "teacher , class and room busy this time");
                }
            }
            if (studentClassBusy.isPresent()) {
                errorResponse.put(schd, "class busy this time");
                if (roomBusy.isPresent()) {
                    errorResponse.put(schd, "class and room busy this time");
                }
            }
            if (roomBusy.isPresent()) {
                errorResponse.put(schd, "teacher , class and room busy this time");
            }


            Optional<Branch> branch = branchRepository.findById(schd.getBranchId());
            Optional<User> teacher = userRepository.findById(schd.getTeacherId());
            Optional<Subject> subject = subjectRepository.findById(schd.getSubjectId());
            Optional<Room> room = roomRepository.findById(schd.getRoomId());
            Optional<StudentClass> studentClass = studentClassRepository.findById(schd.getStudentClassId());

            if (branch.isPresent() && teacher.isPresent() && subject.isPresent() && room.isPresent()) {
                LessonSchedule lessonSchedule = LessonSchedule.from(schd);
                lessonSchedule.setBranch(branch.get());
                lessonSchedule.setTeacher(teacher.get());
                lessonSchedule.setSubject(subject.get());
                lessonSchedule.setRoom(room.get());
                lessonSchedule.setStudentClass(studentClass.get());
                lessonScheduleList.add(lessonSchedule);
            }
        });
        if (!errorResponse.isEmpty()) {
            return new ApiResponse(errorResponse, false);
        }
        lessonScheduleRepository.saveAll(lessonScheduleList);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        LessonSchedule lessonSchedule = lessonScheduleRepository.findById(integer)
                .orElseThrow(() -> new ResolutionException(LESSON_SCHEDULE_NOT_FOUND));
        return new ApiResponse(LessonScheduleResponse.from(lessonSchedule), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(List<LessonScheduleDto> scheduleDtoList) {
        return null;
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        LessonSchedule lessonSchedule = lessonScheduleRepository.findById(integer)
                .orElseThrow(() -> new ResolutionException(LESSON_SCHEDULE_NOT_FOUND));
        lessonSchedule.setActive(false);
        lessonScheduleRepository.save(lessonSchedule);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByBranchId(Integer integer) {
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        LocalDateTime startWeek = LocalDateTime.now().minusDays(dayOfWeek);
        LocalDateTime endWeek = startWeek.plusDays(7);
        List<LessonSchedule> lessonScheduleList = lessonScheduleRepository
                .findAllByBranchIdAndStartTimeBetweenAndActiveTrue(integer, startWeek, endWeek);
        List<LessonScheduleResponse> lessonScheduleResponseList = new ArrayList<>();
        lessonScheduleList.forEach(schedule -> lessonScheduleResponseList.add(LessonScheduleResponse.from(schedule)));
        return new ApiResponse(lessonScheduleResponseList, true);
    }
}
