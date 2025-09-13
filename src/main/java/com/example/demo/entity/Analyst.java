package com.example.demo.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analysts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analyst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 外鍵對應 users.id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_img", length = 255)
    private String profileImgPath;

    @Column(name = "certificate_img", length = 255)
    private String certificateImgPath;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean verified = false;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "analyst_specialties",
        joinColumns = @JoinColumn(name = "analyst_id"),
        inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties = new HashSet<>();
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }
    public void setProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public String getCertificateImgPath() {
        return certificateImgPath;
    }
    public void setCertificateImgPath(String certificateImgPath) {
        this.certificateImgPath = certificateImgPath;
    }

    public Boolean getVerified() {
        return verified;
    }
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }
    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

}
