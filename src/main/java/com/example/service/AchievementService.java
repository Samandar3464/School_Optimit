package com.example.service;

import com.example.entity.Achievement;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.AchievementDto;
import com.example.model.response.AchievementResponse;
import com.example.repository.AchievementRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class AchievementService implements BaseService<AchievementDto, Integer> {

    private final AchievementRepository achievementRepository;
    private final AttachmentService attachmentService;
    private final UserRepository userRepository;


    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(AchievementDto achievementDto) {
        Achievement achievement = Achievement.toAchievement(achievementDto);
        achievement.setUser(userRepository.findById(achievementDto.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        if (achievementDto.getPhotoCertificate() != null) {
            achievement.setPhotoCertificate(attachmentService.saveToSystem(achievementDto.getPhotoCertificate()));
        }
        achievementRepository.save(achievement);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Achievement achievement = checkById(integer);
        AchievementResponse response = AchievementResponse.toResponse(achievement);
        response.setPhotoCertificate(achievement.getPhotoCertificate() == null ? null : attachmentService.getUrl(achievement.getPhotoCertificate()));
        return new ApiResponse(response, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(AchievementDto dto) {
        Achievement oldAchievement = checkById(dto.getId());
        Achievement achievement = Achievement.toAchievement(dto);
        setUpdate(dto, oldAchievement, achievement);
        achievementRepository.save(achievement);
        return new ApiResponse(SUCCESSFULLY, true,AchievementResponse.toResponse(achievement));
    }

    private void setUpdate(AchievementDto dto, Achievement achievement1, Achievement achievement) {
        achievement.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        if (dto.getPhotoCertificate() != null) {
            if (achievement1.getPhotoCertificate() != null) {
                attachmentService.deleteNewName(achievement1.getPhotoCertificate());
            }
            achievement.setPhotoCertificate(attachmentService.saveToSystem(dto.getPhotoCertificate()));
        }
        achievement.setId(dto.getId());
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Achievement achievement = checkById(integer);
        if (achievement.getPhotoCertificate() != null) {
            attachmentService.deleteNewName(achievement.getPhotoCertificate());
        }
        achievementRepository.deleteById(integer);
        return new ApiResponse(DELETED, true, AchievementResponse.toResponse(achievement));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByUserId(Integer userId) {
        List<AchievementResponse> achievementResponses = new ArrayList<>();
        achievementRepository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC,"id")).forEach(achievement -> {
            AchievementResponse response = AchievementResponse.toResponse(achievement);
            response.setPhotoCertificate(achievement.getPhotoCertificate() == null ? null : attachmentService.getUrl(achievement.getPhotoCertificate()));
            achievementResponses.add(response);
        });
        return new ApiResponse(achievementResponses, true);
    }

    private Achievement checkById(Integer integer) {
        return achievementRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(ACHIEVEMENT_NOT_FOUND));
    }
}
