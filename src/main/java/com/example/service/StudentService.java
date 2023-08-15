package com.example.service;

import com.example.entity.*;
import com.example.exception.*;
import com.example.model.common.ApiResponse;
import com.example.model.request.FamilyLoginDto;
import com.example.model.request.StudentRequest;
import com.example.model.response.StudentClassResponse;
import com.example.model.response.StudentResponse;
import com.example.model.response.StudentResponseListForAdmin;
import com.example.repository.BranchRepository;
import com.example.repository.JournalRepository;
import com.example.repository.StudentClassRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentService implements BaseService<StudentRequest, Integer> {

    private final StudentRepository studentRepository;
    private final AttachmentService attachmentService;
    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final JournalRepository journalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = {FileNotFoundException.class, UserNotFoundException.class, FileInputException.class, OriginalFileNameNullException.class, InputException.class, RecordNotFoundException.class})
    public ApiResponse create(StudentRequest studentRequest) {
        if (studentRepository.findByPhoneNumberAndPassword(studentRequest.getPhoneNumber(), studentRequest.getPassword()).isPresent()) {
            throw new RecordNotFoundException(PHONE_NUMBER_ALREADY_REGISTERED);
        }
        Student student = modelMapper.map(studentRequest, Student.class);
        setStudent(studentRequest, student);
        studentRepository.save(student);
        StudentResponse studentResponse = getStudentResponse(student);
        return new ApiResponse(SUCCESSFULLY, true, studentResponse);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        Student student = studentRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        return new ApiResponse(SUCCESSFULLY, true, getStudentResponse(student));
    }


    @Override
    @Transactional(rollbackFor = {FileNotFoundException.class, UserNotFoundException.class, FileInputException.class, OriginalFileNameNullException.class, InputException.class, RecordNotFoundException.class})
    public ApiResponse update(StudentRequest studentRequest) {
        Student old = studentRepository.findByIdAndActiveTrue(studentRequest.getId())
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        deletePhotos(old);
        Student student = modelMapper.map(studentRequest, Student.class);
        setStudent(studentRequest, student);
        studentRepository.save(student);
        StudentResponse response = getStudentResponse(student);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }


    @Override
    public ApiResponse delete(Integer integer) {
        Student student = studentRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        student.setActive(false);
        studentRepository.save(student);
        StudentResponse response = getStudentResponse(student);
        return new ApiResponse(DELETED, true, response);
    }

    public ApiResponse getList(int page, int size, int branchId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAllByBranchIdAndActiveTrue(pageable, branchId);
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(getStudentResponse(student)));

        StudentResponseListForAdmin admin = new StudentResponseListForAdmin(
                studentResponseList,
                students.getTotalElements(),
                students.getTotalPages(),
                students.getNumber());
        return new ApiResponse(admin, true);
    }

    private StudentResponse getStudentResponse(Student student) {
        StudentResponse studentResponse = modelMapper.map(student, StudentResponse.class);
        StudentClassResponse classResponse = modelMapper.map(student.getStudentClass(), StudentClassResponse.class);

        studentResponse.setPhoto(student.getPhoto() == null ? null
                : attachmentService.getUrl(student.getPhoto()));
        studentResponse.setDocPhoto(student.getDocPhoto() == null ? null
                : attachmentService.getUrlList(student.getDocPhoto()));
        studentResponse.setMedDocPhoto(student.getMedDocPhoto() == null ? null
                : attachmentService.getUrl(student.getMedDocPhoto()));
        studentResponse.setReference(student.getReference() == null ? null
                : attachmentService.getUrl(student.getReference()));

        studentResponse.setAddedTime(student.getAddedTime().toString());
        studentResponse.setBirthDate(student.getBirthDate().toString());
        studentResponse.setStudentClass(classResponse);
        return studentResponse;
    }

    public ApiResponse getListByClassNumber(Integer classId, int branchId) {
        List<Student> students = studentRepository.findAllByStudentClassIdAndBranchIdAndActiveTrue(classId, branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(getStudentResponse(student)));
        return new ApiResponse(studentResponseList, true);
    }

    public ApiResponse getAllNeActiveStudents(int branchId) {
        List<Student> neActiveStudents = studentRepository.findAllByBranchIdAndActiveFalseOrderByAddedTimeAsc(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentResponse> studentResponseList = new ArrayList<>();
        neActiveStudents.forEach(student -> studentResponseList.add(getStudentResponse(student)));
        return new ApiResponse(studentResponseList, true);
    }

    public ApiResponse studentLogIn(FamilyLoginDto dto) {
        Student student = studentRepository.findByPhoneNumberAndPassword(dto.getPhoneNumber(), dto.getPassword()).orElseThrow(() -> new RecordNotFoundException(STUDENT_NOT_FOUND));
        Journal journal = journalRepository.findByStudentClassIdAndActiveTrue(student.getStudentClass().getId()).orElseThrow(() -> new RecordNotFoundException(JOURNAL_NOT_FOUND));
        return new ApiResponse(SUCCESSFULLY, true, getStudentResponse(student));
    }

    private void deletePhotos(Student student) {
        attachmentService.deleteNewName(student.getPhoto());
        attachmentService.deleteNewName(student.getReference());
        attachmentService.deleteNewName(student.getMedDocPhoto());
        attachmentService.deleteListFilesByNewName(student.getDocPhoto());
    }

    private void setStudent(StudentRequest studentRequest, Student student) {
        Branch branch = branchRepository.findById(studentRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        StudentClass studentClass = studentClassRepository.findById(studentRequest.getStudentClassId())
                .orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));

        Attachment photo = attachmentService.saveToSystem(studentRequest.getPhoto());
        Attachment reference = attachmentService.saveToSystem(studentRequest.getReference());
        Attachment medDocPhoto = attachmentService.saveToSystem(studentRequest.getMedDocPhoto());
        List<Attachment> documentPhotos = attachmentService.saveToSystemListFile(studentRequest.getDocPhoto());

        student.setActive(true);
        student.setAddedTime(LocalDateTime.now());
        student.setBranch(branch);
        student.setStudentClass(studentClass);
        student.setPhoto(photo);
        student.setReference(reference);
        student.setMedDocPhoto(medDocPhoto);
        student.setDocPhoto(documentPhotos);
    }
}
