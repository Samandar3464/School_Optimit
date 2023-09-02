package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.exception.*;
import com.example.mappers.StudentMapper;
import com.example.model.common.ApiResponse;
import com.example.model.request.FamilyLoginDto;
import com.example.model.request.StudentRequest;
import com.example.model.response.StudentClassResponse;
import com.example.model.response.StudentResponse;
import com.example.model.response.StudentResponseListForAdmin;
<<<<<<< HEAD
import com.example.repository.*;
=======
import com.example.repository.BranchRepository;
import com.example.repository.JournalRepository;
import com.example.repository.StudentClassRepository;
import com.example.repository.StudentRepository;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
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
<<<<<<< HEAD
import java.util.*;
=======
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentService implements BaseService<StudentRequest, Integer> {

    private final StudentRepository studentRepository;
<<<<<<< HEAD
    private final AttachmentRepository attachmentRepository;
=======
    private final AttachmentService attachmentService;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final JournalRepository journalRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse create(StudentRequest studentRequest) {
        Optional<Student> byPhoneNumberAndPassword = studentRepository.findByPhoneNumberAndPassword(studentRequest.getPhoneNumber(), studentRequest.getPassword());
        if (byPhoneNumberAndPassword.isPresent()) {
            throw new RecordAlreadyExistException(PHONE_NUMBER_ALREADY_REGISTERED);
        }
        Branch branch = branchRepository.findByIdAndDeleteFalse(studentRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        StudentClass studentClass = studentClassRepository.findByIdAndActiveTrue(studentRequest.getStudentClassId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_CLASS_NOT_FOUND));

        Student student = StudentMapper.toEntity(studentRequest, studentClass, branch);
<<<<<<< HEAD
        savePhotosIfExists(studentRequest, student, false);
=======
        savePhotosIfExists(studentRequest,student);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
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

        Branch branch = branchRepository.findById(studentRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));

        StudentClass studentClass = studentClassRepository.findById(studentRequest.getStudentClassId())
                .orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));

<<<<<<< HEAD
=======
        Student student = StudentMapper.toEntity(studentRequest,studentClass,branch);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

        Student old = studentRepository.findByIdAndActiveTrue(studentRequest.getId())
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));

<<<<<<< HEAD
        Student student = StudentMapper.update(studentRequest, studentClass, branch, old);
        student.setAccountNumber(old.getAccountNumber());
        student.setId(old.getId());
        savePhotosIfExists(studentRequest, student, true);
=======
        student.setAccountNumber(old.getAccountNumber());
        student.setId(old.getId());
        deletePhotosIfExists(old);
        savePhotosIfExists(studentRequest, student);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        studentRepository.save(student);
        StudentResponse response = getStudentResponse(student);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

<<<<<<< HEAD
    private void savePhotosIfExists(StudentRequest studentRequest, Student student, boolean isUpdate) {
        if (studentRequest.getPhotoId() != null) {
            if (student.getPhoto() != null && isUpdate)
                attachmentRepository.delete(student.getPhoto());
//                attachmentService.deleteNewName(student.getPhoto());
//            Attachment photo = attachmentService.saveToSystem(studentRequest.getPhoto());
            attachmentRepository.findAllById(studentRequest.getPhotoId()).ifPresent(student::setPhoto);
        }

//        if (studentRequest.getReferenceId() != null) {
//            if (student.getReference() != null && isUpdate)
//                attachmentRepository.delete(student.getReference());
////                attachmentService.deleteNewName(student.getReference());
////            Attachment photo = attachmentService.saveToSystem(studentRequest.getReference());
////            student.setReference(photo);
//            attachmentRepository.findById(studentRequest.getReferenceId()).ifPresent(student::setReference);
//        }

        if (studentRequest.getMedDocPhotoId() != null) {
            if (student.getMedDocPhoto() != null && isUpdate)
                attachmentRepository.delete(student.getMedDocPhoto());
               /* student.getDocPhoto().forEach(attachmentService::deleteNewName);
            Attachment photo = attachmentService.saveToSystem(studentRequest.getMedDocPhoto());
            student.setMedDocPhoto(photo);*/
            attachmentRepository.findById(studentRequest.getMedDocPhotoId()).ifPresent(student::setMedDocPhoto);
        }

        if (studentRequest.getDocPhotoIds() != null) {
            if (student.getMedDocPhoto() != null && isUpdate)
                attachmentRepository.deleteAll(student.getDocPhoto());
              /*  attachmentService.deleteNewName(student.getMedDocPhoto());
            List<Attachment> attachments = attachmentService.saveToSystemListFile(studentRequest.getDocPhoto());
            student.setDocPhoto(attachments);*/
            student.setDocPhoto(attachmentRepository.findAllById(studentRequest.getDocPhotoIds()));
        }
    }

