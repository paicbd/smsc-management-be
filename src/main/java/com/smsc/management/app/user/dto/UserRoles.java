package com.smsc.management.app.user.dto;

import lombok.Getter;

@Getter
public enum UserRoles {
    ROOT("ROOT") , ADMIN("ADMIN"), USER("USER");

    private final String roleName;

    UserRoles(String roleName) {
        this.roleName = roleName;
    }
}
