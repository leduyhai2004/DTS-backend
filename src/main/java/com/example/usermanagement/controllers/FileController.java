package com.example.usermanagement.controllers;

import com.example.usermanagement.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    @Value("${project.images}")
    private String path;

    // Define endpoints for file upload and retrieval here
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            String uploadedFileName = fileService.uploadFile(path, file);
            return ResponseEntity.ok("File uploaded successfully: " + uploadedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.ALL_VALUE)
    public void getFile(@PathVariable String fileName, HttpServletResponse response) {
        try {
            InputStream resource = fileService.getResourceFile(path, fileName);

            // Tự động dò loại content (jpeg, png, gif, webp, ...)
            String contentType = Files.probeContentType(
                    java.nio.file.Paths.get(path, fileName));
            if (contentType == null) {
                contentType = "application/octet-stream"; // fallback
            }

            response.setContentType(contentType);
            StreamUtils.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
