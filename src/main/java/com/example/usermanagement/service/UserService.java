package com.example.usermanagement.service;

import com.example.usermanagement.dto.request.CreateUserRequest;
import com.example.usermanagement.dto.request.UpdateUserRequest;
import com.example.usermanagement.dto.response.UserResponse;
import com.example.usermanagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest request, MultipartFile imageFile) throws IOException;
    UserResponse createUser1(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    UserResponse activateUser(Long id);
    User getUserById(Long id);
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<UserResponse> searchUsers(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
}
