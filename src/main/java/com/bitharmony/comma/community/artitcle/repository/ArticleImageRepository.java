package com.bitharmony.comma.community.artitcle.repository;

import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {

    List<ArticleImage> findByArticleId(Long articleId);

    Optional<ArticleImage> findByImageUrl(String imageUrl);
}
