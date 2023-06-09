package com.example.service;

import com.example.entity.Attachment;
import com.example.entity.Student;
import com.example.entity.StudentClass;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentDto;
import com.example.model.response.StudentResponse;
import com.example.model.response.StudentResponseListForAdmin;
import com.example.repository.StudentClassRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentService implements BaseService<StudentDto, Integer> {

    private final StudentRepository studentRepository;
    private final AttachmentService attachmentService;
    private final StudentClassRepository studentClassRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(StudentDto student) {
        studentRepository.save(from(student));
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Student student = studentRepository.findById(integer).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        return new ApiResponse(from(student), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(StudentDto studentDto) {
        Student student = studentRepository.findById(studentDto.getId()).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        studentRepository.save(update(studentDto, student));
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Student student = studentRepository.findById(integer).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        student.setActive(false);
        studentRepository.save(student);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAll(pageable);
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(StudentResponse.from(student)));
        return new ApiResponse(new StudentResponseListForAdmin(studentResponseList, students.getTotalElements(), students.getTotalPages(), students.getNumber()), true);
    }
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getListByClassNumber(Integer classId) {
        List<Student> students = studentRepository.findAllByStudentClassId(classId);
        List<StudentResponse> studentResponseList = new ArrayList<>();
        students.forEach(student -> studentResponseList.add(StudentResponse.from(student)));
        return new ApiResponse(studentResponseList, true);
    }

    private Student from(StudentDto student) {
        Student from = Student.from(student);
        from.setPhoto(attachmentService.saveToSystem(student.getPhoto()));
        from.setReference(attachmentService.saveToSystem(student.getPhoto()));
//        from.setMedDocPhoto(attachmentService.saveToSystem(student.getMedDocPhoto()));
        from.setDocPhoto(attachmentService.saveToSystemListFile(student.getDocPhoto()));
        from.setStudentClass(studentClassRepository.getById(student.getStudentClassId()));
        return from;
    }

    private StudentResponse from(Student student) {
        StudentResponse from = StudentResponse.from(student);
        Attachment photo = student.getPhoto();
        Attachment reference = student.getReference();
        List<Attachment> docPhoto = student.getDocPhoto();
        List<String> docPhotoList = new ArrayList<>();
        docPhoto.forEach(obj -> docPhotoList.add(attachmentService.attachUploadFolder + obj.getPath() + "/" + obj.getNewName() + "." + obj.getType()));
        from.setPhoto(attachmentService.attachUploadFolder + photo.getPath() + "/" + photo.getNewName() + "." + photo.getType());
        from.setReference(attachmentService.attachUploadFolder + reference.getPath() + "/" + reference.getNewName() + "." + reference.getType());
        from.setDocPhoto(docPhotoList);
        return from;
    }
    private Student update(StudentDto studentDto, Student student) {

       if (studentDto.getPhoto()!=null){
           Attachment photo = attachmentService.saveToSystem(studentDto.getPhoto());
           attachmentService.deleteNewName(student.getPhoto());
           student.setPhoto(photo);
       }
        if (studentDto.getReference()!=null){
            Attachment reference = attachmentService.saveToSystem(studentDto.getReference());
            attachmentService.deleteNewName(student.getReference());
            student.setReference(reference);
        }
        if (!studentDto.getDocPhoto().isEmpty()){
            List<Attachment> docPhotos = attachmentService.saveToSystemListFile(studentDto.getDocPhoto());
            attachmentService.deleteListFilesByNewName(student.getDocPhoto());
            student.setDocPhoto(docPhotos);
        }
        if (!studentDto.getStudentClassId().equals(student.getStudentClass().getId())){
            StudentClass studentClass = studentClassRepository.getById(studentDto.getStudentClassId());
            student.setStudentClass(studentClass);
        }
        return Student.builder()
                .id(student.getId())
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .fatherName(studentDto.getFatherName())
                .birthDate(studentDto.getBirthDate())
                .docNumber(studentDto.getDocNumber())
                .docPhoto(student.getDocPhoto())
                .reference(student.getReference())
                .photo(student.getPhoto())
                .studentClass(student.getStudentClass())
                .active(studentDto.isActive())
                .build();
    }
}
