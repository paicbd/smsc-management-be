package com.smsc.management.utils;

import java.time.LocalDateTime;

import com.paicbd.smsc.utils.Generated;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.context.SecurityContextHolder;

import com.smsc.management.app.user.model.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Generated
@MappedSuperclass
public class EntityBase {
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by_id")
    private Integer createdById;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    @LastModifiedBy
    @Column(name = "updated_by_id")
    private Integer updatedById;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users userUpdate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.createdById = getCurrentUserId();
        this.updatedById = getCurrentUserId();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedById = getCurrentUserId();
    }

    // Method to get the current user id from the security context
    private Integer getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ((Users) principal).getId();
        } catch (Exception e) {
            log.error("User active session was not found -> {}", e.getMessage());
        }
        return null;
    }
}
