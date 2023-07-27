package com.example.controller;

import com.example.enums.Constants;
import com.example.model.common.ApiResponse;
import com.example.model.request.FireBaseTokenRegisterDto;
import com.example.model.request.UserDto;
import com.example.model.request.UserRegisterDto;
import com.example.model.request.UserVerifyDto;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public ApiResponse registerUser(@ModelAttribute @Validated UserRegisterDto userRegisterDto) {
        return userService.create(userRegisterDto);
    }

    @PostMapping("/addSubjectToUser")
    public ApiResponse addSubjectToUser(@RequestParam Integer userId,
                                        @RequestParam List<Integer> subjectIds) {
        return userService.addSubjectToUser(userId, subjectIds);
    }


    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Validated UserDto userLoginRequestDto) {
        return userService.login(userLoginRequestDto);
    }

    @PostMapping("/verify")
    public ApiResponse verify(@RequestBody UserVerifyDto userVerifyDto) {
        return userService.verify(userVerifyDto);
    }

    @PostMapping("/forgetPassword")
    public ApiResponse forgetPassword(@RequestBody String number) {
        return userService.forgetPassword(number);
    }

    @GetMapping("/getById/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/getUserByNumber/{phoneNumber}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse getUserByNumber(@PathVariable String phoneNumber) {
        return new ApiResponse(Constants.SUCCESSFULLY, true, userService.getUserByNumber(phoneNumber));
    }

    @PutMapping("/block/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse blockUserById(@PathVariable Integer id) {
        return userService.addBlockUserByID(id);
    }

    @PutMapping("/openBlock/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse openBlockUserById(@PathVariable Integer id) {
        return userService.openToBlockUserByID(id);
    }

    @PostMapping("/setFireBaseToken")
    public ApiResponse setFireBaseToken(@RequestBody FireBaseTokenRegisterDto fireBaseTokenRegisterDto) {
        return userService.saveFireBaseToken(fireBaseTokenRegisterDto);
    }

    @PostMapping("/changePassword")
    public ApiResponse changePassword(@RequestParam String number, @RequestParam String password) {
        return userService.changePassword(number, password);
    }

    @PutMapping("/update")
//    @PreAuthorize("hasAnyRole('DRIVER','CLIENT','ADMIN')")
    public ApiResponse update(@RequestBody UserRegisterDto userUpdateDto) {
        return userService.update(userUpdateDto);
    }

    @GetMapping("/getUserList")
//    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getUserList(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "5") int size) {
        return userService.getUserList(page, size);
    }

    @GetMapping("/getByToken")
    public ApiResponse checkUserResponseExistById() {
        return userService.checkUserResponseExistById();
    }

    @GetMapping("/reSendSms/{phone}")
    public ApiResponse reSendSms(@PathVariable String phone) {
        return userService.reSendSms(phone);
    }

    @GetMapping("/logout")
    public ApiResponse deleteUserFromContext() {
        return userService.removeUserFromContext();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return userService.delete(id);
    }
}
