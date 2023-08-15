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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = {FileNotFoundException.class, UserNotFoundException.class, FileInputException.class, OriginalFileNameNullException.class, InputException.class, RecordNotFoundException.class})
    public ApiResponse create(StudentRequest studentRequest) {
        if (studentRepository.findByPhoneNumberAndPassword(studentRequest.getPhoneNumber(), studentRequest.getPassword()).isPresent()) {
            throw new RecordNotFoundException(PHONE_NUMBER_ALREADY_REGISTERED);
        }
        Branch branch = branchRepository.findById(studentRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Student save = studentRepository.save(toEntity(studentRequest, branch));
        return new ApiResponse(toEntity(save), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Student student = studentRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        return new ApiResponse(getStudentResponse(student), true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {FileNotFoundException.class, UserNotFoundException.class, FileInputException.class, OriginalFileNameNullException.class, InputException.class, RecordNotFoundException.class})
    public ApiResponse update(StudentRequest studentRequest) {
        Student student = studentRepository.findByIdAndActiveTrue(studentRequest.getId()).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        studentRepository.save(update(studentRequest, student));
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Student student = studentRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        student.setActive(false);
        studentRepository.save(student);
        return new ApiResponse(DELETED, true, getStudentResponse(student));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList(int page, int size, int branchId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAllByBranchIdAndActiveTrue(pageable, branchId);
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(getStudentResponse(student)));
        return new ApiResponse(new StudentResponseListForAdmin(studentResponseList, students.getTotalElements(), students.getTotalPages(), students.getNumber()), true);
    }

    private StudentResponse getStudentResponse(Student student) {
        StudentResponse studentResponse = modelMapper.map(student, StudentResponse.class);
        studentResponse.setStudentClass(modelMapper.map(student.getStudentClass(), StudentClassResponse.class));
        studentResponse.setPhoto(student.getPhoto() == null ? null : attachmentService.getUrl(student.getPhoto()));
        studentResponse.setDocPhoto(student.getDocPhoto() == null ? null : attachmentService.getUrlList(student.getDocPhoto()));
        studentResponse.setMedDocPhoto(student.getMedDocPhoto() == null ? null : attachmentService.getUrl(student.getMedDocPhoto()));
        studentResponse.setReference(student.getReference() == null ? null : attachmentService.getUrl(student.getReference()));
        return studentResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getListByClassNumber(Integer classId, int branchId) {
        List<Student> students = studentRepository.findAllByStudentClassIdAndBranchIdAndActiveTrue(classId, branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(getStudentResponse(student)));
        return new ApiResponse(studentResponseList, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllNeActiveStudents(int branchId) {
        List<Student> neActiveStudents = studentRepository.findAllByBranchIdAndActiveFalseOrderByAddedTimeAsc(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentResponse> studentResponseList = new ArrayList<>();
        neActiveStudents.forEach(student -> studentResponseList.add(getStudentResponse(student)));
        return new ApiResponse(studentResponseList, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse studentLogIn(FamilyLoginDto dto) {
        Student student = studentRepository.findByPhoneNumberAndPassword(dto.getPhoneNumber(), dto.getPassword()).orElseThrow(() -> new RecordNotFoundException(STUDENT_NOT_FOUND));
        Journal journal = journalRepository.findByStudentClassIdAndActiveTrue(student.getStudentClass().getId()).orElseThrow(() -> new RecordNotFoundException(JOURNAL_NOT_FOUND));
        return new ApiResponse(SUCCESSFULLY, true,getStudentResponse(student));
    }

    private Student toEntity(StudentRequest student, Branch branch) {
        Student from = Student.from(student);
        from.setStudentClass(studentClassRepository.findById(student.getStudentClassId())
                .orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND)));
        from.setPhoto(attachmentService.saveToSystem(student.getPhoto()));
        from.setReference(attachmentService.saveToSystem(student.getReference()));
        from.setMedDocPhoto(attachmentService.saveToSystem(student.getMedDocPhoto()));
        from.setDocPhoto(attachmentService.saveToSystemListFile(student.getDocPhoto()));
        from.setBranch(branch);
        return from;
    }

    private StudentResponse toEntity(Student student) {
        StudentResponse response = modelMapper.map(student, StudentResponse.class);
        response.setStudentClass(modelMapper.map(student.getStudentClass(), StudentClassResponse.class));
        response.setBirthDate(student.getBirthDate().toString());
        List<String> docPhotoList = new ArrayList<>();
        student.getDocPhoto().forEach(obj -> docPhotoList.add(attachmentService.getUrl(obj)));
        response.setPhoto(attachmentService.getUrl(student.getPhoto()));
        response.setReference(attachmentService.getUrl(student.getReference()));
        response.setMedDocPhoto(attachmentService.getUrl(student.getMedDocPhoto()));
        response.setDocPhoto(docPhotoList);
        return response;
    }

    private Student update(StudentRequest studentRequest, Student student) {
        attachmentService.deleteNewName(student.getPhoto());
        attachmentService.deleteNewName(student.getReference());
        attachmentService.deleteNewName(student.getMedDocPhoto());
        attachmentService.deleteListFilesByNewName(student.getDocPhoto());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setPassword(studentRequest.getPassword());
        student.setMedDocPhoto(attachmentService.saveToSystem(studentRequest.getMedDocPhoto()));
        student.setPhoto(attachmentService.saveToSystem(studentRequest.getPhoto()));
        student.setDocPhoto(attachmentService.saveToSystemListFile(studentRequest.getDocPhoto()));
        student.setReference(attachmentService.saveToSystem(studentRequest.getReference()));
        student.setStudentClass(studentClassRepository.findById(studentRequest.getStudentClassId()).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND)));
        student.setBranch(branchRepository.findById(studentRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setFatherName(studentRequest.getFatherName());
        student.setBirthDate(studentRequest.getBirthDate());
        student.setPaymentAmount(student.getPaymentAmount());
        student.setDocNumber(studentRequest.getDocNumber());
        student.setActive(studentRequest.isActive());
        return student;
    }
}
