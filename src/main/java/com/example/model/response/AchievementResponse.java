package com.example.model.response;

import com.example.entity.Achievement;
import com.example.repository.AttachmentRepository;
import com.example.service.AttachmentService;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementResponse {

    private Integer id;

    private String name;

    private String userName;

    private String aboutAchievement;

    private String photoCertificate;

    private Integer userId;


    public static AchievementResponse toResponse(Achievement achievement) {
        return AchievementResponse
                .builder()
                .id(achievement.getId())
                .userId(achievement.getUser().getId())
                .name(achievement.getName())
                .userName(achievement.getUser().getName() + " " + achievement.getUser().getSurname())
                .aboutAchievement(achievement.getAboutAchievement())
                .build();
    }
}
