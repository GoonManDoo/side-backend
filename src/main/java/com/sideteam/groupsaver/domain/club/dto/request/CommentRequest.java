package com.sideteam.groupsaver.domain.club.dto.request;

import lombok.Getter;

@Getter
public class CommentRequest {
    private long publicId;
    private long commentId;
    private String text;
}
