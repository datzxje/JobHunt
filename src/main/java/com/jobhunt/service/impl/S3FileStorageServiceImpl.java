package com.jobhunt.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jobhunt.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileStorageServiceImpl implements FileStorageService {

  private final AmazonS3 s3Client;

  @Value("${cloudflare.r2.bucket}")
  private String bucketName;

  @Override
  public String uploadFile(MultipartFile file, String directory) {
    String fileName = generateUniqueFileName(file.getOriginalFilename());
    String key = directory + "/" + fileName;

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(file.getContentType());
    metadata.setContentLength(file.getSize());

    try {
      PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
      s3Client.putObject(putObjectRequest);
      return s3Client.getUrl(bucketName, key).toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload file to R2", e);
    }
  }

  @Override
  public void deleteFile(String fileUrl) {
    String key = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
    s3Client.deleteObject(bucketName, key);
  }

  private String generateUniqueFileName(String originalFileName) {
    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    return UUID.randomUUID().toString() + extension;
  }
}