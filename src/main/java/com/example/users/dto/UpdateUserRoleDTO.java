package com.example.users.dto;

public class UpdateUserRoleDTO {
    private String role;

    public UpdateUserRoleDTO() {
    }

    public UpdateUserRoleDTO(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
