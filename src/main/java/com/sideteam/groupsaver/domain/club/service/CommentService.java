package com.sideteam.groupsaver.domain.club.service;

import com.sideteam.groupsaver.domain.club.domain.PublicNotiComment;
import com.sideteam.groupsaver.domain.club.domain.PublicNotification;
import com.sideteam.groupsaver.domain.club.dto.request.CommentRequest;
import com.sideteam.groupsaver.domain.club.dto.response.CommentResponse;
import com.sideteam.groupsaver.domain.club.repository.PublicNotiCommentRepository;
import com.sideteam.groupsaver.domain.club.repository.PublicNotificationRepository;
import com.sideteam.groupsaver.domain.club.repository.RedisCommentRepository;
import com.sideteam.groupsaver.global.auth.userdetails.GetAuthUserUtils;
import com.sideteam.groupsaver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sideteam.groupsaver.global.exception.member.MemberErrorCode.DO_NOT_HAVE_PERMISSION;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final PublicNotificationRepository publicNotificationRepository;
    private final PublicNotiCommentRepository commentRepository;
    private final RedisCommentRepository redisCommentRepository;
    private final RedisCommentService redisCommentService;

    public void createComment(CommentRequest request) {
        PublicNotification publicNotification = publicNotificationRepository.findById(request.getPublicId()).orElseThrow(IllegalArgumentException::new);
        PublicNotiComment comment = PublicNotiComment.of(request.getText(), publicNotification, null);
        publicNotification.addComment(comment);
        commentRepository.save(comment);
        redisCommentService.createRedisComments(comment);
    }

    public void update(CommentRequest request) {
        PublicNotiComment comment = commentRepository.findById(request.getCommentId()).orElseThrow(IllegalArgumentException::new);
        checkIsMe(comment.getCreator().getId());
        comment.updateText(request.getText());
        redisCommentService.updateComment(comment);
    }

    public void delete(CommentRequest request) {
        PublicNotiComment comment = commentRepository.findById(request.getCommentId()).orElseThrow(IllegalArgumentException::new);
        checkIsMe(comment.getCreator().getId());
        comment.deleteText();
        redisCommentService.updateComment(comment);
    }

    private void checkIsMe(long memberId) {
        if(!Objects.equals(memberId, GetAuthUserUtils.getAuthUserId())) {
            throw new BusinessException(DO_NOT_HAVE_PERMISSION, DO_NOT_HAVE_PERMISSION.getDetail());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(CommentRequest request) {
        return redisCommentRepository.findById(request.getPublicId()).orElseThrow(IllegalArgumentException::new)
                .getCommentResponses()
                .stream()
                .map(this::isMe)
                .collect(Collectors.toList());
    }

    private CommentResponse isMe(CommentResponse response) {
        long currentMember = GetAuthUserUtils.getAuthUserId();
        response.isMe(response.getMemberId() == currentMember);
        response.getReplyList().forEach(reply -> reply.isMe(reply.getMemberId() == currentMember));
        return response;
    }

    public void createReply(CommentRequest request) {
        PublicNotiComment comment = commentRepository.findById(request.getCommentId()).orElseThrow(IllegalArgumentException::new);
        PublicNotiComment reply = PublicNotiComment.of(request.getText(), comment.getPublicNotification(), comment);
        comment.addReply(reply);
        comment.getPublicNotification().addComment(reply);
        commentRepository.save(reply);
        redisCommentService.createReply(reply);
    }
}
