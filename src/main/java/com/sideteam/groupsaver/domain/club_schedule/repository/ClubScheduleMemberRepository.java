package com.sideteam.groupsaver.domain.club_schedule.repository;

import com.sideteam.groupsaver.domain.club_schedule.domain.ClubScheduleMember;
import com.sideteam.groupsaver.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ClubScheduleMemberRepository extends JpaRepository<ClubScheduleMember, Long> {

    @Query("SELECT csm.member FROM ClubScheduleMember csm WHERE csm.clubSchedule.id = :clubScheduleId")
    List<Member> findAllScheduleMembersByClubScheduleId(Long clubScheduleId);

    void deleteByClubScheduleIdAndMemberId(Long clubScheduleId, Long memberId);
}
