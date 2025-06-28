package com.example.usermanagement.controllers;

import com.example.usermanagement.dto.request.CreateUserRequest;
import com.example.usermanagement.dto.request.UpdateUserRequest;
import com.example.usermanagement.dto.response.UserResponse;
import com.example.usermanagement.entities.User;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @PostMapping
    public ResponseEntity<User> createUser(@RequestPart("request") CreateUserRequest request,
                                                   @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok(userService.createUser(request, imageFile));
    }

    @PostMapping("/test")
    public ResponseEntity<UserResponse> createUser1(@Valid @RequestBody CreateUserRequest request ) {
        return ResponseEntity.ok(userService.createUser1(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(userService.getAllUsers(pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(userService.searchUsers(keyword, pageNo, pageSize, sortBy, sortDir));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
    //active a user
    @PutMapping("/{id}/active")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        UserResponse activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(activatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
//    @GetMapping(path = "/{id}/image")
//    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        byte[] imageFile = user.getImageData();
//        return ResponseEntity.ok()
//                .contentType(MediaType.valueOf(user.getImageType()))
//                .body(imageFile);
//    }
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getUserImage(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(user.getImageData());

        return ResponseEntity.ok()
                .contentLength(user.getImageData().length)
                .contentType(MediaType.parseMediaType(user.getImageType()))
                .body(resource);
    }


}




