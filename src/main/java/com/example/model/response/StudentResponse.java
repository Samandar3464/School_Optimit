package com.example.model.response;

<<<<<<< HEAD
import com.example.entity.Branch;
=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
import com.example.entity.Family;
import lombok.Data;

import java.util.List;

@Data
public class StudentResponse {

    private Integer id;

    private String firstName;

    private String lastName;

    private String fatherName;

    private String phoneNumber;

    private double paymentAmount;

    private String birthDate;

    private String docNumber;

<<<<<<< HEAD
    private List<Integer> docPhotoIds;

    private Integer referenceId;

    private Integer photoId;
=======
    private List<String> docPhoto;

    private String reference;

    private String photo;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private StudentClassResponse studentClass;

    private boolean active;

<<<<<<< HEAD
    private Branch branch;

=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
    private List<Family> families;

    private String addedTime;

<<<<<<< HEAD
    private Integer medDocPhotoId;
=======
    private String medDocPhoto;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
}
