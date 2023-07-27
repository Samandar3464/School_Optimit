package com.example.service;

import com.example.entity.Achievement;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.AchievementDto;
import com.example.model.response.AchievementResponse;
import com.example.repository.AchievementRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        if (achievementRepository.findByName(achievementDto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ACHIEVEMENT_ALREADY_EXIST);
        }
        Achievement achievement = Achievement.toAchievement(achievementDto);
        achievement.setUser(userRepository.findById(achievementDto.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        if (achievementDto.getPhotoCertificate()!=null) {
            achievement.setPhotoCertificate(attachmentService.saveToSystem(achievementDto.getPhotoCertificate()));
        }
        achievementRepository.save(achievement);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Achievement achievement = checkById(integer);
        AchievementResponse response = AchievementResponse.from(achievement);
        response.setPhotoCertificate(achievement.getPhotoCertificate() == null ? null : attachmentService.getUrl(achievement.getPhotoCertificate()));
        return new ApiResponse(response, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(AchievementDto dto) {
        Achievement achievement1 = checkById(dto.getId());
        if (!dto.getName().equals(achievement1.getName()) && achievementRepository.findByName(dto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ACHIEVEMENT_ALREADY_EXIST);
        }
        Achievement achievement = Achievement.toAchievement(dto);
        achievement.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        if (dto.getPhotoCertificate()!=null) {
            if (achievement1.getPhotoCertificate() != null) {
                attachmentService.deleteNewName(achievement1.getPhotoCertificate());
            }
            achievement.setPhotoCertificate(attachmentService.saveToSystem(dto.getPhotoCertificate()));
        }
        achievement.setId(dto.getId());
        achievementRepository.save(achievement);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Achievement achievement = checkById(integer);
        if (achievement.getAboutAchievement() != null) {
            attachmentService.deleteNewName(achievement.getPhotoCertificate());
        }
        achievementRepository.deleteById(integer);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByUserId(Integer userId) {
        List<AchievementResponse> achievementResponses = new ArrayList<>();
        achievementRepository.findAllByUserId(userId).forEach(achievement -> {
            AchievementResponse response = AchievementResponse.from(achievement);
            response.setPhotoCertificate(achievement.getPhotoCertificate() == null ? null : attachmentService.getUrl(achievement.getPhotoCertificate()));
            achievementResponses.add(response);
        });
        return new ApiResponse(achievementResponses, true);
    }

    private Achievement checkById(Integer integer) {
        return achievementRepository.findById(integer)
                .orElseThrow(() -> new RecordNotFoundException(ACHIEVEMENT_NOT_FOUND));
    }


}
