package com.sideteam.groupsaver.domain.club_schedule.domain;

import com.sideteam.groupsaver.domain.club.domain.Club;
import com.sideteam.groupsaver.domain.club_schedule.dto.request.ClubScheduleRequest;
import com.sideteam.groupsaver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@SQLDelete(sql = "UPDATE club_schedule SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ClubSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted;

    @JoinColumn(nullable = false, name = "club_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    @Column(nullable = false)
    private LocalDateTime meetAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long maxParticipation;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Long participationFee;


    @Formula("(SELECT COUNT(csm.id) from club_schedule_member csm WHERE csm.club_schedule_id = id)")
    private Integer clubScheduleMemberCount;


    public static ClubSchedule of(Club club, ClubScheduleRequest clubScheduleRequest) {

        return new ClubSchedule(
                club,
                clubScheduleRequest.meetAt(),
                clubScheduleRequest.title(),
                clubScheduleRequest.description(),
                clubScheduleRequest.maxParticipation(),
                StringUtils.hasText(clubScheduleRequest.location()) ? clubScheduleRequest.location() : "온라인",
                clubScheduleRequest.participationFee()
        );
    }

    public void update(ClubScheduleRequest clubScheduleRequest) {
        this.meetAt = Objects.requireNonNullElse(clubScheduleRequest.meetAt(), meetAt);
        this.title = Objects.requireNonNullElse(clubScheduleRequest.title(), title);
        this.description = Objects.requireNonNullElse(clubScheduleRequest.description(), description);
        this.maxParticipation = Objects.requireNonNullElse(clubScheduleRequest.maxParticipation(), maxParticipation);
        this.location = Objects.requireNonNullElse(clubScheduleRequest.location(), location);
        this.participationFee = Objects.requireNonNullElse(clubScheduleRequest.participationFee(), participationFee);
    }

    public boolean isReachMaximumParticipation() {
        return maxParticipation <= clubScheduleMemberCount;
    }


    private ClubSchedule(Club club, LocalDateTime meetAt, String title, String description,
                         Long maxParticipation, String location, Long participationFee) {
        this.club = club;
        this.meetAt = meetAt;
        this.title = title;
        this.description = description;
        this.maxParticipation = maxParticipation;
        this.location = location;
        this.participationFee = participationFee;
        this.isDeleted = false;
    }

}
