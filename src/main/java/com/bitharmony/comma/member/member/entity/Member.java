package com.bitharmony.comma.member.member.entity;

import com.bitharmony.comma.album.playlist.entity.Playlist;
import java.util.ArrayList;
import java.util.List;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.member.follow.entity.Follow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username은 필수 입력값입니다.")
    @Size(min = 5, max = 100)
    @Column(length = 100)
    private String username;

    @Size(min = 5, max = 100)
    @Column(length = 100)
    private String password;

    @Email
    private String email;

    @NotNull(message = "nickname은 필수 입력값입니다.")
    @Size(min = 5, max = 100)
    @Column(length = 100)
    private String nickname;

    private String imageUrl;

    @Builder.Default
    private Long credit = 0L;

    @OneToMany(mappedBy = "following")
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    private List<Follow> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "producer")
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Album> albumList = new ArrayList<>();

    @Builder.Default
    private String provider = "COMMA";

    private String providerId;


}