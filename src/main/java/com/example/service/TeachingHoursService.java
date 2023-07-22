package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TeachingHoursService implements BaseService<TeachingHoursRequest, UUID> {

    private final TeachingHoursRepository teachingHoursRepository;
    //    private final TypeOfWorkRepository typeOfWorkRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;
    private final UserRepository userRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(TeachingHoursRequest dto) {
//        TypeOfWork typeOfWork = typeOfWorkRepository.findById(dto.getTypeOfWorkId()).orElseThrow(() -> new RecordNotFoundException(TYPE_OF_WORK_NOT_FOUND));
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        User teacher = userRepository.findById(dto.getTeacherId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        StudentClass studentClass = studentClassRepository.findById(dto.getClassId()).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        TeachingHours teachingHours = TeachingHours
                .builder()
//                .typeOfWork(typeOfWork)
                .subject(subject)
                .teacher(teacher)
                .studentClass(studentClass)
                .lessonHours(dto.getLessonHours())
                .passedDate(dto.getPassedDate())
                .active(true)
                .build();
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(UUID id) {
        return new ApiResponse(checkById(id), true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(TeachingHoursRequest dto) {
        TeachingHours teachingHours = checkById(dto.getId());
        teachingHours.setLessonHours(dto.getLessonHours());
        teachingHours.setPassedDate(dto.getPassedDate());
        if (dto.getTeacherId() != null) {
            User teacher = userRepository.findById(dto.getTeacherId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
            teachingHours.setTeacher(teacher);
        }
//        if (dto.getTypeOfWorkId() != null) {
//            TypeOfWork typeOfWork = typeOfWorkRepository.findById(dto.getTypeOfWorkId()).orElseThrow(() -> new RecordNotFoundException(TYPE_OF_WORK_NOT_FOUND));
//            teachingHours.setTypeOfWork(typeOfWork);
//        }
        if (dto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
            teachingHours.setSubject(subject);
        }
        if (dto.getClassId() != null) {
            StudentClass studentClass = studentClassRepository.findById(dto.getClassId()).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
            teachingHours.setStudentClass(studentClass);
        }
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(UUID id) {
        TeachingHours teachingHours = checkById(id);
        teachingHours.setActive(false);
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByTeacherId(Integer teacherId, LocalDate startDate, LocalDate endDate) {
        List<TeachingHours> allByTeacherId = teachingHoursRepository.findAllByTeacherIdAndActiveTrueAndPassedDateBetween(teacherId, startDate, endDate);
        return new ApiResponse(allByTeacherId, true);
    }

    private TeachingHours checkById(UUID integer) {
        return teachingHoursRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
    }


}
