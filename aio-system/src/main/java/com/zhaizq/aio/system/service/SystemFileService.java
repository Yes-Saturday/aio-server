package com.zhaizq.aio.system.service;

import com.zhaizq.aio.common.BusinessException;
import com.zhaizq.aio.common.utils.DateUtil;
import com.zhaizq.aio.common.utils.DigestUtil;
import com.zhaizq.aio.common.utils.StringUtil;
import com.zhaizq.aio.system.mapper.SystemFileMapper;
import com.zhaizq.aio.system.mapper.entity.SystemFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class SystemFileService {
    @Value("${project.config.file.path:./files}")
    private String basePath;
    @Autowired
    private SystemFileMapper systemFileMapper;

    public SystemFile query(String uuid) {
        return systemFileMapper.lambdaQuery().eq(SystemFile::getFileUuid, uuid).last("LIMIT 1").one();
    }

    public File getFile(String filePath) {
        File file = new File(basePath + File.separator + filePath);
        return file.exists() ? file : null;
    }

    @Transactional(rollbackFor = Exception.class)
    public SystemFile saveFile(String fileName, byte[] bytes, Integer operator) throws IOException {
        // 判断文件MD5值与文件大小
        String fileMd5 = DigestUtil.md5AsHex(bytes);
        SystemFile entity = systemFileMapper.lambdaQuery().eq(SystemFile::getFileMd5, fileMd5).eq(SystemFile::getFileSize, bytes.length).last("LIMIT 1").one();
        if (entity != null) return entity;

        entity = new SystemFile();
        entity.setFileUuid(StringUtil.uuid());
        entity.setFileName(fileName);
        entity.setFilePath(String.format("/upload/%s/%s%s", DateUtil.format("yyyyMMdd"), entity.getFileUuid(), entity.getSuffix()));
        entity.setFileSize((long) bytes.length);
        entity.setFileMd5(fileMd5);
        entity.setCreateUid(operator);
        entity.setCreateTime(new Date());
        systemFileMapper.insert(entity);

        // 保存文件
        this.write(entity.getFilePath(), bytes);
        return entity;
    }

    private void write(String filePath, byte[] bytes) throws IOException {
        if (StringUtil.isEmpty(filePath))
            throw new BusinessException("文件路径不合法");

        File file = new File(basePath + File.separator + filePath);
        boolean ignore = file.getParentFile().mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }
}