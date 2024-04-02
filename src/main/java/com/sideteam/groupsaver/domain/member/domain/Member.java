package com.sideteam.groupsaver.domain.member.domain;

import com.sideteam.groupsaver.domain.category.domain.ClubCategoryMajor;
import com.sideteam.groupsaver.domain.category.domain.JobMajor;
import com.sideteam.groupsaver.domain.common.BaseTimeEntity;
import com.sideteam.groupsaver.domain.join.domain.WantClubCategory;
import com.sideteam.groupsaver.domain.member.dto.request.MemberProfileUpdateRequest;
import com.sideteam.groupsaver.domain.notification.domain.Notification;
import com.sideteam.groupsaver.domain.report.domain.ReportUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.requireNonNullElse;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate birth;

    @Enumerated(STRING)
    private MemberGender gender;

    @Column(nullable = false)
    private String profileUrl = "https://sabuzac-bucket.s3.ap-northeast-2.amazonaws.com/user/profile_default_image/profile.png";

    @Enumerated(STRING)
    @Column(nullable = false)
    private OAuthProvider oAuthProvider;

    @Convert(converter = MemberRoleConverter.class)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(STRING)
    private JobMajor jobCategory;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<WantClubCategory> wantClubCategories = new ArrayList<>();

    @OneToMany(mappedBy = "reportedMember", fetch = FetchType.LAZY)
    private final List<ReportUser> reports = new ArrayList<>();

    @Enumerated(STRING)
    @Column(nullable = false)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<Notification> notifications = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private MemberActive activeStatus;

    @Builder
    protected Member(String password, String phoneNumber, String nickname, String email, OAuthProvider oAuthProvider, MemberRole role, JobMajor jobCategory, MemberGender gender, LocalDate birth) {
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.oAuthProvider = oAuthProvider;
        this.role = role;
        this.jobCategory = jobCategory;
        this.gender = gender;
        this.birth = birth;
        this.activeStatus = MemberActive.ACTIVE;
    }

    public List<ClubCategoryMajor> getClubCategories() {
        return wantClubCategories.stream().map(WantClubCategory::getCategoryMajor).toList();
    }

    public void update(MemberProfileUpdateRequest updateRequest) {
        this.nickname = requireNonNullElse(updateRequest.nickname(), this.nickname);
        this.birth = requireNonNullElse(updateRequest.birth(), this.birth);
        this.profileUrl = requireNonNullElse(updateRequest.profileImageUrl(), this.profileUrl);
        this.jobCategory = requireNonNullElse(updateRequest.jobCategory(), this.jobCategory);
    }


    public void updateWantClubCategory(List<WantClubCategory> categories) {
        this.wantClubCategories.addAll(categories);
    }

    public void updatePassword(PasswordEncoder encoder, String rawPassword) {
        this.password = encoder.encode(rawPassword);
    }

    public void addReport(ReportUser report) {
        this.reports.add(report);
    }

    public void suspend() {
        this.activeStatus = MemberActive.SUSPEND;
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }
}
