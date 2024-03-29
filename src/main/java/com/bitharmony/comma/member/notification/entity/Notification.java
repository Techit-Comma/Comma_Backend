package com.bitharmony.comma.member.notification.entity;

import com.bitharmony.comma.member.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String redirectUrl;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member publisher;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member subscriber;

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

}
