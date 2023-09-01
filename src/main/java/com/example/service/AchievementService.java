package com.example.service;

import com.example.entity.Achievement;
import com.example.entity.Attachment;
import com.example.entity.User;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.AchievementDto;
import com.example.model.response.AchievementResponse;
import com.example.model.response.AchievementResponsePage;
import com.example.repository.AchievementRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class AchievementService implements BaseService<AchievementDto, Integer> {

    private final AchievementRepository achievementRepository;
    private final AttachmentService attachmentService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public ApiResponse create(AchievementDto achievementDto) {
        Achievement achievement = modelMapper.map(achievementDto, Achievement.class);
        setAchievement(achievementDto, achievement);
        achievementRepository.save(achievement);
        AchievementResponse response = getAchievementResponse(achievement);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        Achievement achievement = checkById(integer);
        AchievementResponse response = getAchievementResponse(achievement);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }


    @Override
    public ApiResponse update(AchievementDto dto) {
        Achievement oldAchievement = checkById(dto.getId());
        Achievement achievement = modelMapper.map(dto, Achievement.class);
        setUpdate(dto, oldAchievement, achievement);
        achievementRepository.save(achievement);
        AchievementResponse response = getAchievementResponse(achievement);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Achievement achievement = checkById(integer);
        if (achievement.getPhotoCertificate() != null) {
            attachmentService.deleteNewName(achievement.getPhotoCertificate());
        }
        achievementRepository.deleteById(integer);
        AchievementResponse response = getAchievementResponse(achievement);
        return new ApiResponse(DELETED, true, response);
    }

    public ApiResponse getAllByUserId(Integer userId, int page, int size) {
        Page<Achievement> all = achievementRepository
                .findAllByUserId(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        AchievementResponsePage achievementResponsePage = getAchievementResponsePage(all);
        return new ApiResponse(SUCCESSFULLY, true,achievementResponsePage);
    }

    private AchievementResponsePage getAchievementResponsePage(Page<Achievement> all) {
        AchievementResponsePage achievementResponsePage = new AchievementResponsePage();
        List<AchievementResponse> achievementResponses = new ArrayList<>();
        all.forEach(achievement -> {
            AchievementResponse response = getAchievementResponse(achievement);
            achievementResponses.add(response);
        });
        achievementResponsePage.setAchievementResponses(achievementResponses);
        achievementResponsePage.setTotalPage(all.getTotalPages());
        achievementResponsePage.setTotalElement(all.getTotalElements());
        return achievementResponsePage;
    }

    private Achievement checkById(Integer integer) {
        return achievementRepository.findById(integer)
                .orElseThrow(() -> new RecordNotFoundException(ACHIEVEMENT_NOT_FOUND));
    }

    private AchievementResponse getAchievementResponse(Achievement achievement) {
        AchievementResponse response = modelMapper.map(achievement, AchievementResponse.class);
        String url = attachmentService.getUrl(achievement.getPhotoCertificate());
        User user = achievement.getUser();
        response.setUserId(user.getId());
        response.setUserName(user.getName() + user.getSurname());
        response.setPhotoCertificate(url);
        return response;
    }

    private void setUpdate(AchievementDto dto, Achievement oldAchievement, Achievement achievement) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
        if (dto.getPhotoCertificate() != null) {
            if (oldAchievement.getPhotoCertificate() != null) {
                attachmentService.deleteNewName(oldAchievement.getPhotoCertificate());
            }
            Attachment attachment = attachmentService.saveToSystem(dto.getPhotoCertificate());
            achievement.setPhotoCertificate(attachment);
        }
        achievement.setUser(user);
        achievement.setId(dto.getId());
    }

    private void setAchievement(AchievementDto achievementDto, Achievement achievement) {
        if (achievementDto.getPhotoCertificate() != null) {
            achievement.setPhotoCertificate(attachmentService.saveToSystem(achievementDto.getPhotoCertificate()));
        }
        User user = userRepository.findById(achievementDto.getUserId())
                .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
        achievement.setUser(user);
    }
}
