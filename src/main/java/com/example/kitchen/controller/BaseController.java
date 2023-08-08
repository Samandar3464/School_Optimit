package com.example.kitchen.controller;

import com.example.model.common.ApiResponse;

public interface BaseController<T,I> {
    ApiResponse create(T t);

    ApiResponse getById(I i);

    ApiResponse update(T t);

    ApiResponse delete(I i);
}
