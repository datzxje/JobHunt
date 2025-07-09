package com.jobhunt.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

  private final AmazonS3 s3Client;

  @Value("${cloudflare.r2.bucket}")
  private String bucketName;

  @GetMapping("/download")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileUrl) {
    try {
      String key = extractKeyFromUrl(fileUrl);

      log.info("Downloading file with key: {}", key);

      GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
      S3Object s3Object = s3Client.getObject(getObjectRequest);

      String filename = key.substring(key.lastIndexOf('/') + 1);

      String contentType = s3Object.getObjectMetadata().getContentType();
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
      headers.add(HttpHeaders.CONTENT_TYPE, contentType);

      InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(s3Object.getObjectMetadata().getContentLength())
          .contentType(MediaType.parseMediaType(contentType))
          .body(resource);

    } catch (Exception e) {
      log.error("Error downloading file: {}", e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/view")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<InputStreamResource> viewFile(@RequestParam String fileUrl) {
    try {
      String key = extractKeyFromUrl(fileUrl);

      log.info("Viewing file with key: {}", key);

      GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
      S3Object s3Object = s3Client.getObject(getObjectRequest);

      String filename = key.substring(key.lastIndexOf('/') + 1);

      String contentType = s3Object.getObjectMetadata().getContentType();
      if (contentType == null) {
        contentType = "application/pdf";
      }

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);
      headers.add(HttpHeaders.CONTENT_TYPE, contentType);

      InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(s3Object.getObjectMetadata().getContentLength())
          .contentType(MediaType.parseMediaType(contentType))
          .body(resource);

    } catch (Exception e) {
      log.error("Error viewing file: {}", e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }
  }

  private String extractKeyFromUrl(String fileUrl) {
    try {
      String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);

      if (decodedUrl.contains(bucketName)) {
        int bucketIndex = decodedUrl.indexOf(bucketName);
        if (bucketIndex != -1) {
          String afterBucket = decodedUrl.substring(bucketIndex + bucketName.length());
          return afterBucket.startsWith("/") ? afterBucket.substring(1) : afterBucket;
        }
      }

      if (decodedUrl.contains("://")) {
        String[] parts = decodedUrl.split("://");
        if (parts.length > 1) {
          String[] domainParts = parts[1].split("/", 2);
          if (domainParts.length > 1) {
            return domainParts[1];
          }
        }
      }

      throw new IllegalArgumentException("Cannot extract key from URL: " + fileUrl);

    } catch (Exception e) {
      log.error("Error extracting key from URL: {}", fileUrl, e);
      throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
    }
  }
}