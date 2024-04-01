package com.sideteam.groupsaver.domain.club.domain;

import com.sideteam.groupsaver.domain.category.domain.ClubCategory;
import com.sideteam.groupsaver.domain.category.domain.ClubCategoryMajor;
import com.sideteam.groupsaver.domain.category.domain.ClubCategorySub;
import com.sideteam.groupsaver.domain.club.dto.request.ClubRequest;
import com.sideteam.groupsaver.domain.common.BaseEntity;
import com.sideteam.groupsaver.domain.location.domain.Location;
import com.sideteam.groupsaver.domain.report.domain.ReportClub;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "club",
        indexes = {
                @Index(name = "idx_random_id", columnList = "randomId")
        }
)
@Entity
public class Club extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ClubType type;

    @Column(name = "main_image")
    private String mainImage;

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;
    // 장단기 모임 기간의 설정칸이 없기에 보류
    //private String period;
    private LocalDateTime startAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ClubActivityType activityType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;
    private String locationDetail;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ClubCategory category;

    @Enumerated(value = EnumType.STRING)
    private ClubCategoryMajor categoryMajor;

    @Enumerated(value = EnumType.STRING)
    private ClubCategorySub categorySub;

    @Builder.Default
    @Column(nullable = false)
    private Integer randomId = (int) (Math.random() * 100_000_000);

    @Column(nullable = false)
    private Integer memberMaxNumber;

    @Formula("(SELECT COUNT(*) FROM club_member cm WHERE cm.club_id = id AND cm.status = 'ACTIVITY')")
    private int memberCurrentNumber;

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private final List<ClubMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private final List<ReportClub> reports = new ArrayList<>();

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private final List<PublicNotification> publicNotifications = new ArrayList<>();


    public static Club of(ClubRequest clubRequest) {
        ClubCategoryMajor categoryMajor = clubRequest.getMajor();
        return Club.builder()
                .name(clubRequest.name())
                .description(clubRequest.description())
                .memberMaxNumber(clubRequest.memberMaxNumber())
                .memberCurrentNumber(1)
                .startAt(clubRequest.startAt())
                .mainImage(clubRequest.mainImage())
                .category(categoryMajor.getCategory())
                .categoryMajor(categoryMajor)
                .categorySub(clubRequest.categorySub())
                .type(clubRequest.type())
                .activityType(clubRequest.activityType())
                .build();
    }

    public static Club of(ClubRequest clubRequest, Location location) {
        Club club = of(clubRequest);
        if (ClubActivityType.ONLINE.equals(club.activityType)) { // 온라인 모임은 위치 정보 저장을 생략
            return club;
        }
        club.location = location;
        club.locationDetail = clubRequest.locationDetail();
        return club;
    }


    public void update(ClubRequest clubRequest) {
        this.name = requireNonNullElse(clubRequest.name(), this.name);
        this.memberMaxNumber = requireNonNullElse(clubRequest.memberMaxNumber(), this.memberMaxNumber);
        this.type = requireNonNullElse(clubRequest.type(), this.type);
        this.description = requireNonNullElse(clubRequest.description(), this.description);
        this.mainImage = requireNonNullElse(clubRequest.mainImage(), this.mainImage);
        this.startAt = requireNonNullElse(clubRequest.startAt(), this.startAt);
        this.category = requireNonNullElse(clubRequest.getMajor().getCategory(), this.category);
        this.categoryMajor = requireNonNullElse(clubRequest.categoryMajor(), this.categoryMajor);
        this.categorySub = requireNonNullElse(clubRequest.categorySub(), this.categorySub);
        this.activityType = requireNonNullElse(clubRequest.activityType(), this.activityType);
    }


    public void updateLocation(Location location) {
        this.location = requireNonNullElse(location, this.location);
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void addReport(ReportClub reportClub) {
        this.reports.add(reportClub);
    }

    public void addPublic(PublicNotification publicNotification) {
        this.publicNotifications.add(publicNotification);
    }
}
