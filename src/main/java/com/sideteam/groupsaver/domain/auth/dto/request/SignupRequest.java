package com.sideteam.groupsaver.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideteam.groupsaver.domain.category.domain.ClubCategoryMajor;
import com.sideteam.groupsaver.domain.category.domain.JobMajor;
import com.sideteam.groupsaver.domain.member.domain.MemberGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

import static com.sideteam.groupsaver.domain.member.MemberConstants.PHONE_NUMBER_EXAMPLE;
import static com.sideteam.groupsaver.domain.member.MemberConstants.PHONE_NUMBER_REGEXP;
import static com.sideteam.groupsaver.global.auth.AuthConstants.PASSWORD_REGEXP;
import static com.sideteam.groupsaver.global.util.ProjectTimeFormat.LOCAL_DATE_PATTERN;
import static com.sideteam.groupsaver.global.util.ProjectTimeFormat.LOCAL_DATE_PATTERN_EXAMPLE;

public record SignupRequest(

        @Schema(example = PHONE_NUMBER_EXAMPLE)
        @NotBlank(message = "전화번호는 공백이 아니어야 합니다")
        @Pattern(message = "전화번호는 숫자만 허용되며, '-'을 포함한 형식입니다", regexp = PHONE_NUMBER_REGEXP)
        String phoneNumber,

        @NotBlank(message = "닉네임은 공백이 아니어야 합니다")
        String nickname,

        @NotBlank(message = "이메일은 공백이 아니어야 합니다")
        @Email(message = "이메일 주소에 @ 기호와 도메인 명이 필요합니다")
        String email,

        @NotBlank(message = "비밀번호는 공백이 아니어야 합니다")
        @Pattern(message = "비밀번호는 숫자, 영어, 특수문자를 포함해야 합니다", regexp = PASSWORD_REGEXP)
        @Size(min = 6, message = "비밀번호의 길이는 최소 6자 입니다")
        String password,

        JobMajor jobCategory,

        MemberGender gender,

        @Schema(description = "생년월일", example = LOCAL_DATE_PATTERN_EXAMPLE, type = "string")
        @JsonFormat(pattern = LOCAL_DATE_PATTERN)
        LocalDate birth,

        List<ClubCategoryMajor> categories
) {
}
