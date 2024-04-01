package com.sideteam.groupsaver.domain.club.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideteam.groupsaver.domain.club.domain.PublicNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.sideteam.groupsaver.global.util.ProjectTimeFormat.LOCAL_CREATE_DATE_PATTERN;
import static com.sideteam.groupsaver.global.util.ProjectTimeFormat.LOCAL_CREATE_DATE_PATTERN_EXAMPLE;

@Getter
@Builder
public class PublicResponse {
    private long publicId;
    private String title;
    private String body;
    private String creator;
    @Schema(description = "생성일", example = LOCAL_CREATE_DATE_PATTERN_EXAMPLE, type = "string")
    @JsonFormat(pattern = LOCAL_CREATE_DATE_PATTERN)
    private LocalDate createAt;
    private boolean fix;

    public static PublicResponse of(long publicId,String title, String body, String creator, LocalDate createAt, boolean fix) {
        return PublicResponse.builder()
                .publicId(publicId)
                .title(title)
                .body(body)
                .creator(creator)
                .createAt(createAt)
                .fix(fix)
                .build();
    }

    public static List<PublicResponse> listOf(List<PublicNotification> publicNotificationList) {
        return publicNotificationList.stream()
                .map(publicNotification -> PublicResponse.of(
                        publicNotification.getId(),
                        publicNotification.getTitle(),
                        publicNotification.getBody(),
                        publicNotification.getCreator().getNickname(),
                        LocalDate.from(publicNotification.getCreatedAtLocalDateTime()),
                        publicNotification.isFix()
                ))
                .collect(Collectors.toList());
    }
}
