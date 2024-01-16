package com.sideteam.groupsaver.domain.club.service;

import com.sideteam.groupsaver.domain.club.domain.ClubMember;
import com.sideteam.groupsaver.domain.club.domain.ClubMemberRole;
import com.sideteam.groupsaver.domain.club.repository.ClubMemberRepository;
import com.sideteam.groupsaver.domain.club.repository.ClubRepository;
import com.sideteam.groupsaver.domain.member.repository.MemberRepository;
import com.sideteam.groupsaver.global.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static com.sideteam.groupsaver.global.exception.club.ClubErrorCode.CLUB_MEMBER_ALREADY_EXIST;
import static com.sideteam.groupsaver.global.exception.club.ClubErrorCode.CLUB_MEMBER_IS_FULL;

@Service
@RequiredArgsConstructor
@Transactional
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;


    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) OR hasRole('ADMIN'))")
    public void joinClub(Long clubId, Long memberId, ClubMemberRole role) {
        if (clubMemberRepository.existsByClubIdAndMemberId(clubId, memberId)) {
            throw new BusinessException(CLUB_MEMBER_ALREADY_EXIST, "이미 존재하는 인원 : " + memberId);
        }

        if (clubMemberRepository.hasExceededMemberLimit(clubId)) {
            throw new BusinessException(CLUB_MEMBER_IS_FULL, "인원이 가득찬 모임 : " + clubId);
        }

        ClubMember clubMember = ClubMember.of(
                clubRepository.getReferenceById(clubId),
                memberRepository.getReferenceById(memberId),
                role);
        clubMemberRepository.save(clubMember);
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) OR hasRole('ADMIN'))")
    public void joinClub(Long clubId, Long memberId) {
        joinClub(clubId, memberId, ClubMemberRole.MEMBER);
    }

    @PreAuthorize("isAuthenticated() AND (( #memberId.toString() == principal.username ) " +
            " AND @clubMemberRepository.isLeader(#clubId, #memberId) " +
            " OR hasRole('ADMIN'))")
    public void leaveClub(Long clubId, Long memberId) {
        clubMemberRepository.deleteByClubIdAndMemberId(clubId, memberId);
    }

}
