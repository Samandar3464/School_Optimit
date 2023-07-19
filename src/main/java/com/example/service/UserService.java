package com.example.service;

import com.example.config.jwtConfig.JwtGenerate;
import com.example.entity.Attachment;
import com.example.entity.User;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.*;
import com.example.model.response.NotificationMessageResponse;
import com.example.model.response.TokenResponse;
import com.example.model.response.UserResponseDto;
import com.example.model.response.UserResponseListForAdmin;
import com.example.repository.BranchRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.example.enums.Constants.*;


@Service
@RequiredArgsConstructor
public class UserService implements BaseService<UserRegisterDto, Integer> {

    private final SmsService smsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AttachmentService attachmentService;
    private final AuthenticationManager authenticationManager;
    private final FireBaseMessagingService fireBaseMessagingService;
    private final PasswordEncoder passwordEncoder;
    private final BranchRepository branchRepository;
    private final SubjectService subjectService;
    private final DailyLessonsService dailyLessonsService;

    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse create(UserRegisterDto dto) {
        if (userRepository.existsByPhoneNumberAndDeletedFalse(dto.getPhoneNumber())) {
            throw new RecordAlreadyExistException(USER_ALREADY_EXIST);
        }
        User user = User.from(dto);
        user.setProfilePhoto(dto.getProfilePhoto()==null ?  null :attachmentService.saveToSystem(dto.getProfilePhoto()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBranch(branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
        user.setRole(roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RecordNotFoundException(ROLE_NOT_FOUND)));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse login(UserDto userLoginRequestDto) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userLoginRequestDto.getPhoneNumber(), userLoginRequestDto.getPassword());
            Authentication authenticate = authenticationManager.authenticate(authentication);
            User user = (User) authenticate.getPrincipal();
            return new ApiResponse(new TokenResponse(JwtGenerate.generateAccessToken(user), UserResponseDto.from(user, attachmentService.getUrl(user.getProfilePhoto()))), true);
        } catch (BadCredentialsException e) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer id) {
        User user = checkUserExistById(id);
        return new ApiResponse(UserResponseDto.from(user, attachmentService.getUrl(user.getProfilePhoto())), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(UserRegisterDto dto) {
        User user = checkUserExistById(dto.getId());
        if (!user.getPhoneNumber().equals(dto.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumberAndDeletedFalse(dto.getPhoneNumber())) {
                throw new RecordAlreadyExistException(USER_ALREADY_EXIST);
            }
        }
        updateUser(dto, user);
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        User optional = checkUserExistById(integer);
        optional.setDeleted(true);
        optional.setBlocked(false);
        userRepository.save(optional);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse verify(UserVerifyDto userVerifyRequestDto) {
        User user = userRepository.findByPhoneNumberAndVerificationCodeAndDeletedFalse(userVerifyRequestDto.getPhoneNumber(), userVerifyRequestDto.getVerificationCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        user.setVerificationCode(0);
        user.setBlocked(true);
        userRepository.save(user);
        return new ApiResponse(USER_VERIFIED_SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse forgetPassword(String number) {
        System.out.println(number);
        User user = checkByNumber(number);
        user.setVerificationCode(verificationCodeGenerator());
        userRepository.save(user);
//        sendSms(user.getPhoneNumber(), verificationCodeGenerator());
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse addBlockUserByID(Integer id) {
        User user = checkUserExistById(id);
        user.setBlocked(false);
        userRepository.save(user);
//        sendNotificationByToken(user, BLOCKED);
        return new ApiResponse(BLOCKED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse openToBlockUserByID(Integer id) {
        User user = checkUserExistById(id);
        user.setBlocked(true);
        userRepository.save(user);
//        sendNotificationByToken(user, OPEN);
        return new ApiResponse(OPEN, true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse saveFireBaseToken(FireBaseTokenRegisterDto fireBaseTokenRegisterDto) {
        User user = checkUserExistById(fireBaseTokenRegisterDto.getUserId());
        user.setFireBaseToken(fireBaseTokenRegisterDto.getFireBaseToken());
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse changePassword(String number, String password) {
        User user = checkByNumber(number);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true, new TokenResponse(JwtGenerate.generateAccessToken(user), UserResponseDto.from(user, attachmentService.getUrl(user.getProfilePhoto()))));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getUserList(Integer page, Integer size, Integer branchId) {
        Page<User> all = userRepository.findAllByBranchIdAndDeletedFalse(branchId,PageRequest.of(page, size));
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        all.getContent().forEach(user -> userResponseDtoList.add(UserResponseDto.from(user, attachmentService.getUrl(user.getProfilePhoto()))));
        return new ApiResponse(new UserResponseListForAdmin(userResponseDtoList, all.getTotalElements(), all.getTotalPages(), all.getNumber()), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse removeUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        } else if (authentication != null && authentication.getName().equals(checkByNumber(((User) authentication.getPrincipal()).getPhoneNumber()).getPhoneNumber())) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse reSendSms(String number) {
        sendSms(number, verificationCodeGenerator());
        return new ApiResponse(SUCCESSFULLY, true);
    }
//    ko'rib chiqish kk
    public ApiResponse addSubjectToUser(UserRegisterDto userRegisterDto) {
        User user = checkUserExistById(userRegisterDto.getId());
        user.setSubjects(subjectService.checkAllById(userRegisterDto.getSubjectsIds()));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true, user.getSubjects());
    }
    //    ko'rib chiqish kk

    public ApiResponse addDailyLessonToUser(UserRegisterDto userRegisterDto) {
        User user = checkUserExistById(userRegisterDto.getId());
        user.setDailyLessons(dailyLessonsService.checkAllById(userRegisterDto.getDailyLessonsIds()));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true, user.getDailyLessons());
    }



    private Integer verificationCodeGenerator() {
        Random random = new Random();
        return random.nextInt(1000, 9999);
    }

    private User checkUserExistById(Integer id) {
        return userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private User checkByNumber(String number) {
        return userRepository.findByPhoneNumberAndDeletedFalse(number).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private void sendNotificationByToken(User user, String message) {
        NotificationMessageResponse notificationMessageResponse = NotificationMessageResponse.from(user.getFireBaseToken(), message, new HashMap<>());
        fireBaseMessagingService.sendNotificationByToken(notificationMessageResponse);
    }

    private void sendSms(String phoneNumber, Integer verificationCode) {
        smsService.sendSms(SmsModel.builder()
                .mobile_phone(phoneNumber)
                .message("Cambridge school " + verificationCode + ".")
                .from(4546)
                .callback_url("http://0000.uz/test.php")
                .build());
    }

    private void updateUser(UserRegisterDto dto, User user) {
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setFatherName(dto.getFatherName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail() != null ? dto.getEmail() : user.getEmail());
        user.setInn(dto.getInn() == 0 ? user.getInn() : dto.getInn());
        user.setInps(dto.getInps() == 0 ? user.getInps() : dto.getInps());
        user.setBiography(dto.getBiography() == null ? user.getBiography() : dto.getBiography());
        user.setMarried(dto.isMarried());
        if (dto.getProfilePhoto() != null) {
            Attachment attachment = attachmentService.saveToSystem(dto.getProfilePhoto());
            if (user.getProfilePhoto() != null) {
                attachmentService.deleteNewName(user.getProfilePhoto());
            }
            user.setProfilePhoto(attachment);
        }
    }
}


