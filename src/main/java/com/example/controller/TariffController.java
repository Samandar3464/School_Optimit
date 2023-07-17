package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.TariffDto;
import com.example.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tariff/")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping("save")
    public ApiResponse create(@RequestBody @Valid TariffDto tariffDto) {
        return tariffService.create(tariffDto);
    }

    @GetMapping("getTariffById/{id}")
    public ApiResponse getTariffById(@PathVariable Integer id) {
        return tariffService.getById(id);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody @Valid TariffDto tariffDto) {
        return tariffService.update(tariffDto);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return tariffService.delete(id);
    }

    @GetMapping("deActivate/{id}")
    public ApiResponse deActivate(@PathVariable Integer id) {
        return tariffService.deActivate(id);
    }

    @GetMapping("activate/{id}")
    public ApiResponse activate(@PathVariable Integer id) {
        return tariffService.activate(id);
    }


    @GetMapping("getTariffListForAdmin")
    public ApiResponse getTariffList() {
        return tariffService.getTariffListForAdmin();
    }

    @GetMapping("getTariffListForUser")
    public ApiResponse getTariffListForUser() {
        return tariffService.getTariffListForUser();
    }

}
