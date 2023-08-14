package com.example.entity;

import com.example.model.request.StudentRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String fatherName;

    private String phoneNumber;  // phoneNumber

    private String password;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String docNumber;

    private boolean active;

    private double paymentAmount;

    private String accountNumber;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    @Column(nullable = false)
    @OneToMany(cascade = CascadeType.ALL)
    private List<Attachment> docPhoto; // guvohnoma yoki pasport rasmi

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment reference;  // ish joyidan siprafka

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment photo;  // 3*4 rasm

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudentClass studentClass;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Family> families;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment medDocPhoto;  // med sprafka rasmi

    public static Student from(StudentRequest studentRequest) {
        return Student.builder()
                .firstName(studentRequest.getFirstName())
                .lastName(studentRequest.getLastName())
                .paymentAmount(studentRequest.getPaymentAmount())
                .fatherName(studentRequest.getFatherName())
                .birthDate(studentRequest.getBirthDate())
                .docNumber(studentRequest.getDocNumber())
                .addedTime(LocalDateTime.now())
                .phoneNumber(studentRequest.getPhoneNumber())
                .password(studentRequest.getPassword())
                .active(true)
                .build();
    }
}
