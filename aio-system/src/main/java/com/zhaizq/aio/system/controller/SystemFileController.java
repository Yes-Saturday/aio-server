package com.zhaizq.aio.system.controller;

import com.zhaizq.aio.common.annotation.Uncheck;
import com.zhaizq.aio.system.mapper.entity.SystemFile;
import com.zhaizq.aio.system.service.SystemFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/system/file")
public class SystemFileController extends BaseController {
    @Autowired
    private SystemFileService systemFileService;

    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        SystemFile systemFile = systemFileService.saveFile(file.getOriginalFilename(), file.getBytes(), getLogin().getId());
        return success(systemFile.getFileUuid());
    }

    @Uncheck(verify = false)
    @RequestMapping("/download")
    public ResponseEntity<?> download(String uuid) throws IOException {
        SystemFile systemFile = systemFileService.query(uuid);
        File file = systemFile != null ? systemFileService.getFile(systemFile.getFilePath()) : null;

        if (file == null || !file.exists())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + URLEncoder.encode(systemFile.getFileName(), "UTF-8"))
                .body(new FileSystemResource(file));
    }
}
