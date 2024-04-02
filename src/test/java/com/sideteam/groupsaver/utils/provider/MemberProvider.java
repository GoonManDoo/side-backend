package com.sideteam.groupsaver.utils.provider;

import com.sideteam.groupsaver.domain.auth.dto.request.SignupRequest;
import com.sideteam.groupsaver.domain.category.domain.JobMajor;
import com.sideteam.groupsaver.domain.member.domain.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public final class MemberProvider {

    public static Member createMember() {
        return Member.builder()
                .jobCategory(createJobMajor())
                .nickname(createRandomNickname())
                .phoneNumber(createRandomPhoneNumber())
                .oAuthProvider(OAuthProvider.STANDARD)
                .birth(LocalDate.now())
                .role(MemberRole.USER)
                .email(createRandomEmail())
                .gender(createGender())
                .password("123123123ZXC!@#$a")
                .build();
    }

    public static SignupRequest createSignupRequest(String nickname, String email, String password) {
        return new SignupRequest(
                createRandomPhoneNumber(), nickname, email, password,
                createJobMajor(), createGender(), LocalDate.now(),
                Collections.emptyList());
    }

    private static String createRandomNickname() {
        return "test-" + UUID.randomUUID();
    }

    private static String createRandomPhoneNumber() {
        Random random = new Random();

        int prefix = random.nextInt(1000);
        int middle = random.nextInt(10_000);
        int last = random.nextInt(10_000);

        return String.format("%03d-%04d-%04d", prefix, middle, last);
    }

    private static String createRandomEmail() {
        String username = UUID.randomUUID().toString();
        String domain = UUID.randomUUID().toString().substring(0, 5);
        String domainExtension = UUID.randomUUID().toString().substring(0, 2);

        return username + "@" + domain + "." + domainExtension;
    }


    private static JobMajor createJobMajor() {
        return JobMajor.values()[(int) (Math.random() * JobMajor.values().length)];
    }

    private static MemberGender createGender() {
        return Math.random() > 0.5 ? MemberGender.M : MemberGender.F;
    }

}
