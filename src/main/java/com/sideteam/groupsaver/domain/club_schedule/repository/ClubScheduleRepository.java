package com.sideteam.groupsaver.domain.club_schedule.repository;

import com.sideteam.groupsaver.domain.club.domain.ClubMemberRole;
import com.sideteam.groupsaver.domain.club_schedule.domain.ClubSchedule;
import com.sideteam.groupsaver.global.exception.BusinessException;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.sideteam.groupsaver.global.exception.club.ClubScheduleErrorCode.CLUB_SCHEDULE_NOT_FOUND;


public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {

    default ClubSchedule findOrThrowWithReadLock(Long clubScheduleId) {
        return findByIdForReadLock(clubScheduleId)
                .orElseThrow(() -> new BusinessException(CLUB_SCHEDULE_NOT_FOUND, clubScheduleId.toString()));
    }

    default boolean isCreatorOrLeader(Long memberId, Long clubScheduleId) {
        return isCreatorOrHasClubRole(memberId, clubScheduleId, ClubMemberRole.LEADER);
    }


    @Query("SELECT cs FROM ClubSchedule cs WHERE cs.club.id=:clubId ORDER BY cs.id DESC")
    Page<ClubSchedule> findAllByClubId(Long clubId, Pageable pageable);

    @Lock(value = LockModeType.PESSIMISTIC_READ)
    @Query("SELECT cs FROM ClubSchedule cs WHERE cs.id=:id")
    Optional<ClubSchedule> findByIdForReadLock(Long id);

    @Query("SELECT CASE WHEN COUNT (cm.id) > 0 THEN true ELSE false END " +
            "FROM ClubSchedule cs " +
            "JOIN ClubMember cm ON cs.club.id = cm.club.id " +
            "WHERE cs.id = :clubScheduleId " +
            "AND (cs.creator.id = :memberId OR cm.member.id = :memberId AND cm.role = :role) ")
    boolean isCreatorOrHasClubRole(Long memberId, Long clubScheduleId, ClubMemberRole role);

    @Query("SELECT cs.club.id FROM ClubSchedule cs WHERE cs.id=:id")
    Long findClubIdById(Long id);
}
