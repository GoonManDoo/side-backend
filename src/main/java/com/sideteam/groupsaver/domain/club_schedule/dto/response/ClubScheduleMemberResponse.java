package com.sideteam.groupsaver.domain.club_schedule.dto.response;

import com.sideteam.groupsaver.domain.club_schedule.domain.ClubScheduleMember;
import com.sideteam.groupsaver.domain.member.domain.Member;

public record ClubScheduleMemberResponse(
        Long memberId,
        String memberProfileUrl,
        String nickname,
        boolean isLeader,
        boolean isMe
) {
    public static ClubScheduleMemberResponse from(Member member, boolean isLeader, boolean isMe) {
        return new ClubScheduleMemberResponse(
                member.getId(),
                member.getProfileUrl(),
                member.getNickname(),
                isLeader,
                isMe
        );
    }
}
