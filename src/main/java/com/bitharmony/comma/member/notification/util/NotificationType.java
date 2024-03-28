package com.bitharmony.comma.member.notification.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    NEW_ALBUM("님의 새 앨범이 등록되었습니다!", "/album/"),
    NEW_ARTICLE("님의 새 글이 등록되었습니다!", "/community");

    private final String message;
    private final String redirectUrl;
}
