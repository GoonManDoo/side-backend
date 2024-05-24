package com.sideteam.groupsaver.domain.club.service;

import com.sideteam.groupsaver.domain.club.domain.PublicNotiComment;
import com.sideteam.groupsaver.domain.club.domain.RedisComment;
import com.sideteam.groupsaver.domain.club.dto.response.CommentResponse;
import com.sideteam.groupsaver.domain.club.repository.RedisCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class RedisCommentService {
    private final RedisCommentRepository redisCommentRepository;

    public void createRedisComments(PublicNotiComment comment) {
        if(redisCommentRepository.existsById(comment.getPublicNotification().getId())) {
            CommentResponse response = createResponse(comment);
            RedisComment redisComment = redisCommentRepository.findById(comment.getPublicNotification().getId()).orElseThrow(IllegalArgumentException::new);
            redisComment.getCommentResponses().add(0, response);
            redisCommentRepository.save(redisComment);
        } else {
            List<CommentResponse> responses = List.of(createResponse(comment));
            RedisComment redisComment = RedisComment.of(comment.getPublicNotification().getId(), responses);
            redisCommentRepository.save(redisComment);
        }
    }

    public void createReply(PublicNotiComment reply) {
        RedisComment redisComment = redisCommentRepository.findById(reply.getPublicNotification().getId()).orElseThrow(IllegalArgumentException::new);
        CommentResponse target = findTarget(redisComment, reply.getPublicNotiComment().getId());
        target.getReplyList().add(0, createResponse(reply));
        redisCommentRepository.save(redisComment);
    }

    public void updateComment(PublicNotiComment comment) {
        if (comment.getPublicNotiComment() != null) {
            updateReply(comment);
        } else {
            RedisComment redisComment = redisCommentRepository.findById(comment.getPublicNotification().getId()).orElseThrow(IllegalArgumentException::new);
            CommentResponse target = findTarget(redisComment, comment.getId());
            target.update(comment);
            redisCommentRepository.save(redisComment);
        }
    }

    public void updateReply(PublicNotiComment reply) {
        RedisComment redisComment = redisCommentRepository.findById(reply.getPublicNotification().getId()).orElseThrow(IllegalArgumentException::new);
        CommentResponse target = findTarget(redisComment, reply.getPublicNotiComment().getId())
                .getReplyList()
                .stream()
                .filter(replyResponse -> replyResponse.getCommentId() == reply.getId())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        target.update(reply);
        redisCommentRepository.save(redisComment);
    }

    public CommentResponse createResponse(PublicNotiComment comment) {
        return CommentResponse.of(
                comment.getId(),
                comment.getCreator().getId(),
                comment.getCreator().getProfileUrl(),
                comment.getCreator().getNickname(),
                comment.getText(),
                comment.getCreatedAtLocalDateTime());
    }

    public CommentResponse findTarget(RedisComment redisComment, long commentId) {
        return redisComment.getCommentResponses().stream()
                .filter(commentResponse -> commentResponse.getCommentId() == commentId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}