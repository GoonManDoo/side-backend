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
public class PublicNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String body;
    private boolean fix;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;
    @OneToMany(mappedBy = "publicNotification", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublicNotiComment> commentList = new ArrayList<>();

    @Builder
    public PublicNotification(String title, String body, boolean fix, Club club) {
        this.title = title;
        this.body = body;
        this.fix = fix;
        this.club = club;
    }

    public static PublicNotification of(String title, String body, Club club) {
        return PublicNotification.builder()
                .title(title)
                .body(body)
                .fix(false)
                .club(club)
                .build();
    }

    public void updateFix() {
        if (!this.fix) {
            this.fix = true;
        } else {
            this.fix = false;
        }
    }

    public void addComment(PublicNotiComment publicNotiComment) {
        this.commentList.add(publicNotiComment);
    }
}
