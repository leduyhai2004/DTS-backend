package com.example.usermanagement.service;

import com.example.usermanagement.dto.request.CreateUserRequest;
import com.example.usermanagement.dto.request.UpdateUserRequest;
import com.example.usermanagement.dto.response.UserResponse;
import com.example.usermanagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    User createUser(CreateUserRequest request, MultipartFile imageFile) throws IOException;
    User updateUser(Long id, UpdateUserRequest request, MultipartFile imageFile) throws IOException;
    void deleteUser(Long id);
    UserResponse activateUser(Long id);
    User getUserById(Long id);
    Page<UserResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<UserResponse> searchUsers(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
    boolean isCurrentUser(Long userId);
}
