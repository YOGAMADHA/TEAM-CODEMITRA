package com.aicte.service.impl;

import com.aicte.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

    @Value("${app.storage.path:./uploads}")
    private String storagePathStr;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(storagePathStr).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            log.info("Storage directory initialized at: {}", rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String upload(String key, byte[] data, String contentType) throws IOException {
        Path targetPath = rootLocation.resolve(key).normalize();
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Saved file key: {} ({} bytes)", key, data.length);
        return key;
    }

    @Override
    public byte[] download(String key) throws IOException {
        Path targetPath = rootLocation.resolve(key).normalize();
        if (!Files.exists(targetPath)) {
            throw new IOException("File not found for key: " + key);
        }
        return Files.readAllBytes(targetPath);
    }

    @Override
    public InputStream getInputStream(String key) throws IOException {
        Path targetPath = rootLocation.resolve(key).normalize();
        if (!Files.exists(targetPath)) {
            throw new IOException("File not found for key: " + key);
        }
        return Files.newInputStream(targetPath);
    }

    @Override
    public void delete(String key) throws IOException {
        Path targetPath = rootLocation.resolve(key).normalize();
        Files.deleteIfExists(targetPath);
        log.info("Deleted file key: {}", key);
    }
}
