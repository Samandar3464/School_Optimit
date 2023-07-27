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
import com.example.repository.SubjectRepository;
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

    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SmsService service;
    private final FireBaseMessagingService fireBaseMessagingService;
    private final RoleService roleService;
    private final BranchRepository branchRepository;


    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse create(UserRegisterDto userRegisterDto) {
        User user = setUserAndCheckByNumber(userRegisterDto);
        user.setProfilePhoto(userRegisterDto.getProfilePhoto() == null ? null : attachmentService.saveToSystem(userRegisterDto.getProfilePhoto()));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @Override
    public ApiResponse getById(Integer id) {
        User user = getUserById(id);
        return new ApiResponse(toUserResponse(user), true);
    }

    @Override
    public ApiResponse update(UserRegisterDto userRegisterDto) {
        getUserById(userRegisterDto.getId());
        User user = setUserAndCheckByNumber(userRegisterDto);
        user.setId(userRegisterDto.getId());
        setPhotoIfIsExist(userRegisterDto, user);
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        User optional = getUserById(integer);
        optional.setBlocked(true);
        userRepository.save(optional);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse verify(UserVerifyDto userVerifyRequestDto) {
        User user = userRepository.findByPhoneNumberAndVerificationCode(userVerifyRequestDto.getPhoneNumber(), userVerifyRequestDto.getVerificationCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        user.setVerificationCode(0);
        user.setBlocked(true);
        userRepository.save(user);
        return new ApiResponse(USER_VERIFIED_SUCCESSFULLY, true, new TokenResponse(JwtGenerate.generateAccessToken(user), toUserResponse(user)));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse login(UserDto userLoginRequestDto) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userLoginRequestDto.getPhoneNumber(), userLoginRequestDto.getPassword());
            Authentication authenticate = authenticationManager.authenticate(authentication);
            User user = (User) authenticate.getPrincipal();
            return new ApiResponse(new TokenResponse(JwtGenerate.generateAccessToken(user), toUserResponse(user)), true);
        } catch (BadCredentialsException e) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse forgetPassword(String number) {
        User user = getUserByNumber(number);
        sendSms(getUserByNumber(number).getPhoneNumber(), verificationCodeGenerator());
        return new ApiResponse(SUCCESSFULLY, true, user);
    }


    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse addBlockUserByID(Integer id) {
        User user = getUserById(id);
        user.setBlocked(true);
        userRepository.save(user);
        sendNotificationByToken(user, BLOCKED);
        return new ApiResponse(BLOCKED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse openToBlockUserByID(Integer id) {
        User user = getUserById(id);
        user.setBlocked(false);
        userRepository.save(user);
        sendNotificationByToken(user, OPEN);
        return new ApiResponse(OPEN, true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse saveFireBaseToken(FireBaseTokenRegisterDto fireBaseTokenRegisterDto) {
        User user = getUserById(fireBaseTokenRegisterDto.getUserId());
        user.setFireBaseToken(fireBaseTokenRegisterDto.getFireBaseToken());
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse changePassword(String number, String password) {
        User user = getUserByNumber(number);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ApiResponse(user, true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getUserList(int page, int size) {
        Page<User> all = userRepository.findAll(PageRequest.of(page, size));
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        all.forEach(obj -> userResponseDtoList.add(toUserResponse(obj)));
        return new ApiResponse(new UserResponseListForAdmin(userResponseDtoList, all.getTotalElements(), all.getTotalPages(), all.getNumber()), true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse checkUserResponseExistById() {
        User user = checkUserExistByContext();
        return new ApiResponse(toUserResponse(user), true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse removeUserFromContext() {
        User user = checkUserExistByContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName().equals(user.getPhoneNumber())) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return new ApiResponse(DELETED, true);
    }


    public User checkUserExistByContext() {
        User user = getUserFromContext();
        return getUserByNumber(user.getPhoneNumber());
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        return (User) authentication.getPrincipal();
    }

    public ApiResponse addSubjectToUser(Integer userId, List<Integer> subjectIds) {
        User user = getUserById(userId);
        user.setSubjects(subjectRepository.findAllById(subjectIds));
        userRepository.save(user);
        return new ApiResponse(SUCCESSFULLY, true, toUserResponse(user));
    }

    public UserResponseDto toUserResponse(User user) {
        UserResponseDto userResponseDto = UserResponseDto.from(user);
        userResponseDto.setProfilePhotoUrl(getPhotoLink(user.getProfilePhoto()));
        return userResponseDto;
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse reSendSms(String number) {
        sendSms(number, verificationCodeGenerator());
        return new ApiResponse(SUCCESSFULLY, true);
    }

    private Integer verificationCodeGenerator() {
        Random random = new Random();
        return random.nextInt(1000, 9999);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public User getUserByNumber(String number) {
        return userRepository.findByPhoneNumber(number).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public String getPhotoLink(Attachment attachment) {
        String photoLink = "https://sb.kaleidousercontent.com/67418/992x558/7632960ff9/people.png";
        if (attachment != null) {
            photoLink = attachmentService.attachUploadFolder + attachment.getPath() + "/" + attachment.getNewName() + "." + attachment.getType();
        }
        return photoLink;
    }

    private void setPhotoIfIsExist(UserRegisterDto userRegisterDto, User user) {
        if (userRegisterDto.getProfilePhoto() != null) {
            Attachment attachment = attachmentService.saveToSystem(userRegisterDto.getProfilePhoto());
            if (user.getProfilePhoto() != null) {
                attachmentService.deleteNewName(user.getProfilePhoto());
            }
            user.setProfilePhoto(attachment);
        }
    }

    private User setUserAndCheckByNumber(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByPhoneNumberAndBlockedFalse(userRegisterDto.getPhoneNumber())) {
            throw new RecordAlreadyExistException(PHONE_NUMBER_ALREADY_REGISTERED);
        }
        User user = User.from(userRegisterDto);
        user.setBranch(branchRepository.findById(userRegisterDto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
        user.setRoles(userRegisterDto.getRolesIds() == null ? null : roleService.getAllByIds(userRegisterDto.getRolesIds()));
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setSubjects(userRegisterDto.getSubjectsIds() == null ? null : subjectRepository.findAllById(userRegisterDto.getSubjectsIds()));
        return user;
    }

    private void sendNotificationByToken(User user, String message) {
        NotificationMessageResponse notificationMessageResponse = NotificationMessageResponse.from(user.getFireBaseToken(), message, new HashMap<>());
        fireBaseMessagingService.sendNotificationByToken(notificationMessageResponse);
    }

    private void sendSms(String phoneNumber, Integer verificationCode) {
        service.sendSms(SmsModel.builder()
                .mobile_phone(phoneNumber)
                .message("Cambridge school " + verificationCode + ".")
                .from(4546)
                .callback_url("http://0000.uz/test.php")
                .build());
    }
}


