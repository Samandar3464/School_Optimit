package com.example.model.response;

import com.example.entity.Attachment;
import com.example.entity.Student;
import com.example.model.request.StudentDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponse {

    private Integer id;

    private String firstName;

    private String lastName;

    private String fatherName;

    private LocalDate birthDate;

    private String docNumber;

    private List<String> docPhoto;

    private String reference;

    private String photo;

    private String studentClass;

    private boolean active;

//    private Attachment medDocPhoto;

    public static StudentResponse from(Student student){
        return StudentResponse.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fatherName(student.getFatherName())
                .birthDate(student.getBirthDate())
                .docNumber(student.getDocNumber())
                .docNumber(student.getDocNumber())
                .studentClass(student.getStudentClass().getClassName())
                .active(student.isActive())
                .build();
    }
}
