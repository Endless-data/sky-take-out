package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file 文件
     * @return 文件访问路径
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file);
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新的文件名
            String objectName = UUID.randomUUID().toString() + "." + extension;
            // 上传文件到OSS
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error("文件上传失败");
        }
    }
}
