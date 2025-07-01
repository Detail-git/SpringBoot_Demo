package com.sky.service.impl;

import com.sky.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${sky.file.local.storage-path}")
    private String storagePath;

    @Value("${sky.file.local.server-url}")
    private String serverUrl;

    @Override
    public String upload(MultipartFile file) throws IOException {
        // 确保存储目录存在
        File dir = new File(storagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名，避免冲突
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件到本地目录
        File dest = new File(storagePath + uniqueFilename);
        file.transferTo(dest);

        // 返回可访问的URL路径
        return serverUrl + uniqueFilename;
    }
}