package com.example.servise;

import com.example.config.jwtConfig.JwtGenerate;
import com.example.entity.Attachment;
import com.example.entity.User;
import com.example.exception.UserAlreadyExistException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.FireBaseTokenRegisterDto;
import com.example.model.request.SmsModel;
import com.example.model.request.UserDto;
import com.example.model.request.UserVerifyDto;
import com.example.model.response.NotificationMessageResponse;
import com.example.model.response.TokenResponse;
import com.example.model.response.UserResponseDto;
import com.example.model.response.UserResponseListForAdmin;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


import java.util.*;
import java.util.random.RandomGenerator;

import static com.example.enums.Constants.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final JwtGenerate jwtGenerate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SmsService service;
    private final FireBaseMessagingService fireBaseMessagingService;
    //    private final RoleRepository roleRepository;

    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse registerUser(UserDto userDto) {
        boolean byPhone = userRepository.existsByPhoneNumber(userDto.getPhoneNumber());
        if (byPhone) {
            throw new UserAlreadyExistException(USER_ALREADY_EXIST);
        }
        Integer verificationCode = verificationCodeGenerator();
//        service.sendSms(SmsModel.builder()
//                .mobile_phone(userRegisterDto.getPhoneNumber())
//                .message("Cambridge school "+verificationCode + ".")
//                .from(4546)
//                .callback_url("http://0000.uz/test.php")
//                .build());
        System.out.println(verificationCode);
        userRepository.save(from(userDto, verificationCode));
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse verify(UserVerifyDto userVerifyRequestDto) {
        User user = userRepository.findByPhoneNumberAndVerificationCode(userVerifyRequestDto.getPhoneNumber(), userVerifyRequestDto.getVerificationCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        user.setVerificationCode(0);
        user.setBlocked(true);
        userRepository.save(user);
        return new ApiResponse(USER_VERIFIED_SUCCESSFULLY, true,new TokenResponse(JwtGenerate.generateAccessToken(user), fromUserToResponse(user)));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse login(UserDto userLoginRequestDto) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userLoginRequestDto.getPhoneNumber(), userLoginRequestDto.getPassword());
            Authentication authenticate = authenticationManager.authenticate(authentication);
            User user = (User) authenticate.getPrincipal();
            return new ApiResponse(new TokenResponse(JwtGenerate.generateAccessToken(user), fromUserToResponse(user)), true);
        } catch (BadCredentialsException e) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse forgetPassword(String number) {
        User user = userRepository.findByPhoneNumber(number).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Integer verificationCode = verificationCodeGenerator();
        System.out.println("Verification code: " + verificationCode);
//        service.sendSms(SmsModel.builder()
//                .mobile_phone(userRegisterDto.getPhoneNumber())
//                .message("Cambridge school "+verificationCode + ".")
//                .from(4546)
//                .callback_url("http://0000.uz/test.php")
//                .build());
        return new ApiResponse(SUCCESSFULLY, true, user);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByUserId(UUID id) {
        User user = checkUserExistById(id);
        return new ApiResponse(fromUserToResponse(user), true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse addBlockUserByID(UUID id) {
        User user = checkUserExistById(id);
        Optional<User> byId = userRepository.findById(id);
        byId.get().setBlocked(false);
        userRepository.save(byId.get());
        NotificationMessageResponse notificationMessageResponse = NotificationMessageResponse.from(user.getFireBaseToken(), BLOCKED, new HashMap<>());
        fireBaseMessagingService.sendNotificationByToken(notificationMessageResponse);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse openToBlockUserByID(UUID id) {
        User user = checkUserExistById(id);
        Optional<User> byId = userRepository.findById(id);
        byId.get().setBlocked(true);
        userRepository.save(byId.get());
        NotificationMessageResponse notificationMessageResponse = NotificationMessageResponse.from(user.getFireBaseToken(), OPEN, new HashMap<>());
        fireBaseMessagingService.sendNotificationByToken(notificationMessageResponse);
        return new ApiResponse(DELETED, true);
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
        User user = userRepository.findByPhoneNumber(number).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ApiResponse(user, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> all = userRepository.findAll(pageable);
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        all.forEach(obj -> userResponseDtoList.add(fromUserToResponse(obj)));
        return new ApiResponse(new UserResponseListForAdmin(userResponseDtoList, all.getTotalElements(), all.getTotalPages(), all.getNumber()), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse checkUserResponseExistById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        User user = (User) authentication.getPrincipal();
        User user1 = userRepository.findByPhoneNumber(user.getPhoneNumber()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return new ApiResponse(fromUserToResponse(user1), true);
    }
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse reSendSms(String number) {
        Integer integer = verificationCodeGenerator();
        System.out.println(integer);
        service.sendSms(SmsModel.builder()
                .mobile_phone(number)
                .message("Tasdiqlash kodi: " + integer + ". Yo'linggiz bexatar  bo'lsin.")
                .from(4546)
                .callback_url("http://0000.uz/test.php")
                .build());
        return new ApiResponse(SUCCESSFULLY, true);
    }
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse removeUserFromContext() {
        User user = checkUserExistByContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getName().equals(user.getPhoneNumber())) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return new ApiResponse(SUCCESSFULLY, true);
    }

    public User checkUserExistByContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        User user = (User) authentication.getPrincipal();
        return userRepository.findByPhoneNumber(user.getPhoneNumber()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public User checkUserExistById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private User from(UserDto userDto, int verificationCode) {
        User user = User.from(userDto);
        user.setVerificationCode(verificationCode);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        user.setRoles(List.of(roleRepository.findByName(CLIENT), roleRepository.findByName(DRIVER)));
        return user;
    }

    public UserResponseDto fromUserToResponse(User user) {
        String photoLink = "https://sb.kaleidousercontent.com/67418/992x558/7632960ff9/people.png";
        if (user.getProfilePhoto() != null) {
            Attachment attachment = user.getProfilePhoto();
            photoLink = attachmentService.attachUploadFolder + attachment.getPath() + "/" + attachment.getNewName() + "." + attachment.getType();
        }
        UserResponseDto userResponseDto = UserResponseDto.from(user);
        userResponseDto.setProfilePhotoUrl(photoLink);
        return userResponseDto;
    }

    private Integer verificationCodeGenerator() {
        Random random = new Random();
        return random.nextInt(1000,9999);
    }

//    public ApiResponse updateUser(UserUpdateDto userUpdateDto) {
//        User user = checkUserExistByContext();
//        user.setFullName(userUpdateDto.getFullName());
//        user.setGender(userUpdateDto.getGender());
//        if (userUpdateDto.getProfilePhoto() != null) {
//            Attachment attachment = attachmentService.saveToSystem(userUpdateDto.getProfilePhoto());
//            if (user.getProfilePhoto() != null) {
//                attachmentService.deleteNewNameId(user.getProfilePhoto().getNewName() + "." + user.getProfilePhoto().getType());
//            }
//            user.setProfilePhoto(attachment);
//        }
//        user.setBirthDate(userUpdateDto.getBrithDay());
//        userRepository.save(user);
//        return new ApiResponse(SUCCESSFULLY, true);
//    }
}


