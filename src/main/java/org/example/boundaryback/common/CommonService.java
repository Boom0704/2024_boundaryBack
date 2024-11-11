package org.example.boundaryback.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class CommonService {

  private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

  @Value("${app.upload.path}")
  private String uploadPath;

  @PostConstruct
  public void initializeDirectory() {
    File directory = new File(uploadPath);
    if (!directory.exists()) {
      boolean created = directory.mkdirs();
      if (created) {
        logger.info("Upload directory created at: {}", directory.getAbsolutePath());
      } else {
        logger.error("Failed to create upload directory.");
      }
    }
  }

  public String saveFile(MultipartFile file) {
    try {
      String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
      File destinationFile = new File(uploadPath + uniqueFileName);
      try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
        fos.write(file.getBytes());
      }
      return uniqueFileName;
    } catch (IOException e) {
      logger.error("Failed to save file: {}", file.getOriginalFilename(), e);
      return null;
    }
  }

  public ResponseEntity<Resource> getFileAsResource(String filename) {
    File file = new File(uploadPath + filename);
    if (!file.exists()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Resource resource = new FileSystemResource(file);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
        .body(resource);
  }
}
