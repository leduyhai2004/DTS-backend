package com.example.usermanagement.service.impl;

import com.example.usermanagement.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;


@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        // images/abc.png
        String filePath = path + File.pathSeparator + fileName;

        // Create directories if they do not exist
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        // copy file content to path

        CompletableFuture.runAsync(() -> {
            try {
                Files.copy(file.getInputStream(), Paths.get(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.pathSeparator + fileName;
        return new FileInputStream(fullPath);
    }
}
