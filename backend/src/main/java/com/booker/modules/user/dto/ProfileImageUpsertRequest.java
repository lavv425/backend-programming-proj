package com.booker.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfileImageUpsertRequest {

    @NotBlank
    public String profileImageUrl;
}
