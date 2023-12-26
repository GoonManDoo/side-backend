package com.sideteam.groupsaver.domain.club.repository;

import com.sideteam.groupsaver.domain.club.domain.ClubMember;
import com.sideteam.groupsaver.domain.club.domain.ClubMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    boolean existsByMemberId(long memberId);

    @Query("SELECT c.role FROM ClubMember c WHERE c.memberId = :memberId AND c.clubId = :clubId")
    ClubMemberRole findRoleByMemberIdAndClubId(long memberId, long clubId);
}