package com.sideteam.groupsaver.domain.club_schedule.service;

import com.sideteam.groupsaver.domain.club.repository.ClubMemberRepository;
import com.sideteam.groupsaver.domain.club_schedule.domain.ClubSchedule;
import com.sideteam.groupsaver.domain.club_schedule.domain.ClubScheduleMember;
import com.sideteam.groupsaver.domain.club_schedule.dto.response.ClubScheduleMemberResponse;
import com.sideteam.groupsaver.domain.club_schedule.repository.ClubScheduleMemberRepository;
import com.sideteam.groupsaver.domain.club_schedule.repository.ClubScheduleRepository;
import com.sideteam.groupsaver.domain.member.domain.Member;
import com.sideteam.groupsaver.domain.member.repository.MemberRepository;
import com.sideteam.groupsaver.global.auth.userdetails.GetAuthUserUtils;
import com.sideteam.groupsaver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.sideteam.groupsaver.global.exception.club.ClubScheduleErrorCode.CLUB_SCHEDULE_IS_FULL;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ClubScheduleMemberService {

    private final ClubScheduleRepository clubScheduleRepository;
    private final ClubScheduleMemberRepository clubScheduleMemberRepository;

    private final ClubMemberRepository clubMemberRepository;

    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public List<ClubScheduleMemberResponse> getClubScheduleMembers(Long clubScheduleId) {
        return clubScheduleMemberRepository.findAllScheduleMembersByClubScheduleId(clubScheduleId).stream()
                .map(member -> createResponseClubMember(member, clubScheduleId)).toList();
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) OR hasRole('ADMIN'))")
    public void joinSchedule(Long clubScheduleId, Long memberId) {
        ClubSchedule clubSchedule = clubScheduleRepository.findOrThrowWithReadLock(clubScheduleId);

        clubMemberRepository.throwIfMemberNotInClub(memberId, clubSchedule.getClub().getId());
        checkIfParticipationExceed(clubSchedule);

        Member member = memberRepository.getReferenceById(memberId);

        clubScheduleMemberRepository.save(ClubScheduleMember.of(clubSchedule, member));
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) OR hasRole('ADMIN'))")
    public void leaveSchedule(Long clubScheduleId, Long memberId) {
        clubScheduleMemberRepository.deleteByClubScheduleIdAndMemberId(clubScheduleId, memberId);
    }


    private void checkIfParticipationExceed(ClubSchedule clubSchedule) {
        if (clubSchedule.getClubScheduleMemberCount() != null && clubSchedule.isReachMaximumParticipation()) {
            log.warn("이미 가득찬 모임 일정! 모임일정 ID: {}", clubSchedule.getId());
            throw new BusinessException(CLUB_SCHEDULE_IS_FULL, clubSchedule.getId().toString());
        }
    }

    private ClubScheduleMemberResponse createResponseClubMember(Member member, Long clubScheduleId) {
        boolean isLeader = clubMemberRepository.isLeader(clubScheduleRepository.findClubIdById(clubScheduleId), member.getId());
        return ClubScheduleMemberResponse.from(member, isLeader, GetAuthUserUtils.getAuthUserId().equals(member.getId()));
    }

}
