package com.example.model.response;

import com.example.enums.Gender;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyResponse {

    private Integer id;

    private String fullName;

    private String phoneNumber;

    private String password;

    private String fireBaseToken;

    private Gender gender;

    private boolean active;
}
