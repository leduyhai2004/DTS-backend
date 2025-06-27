package com.example.usermanagement.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistedException(request.getUsername());
        }
        User user = userMapper.toEntity(request);
        //find role USER and automatically assign it
        user.setRole(roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found")));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Update only the fields from the request, preserve others

        existing.setName(request.getName());
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setAvatar(request.getAvatar());
        if (request.getRole_id() != null) {
            existing.setRole(roleRepository.findById(request.getRole_id())
                    .orElseThrow(() -> new RoleNotFoundException(request.getRole_id())));
        }
        User updatedUser = userRepository.save(existing);
        return userMapper.toResponse(updatedUser);
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
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return List.of();
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return null;
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
}
