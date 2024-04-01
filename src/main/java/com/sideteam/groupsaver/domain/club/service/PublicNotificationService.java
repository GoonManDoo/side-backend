package com.sideteam.groupsaver.domain.club.service;

import com.sideteam.groupsaver.domain.club.domain.Club;
import com.sideteam.groupsaver.domain.club.domain.PublicNotification;
import com.sideteam.groupsaver.domain.club.dto.request.PublicRequest;
import com.sideteam.groupsaver.domain.club.dto.response.PublicResponse;
import com.sideteam.groupsaver.domain.club.repository.ClubMemberRepository;
import com.sideteam.groupsaver.domain.club.repository.ClubRepository;
import com.sideteam.groupsaver.domain.club.repository.PublicNotificationRepository;
import com.sideteam.groupsaver.global.auth.userdetails.GetAuthUserUtils;
import com.sideteam.groupsaver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.sideteam.groupsaver.global.exception.club.ClubErrorCode.CLUB_MEMBER_DO_NOT_HAVE_PERMISSION;
import static com.sideteam.groupsaver.global.exception.club.ClubErrorCode.PUBLIC_NOTIFICATION_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class PublicNotificationService {
    private final PublicNotificationRepository repository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    public void createPublic(long clubId, PublicRequest request) {
        checkLeader(clubId);
        Club club = clubRepository.findByIdOrThrow(clubId);
        PublicNotification publicNotification = repository.save(PublicNotification.of(request.getTitle(), request.getBody(), club));
        club.addPublic(publicNotification);
    }

    public void fix(long publicId) {
        PublicNotification publicNotification = repository.findById(publicId).orElseThrow(
                () -> new BusinessException(PUBLIC_NOTIFICATION_NOT_FOUND, PUBLIC_NOTIFICATION_NOT_FOUND.getDetail()));
        checkLeader(publicNotification.getClub().getId());
        publicNotification.updateFix();
    }

    public void deletePublic(long publicId) {
        PublicNotification publicNotification = repository.findById(publicId).orElseThrow(
                () -> new BusinessException(PUBLIC_NOTIFICATION_NOT_FOUND, PUBLIC_NOTIFICATION_NOT_FOUND.getDetail()));
        checkLeader(publicNotification.getClub().getId());
        repository.delete(publicNotification);
    }

    public PublicResponse getPublicNotification(long publicId) {
        PublicNotification publicNotification = repository.findById(publicId).orElseThrow(
                () -> new BusinessException(PUBLIC_NOTIFICATION_NOT_FOUND, PUBLIC_NOTIFICATION_NOT_FOUND.getDetail()));
        return PublicResponse.of(
                publicNotification.getId(),
                publicNotification.getTitle(),
                publicNotification.getBody(),
                publicNotification.getCreator().getNickname(),
                LocalDate.from(publicNotification.getCreatedAtLocalDateTime()),
                publicNotification.isFix());
    }

    public List<PublicResponse> getPublicNotificationList(long clubId) {
        List<PublicNotification> publicNotificationList = repository.findAllByClubId(clubId);
        return PublicResponse.listOf(publicNotificationList);
    }

    @Transactional(readOnly = true)
    private void checkLeader(long clubId) {
        long memberId = GetAuthUserUtils.getAuthUserId();
        if (!clubMemberRepository.isLeader(clubId, memberId)) {
            throw new BusinessException(CLUB_MEMBER_DO_NOT_HAVE_PERMISSION, CLUB_MEMBER_DO_NOT_HAVE_PERMISSION.getDetail());
        }
    }
}
