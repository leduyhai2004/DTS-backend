package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.request.CreateUserRequest;
import com.example.usermanagement.dto.request.UpdateUserRequest;
import com.example.usermanagement.dto.response.UserResponse;
import com.example.usermanagement.entities.User;
import com.example.usermanagement.enums.UserStatus;
import com.example.usermanagement.exception.RoleNotFoundException;
import com.example.usermanagement.exception.UserExistedException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.repositories.RoleRepository;
import com.example.usermanagement.repositories.UserRepository;
import com.example.usermanagement.service.FileService;
import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${project.images}")
    private String path;


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final FileService fileService;


    @Override
    public User createUser(CreateUserRequest request, MultipartFile imageFile) throws IOException {

        if(Files.exists(Paths.get(path + File.separator + imageFile.getOriginalFilename()))) {
            throw new FileAlreadyExistsException("File already exists! Please give another file!");
        }

        String uploadedFileName = fileService.uploadFile(path, imageFile);
        String userAvatarUrl = baseUrl + "/api/files/" + uploadedFileName;

        request.setUserAvatar(uploadedFileName);
        request.setUserAvatarUrl(userAvatarUrl);


        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistedException(request.getUsername());
        }
        User user = userMapper.toEntity(request);


        user.setRole(roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found")));
        return  userRepository.save(user);
    }

//    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public User updateUser(Long id, UpdateUserRequest request, MultipartFile imageFile) throws IOException {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        existing.setName(request.getName());
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        if (request.getRole_id() != null) {
            existing.setRole(roleRepository.findById(request.getRole_id())
                    .orElseThrow(() -> new RoleNotFoundException(request.getRole_id())));
        }

        String userAvatar = existing.getUserAvatar();
        String userAvatarUrl = existing.getUserAvatarUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                try {
                    Files.deleteIfExists(Paths.get(path + File.separator + existing.getUserAvatar()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            userAvatar = fileService.uploadFile(path, imageFile);
            userAvatarUrl = baseUrl + "/api/files/" + userAvatar;
        }
        existing.setUserAvatar(userAvatar);
        existing.setUserAvatarUrl(userAvatarUrl);
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(UserStatus.INACTIVE);
        //save user
        userRepository.save(user);
    }

    @Override
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        // Set status to ACTIVE
        user.setStatus(UserStatus.ACTIVE);
        // Save the updated user
        User activatedUser = userRepository.save(user);
        return userMapper.toResponse(activatedUser);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }


    @Override
    public Page<UserResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        // Tạo đối tượng Sort
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        // pageNo bắt đầu từ 0
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        // Create sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Create sort object
        Sort sort = Sort.by(direction, sortBy);

        // Create pageable object
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Search users with pagination and sorting
        Page<User> userPage = userRepository.findUsersWithSearch(keyword, pageable);

        // Convert to response DTOs
        return userPage.map(userMapper::toResponse);
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getUsername().equals(currentUsername);
    }

}
