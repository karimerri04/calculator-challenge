package com.karimmerri.calculator.adapter.in.rest;

public record ApiError(
        String code,
        String message,
        String path
) {
}
