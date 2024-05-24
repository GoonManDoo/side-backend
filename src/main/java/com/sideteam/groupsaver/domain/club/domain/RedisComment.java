package com.sideteam.groupsaver.domain.club.domain;

import com.sideteam.groupsaver.domain.club.dto.response.CommentResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "club_noti_comment")
@Getter
public class RedisComment {
    @Id
    private long publicNotificationId;
    private List<CommentResponse> commentResponses;

    @Builder
    private RedisComment(long publicNotificationId, List<CommentResponse> commentResponses) {
        this.publicNotificationId = publicNotificationId;
        this.commentResponses = commentResponses;
    }

    public static RedisComment of(long publicNotificationId, List<CommentResponse> commentResponses) {
        return RedisComment.builder()
                .publicNotificationId(publicNotificationId)
                .commentResponses(commentResponses)
                .build();
    }
}