package com.example.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementResponse {

    private Integer id;

    private String name;

    private String userName;

    private String aboutAchievement;

<<<<<<< HEAD
    private Integer photoCertificateId;
=======
    private String photoCertificate;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private Integer userId;
}