=======
    private void savePhotosIfExists(StudentRequest studentRequest, Student student) {
        if (studentRequest.getPhoto() != null) {
            Attachment photo = attachmentService.saveToSystem(studentRequest.getPhoto());
            student.setPhoto(photo);
        }

        if (studentRequest.getReference() != null) {
            Attachment photo = attachmentService.saveToSystem(studentRequest.getReference());
            student.setReference(photo);
        }

        if (studentRequest.getMedDocPhoto() != null) {
            Attachment photo = attachmentService.saveToSystem(studentRequest.getMedDocPhoto());
            student.setMedDocPhoto(photo);
        }

        if (studentRequest.getDocPhoto() != null) {
            List<Attachment> attachments = attachmentService.saveToSystemListFile(studentRequest.getDocPhoto());
            student.setDocPhoto(attachments);
        }
    }

    private void deletePhotosIfExists(Student old) {
        if (old.getPhoto() != null) {
            attachmentService.deleteNewName(old.getPhoto());
        }
        if (old.getReference() != null) {
            attachmentService.deleteNewName(old.getReference());
        }
        if (old.getDocPhoto() != null) {
            old.getDocPhoto().forEach(attachmentService::deleteNewName);
        }
        if (old.getMedDocPhoto() != null) {
            attachmentService.deleteNewName(old.getMedDocPhoto());
        }
    }


>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
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
<<<<<<< HEAD
        studentResponseList.sort(Comparator.comparing(StudentResponse::getLastName).reversed());
=======

>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
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

<<<<<<< HEAD
        studentResponse.setPhotoId(student.getPhoto() == null ? null
                : student.getPhoto().getId());

        if (!student.getDocPhoto().isEmpty()) {
            List<Integer> ids = new ArrayList<>();
            for (Attachment attachment : student.getDocPhoto()) {
                ids.add(attachment.getId());
            }
            studentResponse.setDocPhotoIds(ids);
        }

        studentResponse.setMedDocPhotoId(student.getMedDocPhoto() == null ? null
                : student.getMedDocPhoto().getId());

//        studentResponse.setReferenceId(student.getReference() == null ? null
//                : student.getReference().getId());
=======
        studentResponse.setPhoto(student.getPhoto() == null ? null
                : attachmentService.getUrl(student.getPhoto()));

        studentResponse.setDocPhoto(student.getDocPhoto() == null ? null
                : attachmentService.getUrlList(student.getDocPhoto()));

        studentResponse.setMedDocPhoto(student.getMedDocPhoto() == null ? null
                : attachmentService.getUrl(student.getMedDocPhoto()));

        studentResponse.setReference(student.getReference() == null ? null
                : attachmentService.getUrl(student.getReference()));
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

        studentResponse.setAddedTime(student.getAddedTime().toString());
        studentResponse.setBirthDate(student.getBirthDate().toString());
        studentResponse.setStudentClass(classResponse);
<<<<<<< HEAD
        studentResponse.setBranch(student.getBranch());
=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

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
<<<<<<< HEAD

    public ApiResponse search(String name) {
        List<Student> search = studentRepository.findAllByFirstNameContainingIgnoreCaseAndActiveTrue(name);
        search.addAll(studentRepository.findAllByLastNameContainingIgnoreCaseAndActiveTrue(name));
        search.addAll(studentRepository.findAllByDocNumberContainingIgnoreCaseAndActiveTrue(name));

        Set<Student> all = new HashSet<>(search);

        search = new ArrayList<>(all);
        List<StudentResponse> arrayList = new ArrayList<>();
        for (Student student : search) {
            arrayList.add(getStudentResponse(student));
        }
        return new ApiResponse("all", true, arrayList);
    }
=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
}
