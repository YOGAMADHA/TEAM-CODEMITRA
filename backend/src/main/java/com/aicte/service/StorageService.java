package com.aicte.service;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    String upload(String key, byte[] data, String contentType) throws IOException;
    byte[] download(String key) throws IOException;
    InputStream getInputStream(String key) throws IOException;
    void delete(String key) throws IOException;
}
