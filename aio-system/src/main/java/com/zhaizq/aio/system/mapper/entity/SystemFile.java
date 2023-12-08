package com.zhaizq.aio.system.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhaizq.aio.common.utils.StringUtil;
import lombok.Data;

import java.util.Date;

@Data
@TableName("system_file")
public class SystemFile {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("file_uuid")
    private String fileUuid;
    @TableField("file_name")
    private String fileName;
    @TableField("file_path")
    private String filePath;
    @TableField("file_size")
    private Long fileSize;
    @TableField("file_md5")
    private String fileMd5;
    @TableField("create_uid")
    private Integer createUid;
    @TableField("create_time")
    private Date createTime;

    public String getSuffix() {
        if (StringUtil.isEmpty(fileName) || fileName.lastIndexOf(".") == -1)
            return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }
}