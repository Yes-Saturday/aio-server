package com.zhaizq.aio.blog.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "blog_info", autoResultMap = true)
public class BlogInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 祖ID */
    @TableField("gid")
    private Integer gid;

    /** 父ID */
    @TableField("pid")
    private Integer pid;

    /** 排序 */
    @TableField("`order`")
    private Integer order;

    /** 标题 */
    @TableField("title")
    private String title;

    /** 内容 */
    @TableField("markdown")
    private String markdown;
    @TableField("html")
    private String html;

    /** 等级 */
    @TableField("level")
    private Integer level;

    /** 删除标记 */
    @TableField("del")
    private Boolean del;

    /** 创建信息 */
    @TableField(value = "create_time", typeHandler = FastjsonTypeHandler.class)
    private Date createTime;
    @TableField("update_uid")
    private Integer updateUid;
    @TableField("update_time")
    private Date updateTime;
}