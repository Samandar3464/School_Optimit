package com.example.service;

import com.example.entity.StudentHomework;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentHomeworkRequest;
import com.example.model.response.StudentHomeworkResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentHomeworkService implements BaseService<StudentHomeworkRequest, Integer> {

    private final StudentHomeworkRepository studentHomeworkRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;


    @Override
    public ApiResponse create(StudentHomeworkRequest request) {
        checkingStudentHomeworkForExists(request);
        StudentHomework studentHomework = StudentHomework.toEntity(request);
        setStudentHomework(request, studentHomework);
        studentHomeworkRepository.save(studentHomework);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        StudentHomework studentHomework = getStudentHomework(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentHomeworkResponse.toResponse(studentHomework));
    }

    @Override
    public ApiResponse update(StudentHomeworkRequest request) {
        checkingStudentHomeworkForExists(request);
        getStudentHomework(request.getId());
        StudentHomework studentHomework = StudentHomework.toEntity(request);
        studentHomework.setId(request.getId());
        setStudentHomework(request, studentHomework);
        studentHomeworkRepository.save(studentHomework);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentHomeworkResponse.toResponse(studentHomework));
    }

    public ApiResponse getList() {
        List<StudentHomework> all = studentHomeworkRepository.findAll();
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentHomeworkResponse.toAllResponse(all));
    }

    public ApiResponse getListByActive() {
        List<StudentHomework> all = studentHomeworkRepository.findAllByActiveTrue( Sort.by(Sort.Direction.DESC,"id"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentHomeworkResponse.toAllResponse(all));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        StudentHomework studentHomework = getStudentHomework(integer);
        studentHomework.setActive(false);
        studentHomeworkRepository.save(studentHomework);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentHomeworkResponse.toResponse(studentHomework));
    }

    private void setStudentHomework(StudentHomeworkRequest request, StudentHomework studentHomework) {
        studentHomework.setTeacher(userRepository.findByIdAndBlockedFalse(request.getTeacherId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        studentHomework.setStudentClass(studentClassRepository.findByIdAndActiveTrue(request.getStudentClassId()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_CLASS_NOT_FOUND)));
        studentHomework.setSubject(subjectRepository.findByIdAndActiveTrue(request.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
        studentHomework.setBranch(branchRepository.findByIdAndDeleteFalse(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
    }

    private void checkingStudentHomeworkForExists(StudentHomeworkRequest request) {
        if (studentHomeworkRepository.findByDateAndLessonHourAndStudentClassIdAndActiveTrue(request.getDate(), request.getLessonHour(), request.getStudentClassId()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.STUDENT_HOMEWORK_ALREADY_EXISTS);
        }
    }

    private StudentHomework getStudentHomework(Integer integer) {
        return studentHomeworkRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_HOMEWORK_NOT_FOUND));
    }
}
