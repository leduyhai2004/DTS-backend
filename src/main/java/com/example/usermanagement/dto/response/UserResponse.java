package com.example.usermanagement.dto.response;

import com.example.usermanagement.enums.UserStatus;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String roleName;
    private UserStatus status;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
