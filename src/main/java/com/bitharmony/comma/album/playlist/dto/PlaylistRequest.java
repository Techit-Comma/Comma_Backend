package com.bitharmony.comma.album.playlist.dto;

import jakarta.validation.constraints.NotBlank;

public record PlaylistRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        String description
) {

}
