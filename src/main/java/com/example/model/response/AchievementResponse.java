package com.example.model.response;

import com.example.entity.Achievement;
import com.example.entity.Attachment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementResponse {

    private Integer id;

    private String name;

    private String aboutAchievement;

    private String photoCertificate;

    private Integer userId;

    private String createdDate;

    public static AchievementResponse from(Achievement achievement) {
        return AchievementResponse
                .builder()
                .id(achievement.getId())
                .userId(achievement.getUser().getId())
                .name(achievement.getName())
                .aboutAchievement(achievement.getAboutAchievement())
                .createdDate(achievement.getCreatedDate().toString())
                .build();
    }
}
