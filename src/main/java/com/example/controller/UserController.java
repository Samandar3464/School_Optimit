package com.example.controller;

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


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public ApiResponse registerUser(@ModelAttribute @Valid UserRegisterDto userRegisterDto) {
        return userService.create(userRegisterDto);
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid UserDto userLoginRequestDto) {
        return userService.login(userLoginRequestDto);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@ModelAttribute @Valid UserRegisterDto userUpdateDto) {
        return userService.update(userUpdateDto);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return userService.delete(id);
    }

    @PostMapping("/verify")
    public ApiResponse verify(@RequestBody UserVerifyDto userVerifyDto) {
        return userService.verify(userVerifyDto);
    }

    @PostMapping("/forgetPassword")
    public ApiResponse forgetPassword(@RequestParam String number) {
        return userService.forgetPassword(number);
    }

    @PutMapping("/block/{id}")
    public ApiResponse blockUserById(@PathVariable Integer id) {
        return userService.addBlockUserByID(id);
    }

    @PutMapping("/openBlock/{id}")
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

    @GetMapping("/getUserList")
    public ApiResponse getUserList(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", defaultValue = "5") Integer size,
                                   @RequestParam(name = "branchId") Integer branchId) {
        return userService.getUserList(page, size, branchId);
    }

    @GetMapping("/logout")
    public ApiResponse deleteUserFromContext() {
        return userService.removeUserFromContext();
    }

    @GetMapping("/reSendSms/{phone}")
    public ApiResponse reSendSms(@PathVariable String phone) {
        return userService.reSendSms(phone);
    }


    @PostMapping("/addSubjectToUser")
    public ApiResponse addSubjectToUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.addSubjectToUser(userRegisterDto);
    }

    @PostMapping("/addDailyLessonToUser")
    public ApiResponse addDailyLessonToUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.addDailyLessonToUser(userRegisterDto);
    }

}
