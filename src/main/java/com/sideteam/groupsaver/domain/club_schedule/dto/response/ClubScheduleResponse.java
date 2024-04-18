package com.sideteam.groupsaver.domain.club_schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideteam.groupsaver.domain.club_schedule.domain.ClubSchedule;
import com.sideteam.groupsaver.global.util.ProjectTimeFormat;

import java.time.LocalDateTime;

public record ClubScheduleResponse(
        Long clubScheduleId,
        Long clubId,
        String title,
        String description,
        String location,
        Long participationFee,
        LocalDateTime meetAt,
        Integer currentParticipation,
        Integer maxParticipation
) {
    public static ClubScheduleResponse from(ClubSchedule schedule) {
        return new ClubScheduleResponse(
                schedule.getId(),
                schedule.getClub().getId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getLocation(),
                schedule.getParticipationFee(),
                schedule.getMeetAt(),
                schedule.getClubScheduleMemberCount(),
                schedule.getMaxParticipation().intValue()
        );
    }
}
