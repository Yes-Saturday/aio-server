package com.zhaizq.aio.blog.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blog_history")
public class BlogHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** blog ID */
    @TableField("bid")
    private Integer bid;

    /** 标题 */
    @TableField("title")
    private String title;

    /** 内容 */
    @TableField("markdown")
    private String markdown;

    /** 创建信息 */
    @TableField("create_uid")
    private Integer createUid;
    @TableField("create_time")
    private Date createTime;
}