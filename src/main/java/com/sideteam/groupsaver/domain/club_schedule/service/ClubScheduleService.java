package com.sideteam.groupsaver.domain.club_schedule.service;

import com.sideteam.groupsaver.domain.club.domain.Club;
import com.sideteam.groupsaver.domain.club.repository.ClubRepository;
import com.sideteam.groupsaver.domain.club_schedule.domain.ClubSchedule;
import com.sideteam.groupsaver.domain.club_schedule.dto.request.ClubScheduleRequest;
import com.sideteam.groupsaver.domain.club_schedule.dto.response.ClubScheduleResponse;
import com.sideteam.groupsaver.domain.club_schedule.repository.ClubScheduleRepository;
import com.sideteam.groupsaver.domain.notification.service.NotificationService;
import com.sideteam.groupsaver.global.auth.userdetails.GetAuthUserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ClubScheduleService {

    private final ClubRepository clubRepository;
    private final ClubScheduleRepository clubScheduleRepository;
    private final NotificationService notificationService;
    private final ClubScheduleMemberService clubScheduleMemberService;

    @Transactional(readOnly = true)
    public ClubScheduleResponse getSchedule(Long clubScheduleId) {
        ClubSchedule clubSchedule = clubScheduleRepository.findOrThrowWithReadLock(clubScheduleId);
        return ClubScheduleResponse.from(clubSchedule);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) " +
            " AND @clubMemberRepository.existsByClubIdAndMemberId(#clubId, #memberId) " +
            " OR hasRole('ADMIN'))")
    public Page<ClubScheduleResponse> getScheduleByClubId(Long clubId, Long memberId, Pageable pageable) {
        Page<ClubSchedule> clubSchedulePage = clubScheduleRepository.findAllByClubId(clubId, pageable);

        List<ClubScheduleResponse> clubScheduleResponses = clubSchedulePage.getContent().stream()
                .map(ClubScheduleResponse::from)
                .toList();
        return new PageImpl<>(clubScheduleResponses, clubSchedulePage.getPageable(), clubSchedulePage.getTotalElements());
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) " +
            " AND @clubMemberRepository.existsByClubIdAndMemberId(#clubId, #memberId) " +
            " OR hasRole('ADMIN'))")
    public ClubScheduleResponse createSchedule(Long clubId, ClubScheduleRequest clubScheduleRequest, Long memberId) {
        Club club = clubRepository.getReferenceById(clubId);
        ClubSchedule clubSchedule = clubScheduleRepository.save(ClubSchedule.of(club, clubScheduleRequest));
        clubScheduleMemberService.joinSchedule(clubSchedule.getId(), memberId);
        notificationService.createNewSchedule(club.getId(), clubSchedule.getId(), club.getMainImage(), GetAuthUserUtils.getAuthUserId());
        return ClubScheduleResponse.from(clubSchedule);
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) " +
            " AND @clubScheduleRepository.isCreatorOrLeader(#memberId, #clubScheduleId) " +
            " OR hasRole('ADMIN'))")
    public ClubScheduleResponse updateSchedule(Long clubScheduleId, ClubScheduleRequest clubScheduleRequest, Long memberId) {
        ClubSchedule clubSchedule = clubScheduleRepository.getReferenceById(clubScheduleId);
        clubSchedule.update(clubScheduleRequest);
        return ClubScheduleResponse.from(clubSchedule);
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) " +
            " AND @clubScheduleRepository.isCreatorOrLeader(#memberId, #clubScheduleId) " +
            " OR hasRole('ROLE_ADMIN'))")
    public void deleteSchedule(Long clubScheduleId, Long memberId) {
        clubScheduleRepository.deleteById(clubScheduleId);
    }

}
