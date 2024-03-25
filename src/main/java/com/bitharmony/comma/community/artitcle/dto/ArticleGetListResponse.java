package com.bitharmony.comma.community.artitcle.dto;

import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Builder
public record ArticleGetListResponse(
        Page<ArticleDto> articleList,

        Map<Long, List<String>> articleImages
) {
}
