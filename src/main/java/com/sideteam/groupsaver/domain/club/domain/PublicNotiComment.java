package com.sideteam.groupsaver.domain.club.domain;

import com.sideteam.groupsaver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicNotiComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicNotification_Id")
    private PublicNotification publicNotification;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicNotiComment_Id")
    private PublicNotiComment publicNotiComment;
    @OneToMany(mappedBy = "publicNotiComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublicNotiComment> replyList = new ArrayList<>();

    @Builder
    public PublicNotiComment(String text, PublicNotification publicNotification, PublicNotiComment publicNotiComment) {
        this.text = text;
        this.publicNotification = publicNotification;
        this.publicNotiComment = publicNotiComment;
    }

    public static PublicNotiComment of(String text, PublicNotification publicNotification, PublicNotiComment publicNotiComment) {
        return PublicNotiComment.builder()
                .text(text)
                .publicNotification(publicNotification)
                .publicNotiComment(publicNotiComment)
                .build();
    }

    public void updateText(String text) {
        this.text = text;
    }

    public void addReply(PublicNotiComment reply) {
        this.replyList.add(reply);
    }

    public void deleteText() {
        this.text = "삭제된 댓글입니다";
    }
}