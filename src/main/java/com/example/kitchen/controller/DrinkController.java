package com.example.kitchen.controller;

import com.example.kitchen.model.request.DrinkRequest;
import com.example.kitchen.service.DrinkService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drink/")
@RequiredArgsConstructor
public class DrinkController implements BaseController<DrinkRequest,Integer>{

   private final DrinkService drinkService;

    @Override
    @PostMapping("create")
    public ApiResponse create(@RequestBody DrinkRequest drinkRequest) {
        return drinkService.create(drinkRequest);
    }

    @Override
    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return drinkService.getById(id);
    }

    @Override
    @PutMapping("update")
    public ApiResponse update(@RequestBody DrinkRequest drinkRequest) {
        return drinkService.update(drinkRequest);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return drinkService.delete(id);
    }
}
