package com.jobhunt.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  String uploadFile(MultipartFile file, String directory);

  void deleteFile(String fileUrl);
}