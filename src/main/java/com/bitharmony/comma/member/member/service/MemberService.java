package com.bitharmony.comma.member.member.service;

import com.bitharmony.comma.global.exception.member.DuplicateNicknameException;
import com.bitharmony.comma.global.exception.member.IncorrectPasswordException;
import com.bitharmony.comma.global.exception.member.InvalidPasswordException;
import com.bitharmony.comma.member.member.repository.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.global.exception.member.MemberDuplicateException;
import com.bitharmony.comma.global.exception.member.MemberNotFoundException;
import com.bitharmony.comma.global.security.SecurityUser;
import com.bitharmony.comma.global.util.JwtUtil;
import com.bitharmony.comma.member.auth.dto.JwtCreateRequest;
import com.bitharmony.comma.member.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.member.dto.MemberReturnResponse;
import com.bitharmony.comma.member.member.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ProfileImageService profileImageService;

    public MemberLoginResponse login(String username, String password) {
        Member member = getMemberByUsername(username);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IncorrectPasswordException();
        }

        JwtCreateRequest jwtCreateRequest = JwtCreateRequest.builder()
                .id(member.getId())
                .username(member.getUsername())
                .build();

        String accessToken = jwtUtil.createAccessToken(jwtCreateRequest);
        String refreshToken = jwtUtil.createRefreshToken(jwtCreateRequest);

        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout() {
        SecurityUser loginedMember = getUser();
        if (redisTemplate.opsForValue().get(loginedMember.getUsername()) != null) {
            redisTemplate.delete(loginedMember.getUsername());
        }
    }

    @Transactional
    public void join(String username, String password, String passwordCheck, String email, String nickname) {
        if (!password.equals(passwordCheck)) {
            throw new InvalidPasswordException();
        }

        if (memberRepository.findByUsername(username).isPresent()) {
            throw new MemberDuplicateException();
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateNicknameException();
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .imageUrl(profileImageService.defaultProfileUrl)
                .build();

        memberRepository.save(member);
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    public MemberReturnResponse getProfile() {
        SecurityUser loginedMember = getUser();
        Member findMember = getMemberByUsername(loginedMember.getUsername());

        return MemberReturnResponse.builder()
                .username(findMember.getUsername())
                .Email(findMember.getEmail())
                .nickname(findMember.getNickname())
                .profileImageUrl(findMember.getImageUrl())
                .build();
    }

    public MemberReturnResponse getProfile(String username) {

        Member findMember = getMemberByUsername(username);

        return MemberReturnResponse.builder()
                .username(findMember.getUsername())
                .Email(findMember.getEmail())
                .nickname(findMember.getNickname())
                .profileImageUrl(findMember.getImageUrl())
                .build();
    }

    @Transactional
    public void modify(String nickname, String email) {

        SecurityUser user = getUser();
        long id = user.getId();
        Member findMember = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateNicknameException();
        }

        Member modifyMember = findMember.toBuilder()
                .nickname(nickname)
                .email(email)
                .build();

        memberRepository.save(modifyMember);
    }

    @Transactional
    public void passwordModify(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new InvalidPasswordException();
        }

        SecurityUser user = getUser();
        Member member = getMemberByUsername(user.getUsername());

        member = member.toBuilder()
                .password(passwordEncoder.encode(password))
                .build();

        memberRepository.save(member);
    }

    public SecurityUser getUser() {
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void updateCredit(String username, Long updatedCredit) {
        Member member = getMemberByUsername(username);
        memberRepository.save(member.toBuilder().credit(updatedCredit).build());
    }

    @Transactional
    public void setProfileImage(Member member, String imageUrl) {
        Member _member = member.toBuilder()
                .imageUrl(imageUrl)
                .build();

        memberRepository.save(_member);
    }

    @Transactional
    public void updateUserAlbum(Member member, Album album){
        member.getAlbumList().add(album);
        memberRepository.save(member);
    }
}
