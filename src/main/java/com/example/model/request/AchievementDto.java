package com.example.model.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementDto {

    private Integer id;

    private String name;

    private String aboutAchievement;

<<<<<<< HEAD
    private Integer photoCertificateId;
=======
    private MultipartFile photoCertificate;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private Integer userId;
}
