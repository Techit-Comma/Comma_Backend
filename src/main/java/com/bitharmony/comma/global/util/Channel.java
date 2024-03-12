package com.bitharmony.comma.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Channel {

    ENCODING_STATUS("encodingStatus"),
    ARTIST_NOTIFICATION("artistNotification");

    private final String name;
}
