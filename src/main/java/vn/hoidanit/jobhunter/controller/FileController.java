package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.uploadFile.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.utils.annotation.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder

    ) throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty.");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        boolean isValidFile = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValidFile) {
            throw new StorageException("Invalid file. Only allow: " + allowedExtensions.toString());
        }

        this.fileService.createDirectory(baseURI + folder);
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadFile, Instant.now());
        return ResponseEntity.ok().body(resUploadFileDTO);
    }
}
