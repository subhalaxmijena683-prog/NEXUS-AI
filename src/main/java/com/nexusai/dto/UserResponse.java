package com.nexusai.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {
}