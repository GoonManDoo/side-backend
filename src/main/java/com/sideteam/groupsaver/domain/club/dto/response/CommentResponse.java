package com.sideteam.groupsaver.domain.club.dto.response;

import com.sideteam.groupsaver.domain.club.domain.PublicNotiComment;
import com.sideteam.groupsaver.global.util.ProjectTimeFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponse {
    private long commentId;
    private long memberId;
    private String profileUrl;
    private String nickname;
    private String text;
    private LocalDateTime time;
    private boolean me;
    private List<CommentResponse> replyList = new ArrayList<>();

    @Builder
    private CommentResponse(long commentId, long memberId, String profileUrl, String nickname, String text, LocalDateTime time, boolean me) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.profileUrl = profileUrl;
        this.nickname = nickname;
        this.text = text;
        this.time = time;
        this.me = me;
    }

    public static CommentResponse of(long commentId, long memberId,String profileUrl, String nickname, String text, LocalDateTime time) {
        return CommentResponse.builder()
                .commentId(commentId)
                .memberId(memberId)
                .profileUrl(profileUrl)
                .nickname(nickname)
                .text(text)
                .time(time)
                .me(false)
                .build();
    }

    public void update(PublicNotiComment comment) {
        this.profileUrl = comment.getCreator().getProfileUrl();
        this.nickname = comment.getCreator().getNickname();
        this.text = comment.getText();
        this.time = LocalDateTime.ofInstant(comment.getUpdateAt(), ProjectTimeFormat.SERVER_ZONE_ID);
    }

    public void isMe(boolean isMe) {
        this.me = isMe;
    }
}
