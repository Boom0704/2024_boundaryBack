package org.example.boundaryback.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController {

  private final CommonService commonService;

  @Autowired
  public CommonController(CommonService commonService) {
    this.commonService = commonService;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    String uploadPath = commonService.saveFile(file);
    if (uploadPath != null) {
      // 동적 URL 생성
      String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
          .path("/common/file/")
          .path(uploadPath)
          .toUriString();
      return new ResponseEntity<>(fileUrl, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/upload-multiple")
  public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
    List<String> fileUrls = new ArrayList<>();
    for (MultipartFile file : files) {
      String uploadPath = commonService.saveFile(file);
      if (uploadPath != null) {
        // 동적 URL 생성
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/common/file/")
            .path(uploadPath)
            .toUriString();
        fileUrls.add(fileUrl);
      }
    }
    return new ResponseEntity<>(fileUrls, HttpStatus.OK);
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/file/{filename}")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    return commonService.getFileAsResource(filename);
  }
}
