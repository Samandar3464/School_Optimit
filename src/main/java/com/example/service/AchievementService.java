package com.example.service;

import com.example.entity.Achievement;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.AchievementDto;
import com.example.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService implements BaseService<AchievementDto, Integer> {

    private final AchievementRepository achievementRepository;
    private final AttachmentService attachmentService;


    @Override
    public ApiResponse create(AchievementDto achievementDto) {
        Achievement achievement = Achievement.toAchievement(achievementDto);
//        setPhoto(achievementDto, achievement);
        checkIfExist(achievementDto);
        return new ApiResponse(achievementRepository.save(achievement), true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(checkById(integer), true);
    }


    @Override
    public ApiResponse update(AchievementDto achievementDto) {
        checkById(achievementDto.getId());
        Achievement achievement = Achievement.toAchievement(achievementDto);
//        setPhoto(achievementDto,achievement);
        achievement.setId(achievementDto.getId());
        return new ApiResponse(achievementRepository.save(achievement), true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Achievement achievement = checkById(integer);
        achievementRepository.deleteById(integer);
        return new ApiResponse(Constants.DELETED, true, achievement);
    }

    private Achievement checkById(Integer integer) {
        return achievementRepository.findById(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.ACHIEVEMENT_NOT_FOUND));
    }

    private void checkIfExist(AchievementDto achievementDto) {
        if (achievementRepository.findByName(achievementDto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ACHIEVEMENT_ALREADY_EXIST);
        }
    }

    public List<Achievement> saveAll(List<Achievement> achievements) {
        return achievementRepository.saveAll(achievements);
    }

    public List<Achievement> toAllEntity(List<AchievementDto> achievements) {
        List<Achievement> achievementList = new ArrayList<>();
        achievements.forEach(achievementDto -> {
            Achievement achievement = Achievement.toAchievement(achievementDto);
//            setPhoto(achievementDto, achievement);
            achievementList.add(achievement);
        });
        return achievementList;
    }

    private void setPhoto(AchievementDto achievementDto, Achievement achievement) {
        if (!achievementDto.getPhotoCertificate().isEmpty()){
            achievement.setPhotoCertificate(attachmentService.saveToSystem(achievementDto.getPhotoCertificate()));
        }
    }

    public List<Achievement> findAllById(List<Integer> achievements) {
        return achievementRepository.findAllById(achievements);
    }
}
