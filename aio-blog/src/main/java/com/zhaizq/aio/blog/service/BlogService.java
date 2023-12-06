package com.zhaizq.aio.blog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaizq.aio.blog.mapper.BlogHistoryMapper;
import com.zhaizq.aio.blog.mapper.BlogInfoMapper;
import com.zhaizq.aio.blog.mapper.entity.BlogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BlogService extends ServiceImpl<BlogInfoMapper, BlogInfo> {
    @Autowired
    private BlogHistoryMapper blogHistoryMapper;

    @Transactional(rollbackFor = Exception.class)
    public void save(BlogInfo blog, Integer operator) {
        blog.setDel(false);
        blog.setUpdateUid(operator);
        blog.setUpdateTime(new Date());

        if (blog.getId() == null) {
            blog.setCreateTime(blog.getUpdateTime());
            baseMapper.insert(blog);
        } else {
            blog.setCreateTime(null);
            baseMapper.updateById(blog);
        }

        blogHistoryMapper.save(blog);
    }
}
