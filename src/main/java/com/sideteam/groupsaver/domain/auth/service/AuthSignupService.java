package com.sideteam.groupsaver.domain.auth.service;

import com.sideteam.groupsaver.domain.auth.dto.request.SignupRequest;
import com.sideteam.groupsaver.domain.auth.dto.response.SignupResult;
import com.sideteam.groupsaver.domain.category.domain.ClubCategoryMajor;
import com.sideteam.groupsaver.domain.join.domain.WantClubCategory;
import com.sideteam.groupsaver.domain.join.repository.WantClubCategoryRepository;
import com.sideteam.groupsaver.domain.member.domain.Member;
import com.sideteam.groupsaver.domain.member.domain.MemberRole;
import com.sideteam.groupsaver.domain.member.domain.OAuthProvider;
import com.sideteam.groupsaver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthSignupService {
    private final MemberRepository memberRepository;
    private final WantClubCategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResult signup(final SignupRequest signupRequest) {

        final Member member = Member.builder()
                .email(signupRequest.email())
                .nickname(signupRequest.nickname())
                .role(MemberRole.USER)
                .oAuthProvider(OAuthProvider.STANDARD)
                .password(encodePassword(signupRequest))
                .phoneNumber(signupRequest.phoneNumber())
                .jobCategory(signupRequest.jobCategory())
                .gender(signupRequest.gender())
                .birth(signupRequest.birth())
                .build();
        memberRepository.save(member);
        createWantClubCategories(signupRequest.categories(), member);

        log.info("회원가입 완료: ID: {}, 이메일: {}, 닉네임: {}", member.getId(), member.getEmail(), member.getNickname());

        return SignupResult.of(member);
    }

    private String encodePassword(final SignupRequest signupRequest) {
        return passwordEncoder.encode(signupRequest.password());
    }

    private void createWantClubCategories(List<ClubCategoryMajor> clubCategoryMajors, Member member) {
        List<WantClubCategory> wantClubCategories = clubCategoryMajors.stream()
                .map(major -> WantClubCategory.of(member, major))
                .toList();

        member.updateWantClubCategory(categoryRepository.saveAll(wantClubCategories));
    }

}
