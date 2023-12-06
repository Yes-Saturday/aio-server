package com.zhaizq.aio.blog.controller;

import com.zhaizq.aio.blog.mapper.entity.BlogInfo;
import com.zhaizq.aio.blog.service.BlogService;
import com.zhaizq.aio.common.BusinessException;
import com.zhaizq.aio.common.TreeHolder;
import com.zhaizq.aio.common.annotation.JsonParam;
import com.zhaizq.aio.common.utils.StringUtil;
import com.zhaizq.aio.system.controller.BaseController;
import com.zhaizq.aio.system.mapper.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController extends BaseController {
    @Autowired
    private BlogService blogService;

    @RequestMapping("/save")
    public Result<?> save(@JsonParam BlogInfo blog) {
        if (blog.getLevel() > getLogin().getLevel())
            throw new BusinessException("level is to high");

        if (StringUtil.isEmpty(blog.getTitle()))
            throw new BusinessException("title is empty");

        blogService.save(blog, getLogin().getId());
        return success();
    }

    @RequestMapping("/del")
    public Result<?> del(@JsonParam Integer id) {
        Integer count = blogService.lambdaQuery().eq(BlogInfo::getPid, id).count();
        if (count > 0) throw new BusinessException("children is not empty");

        BlogInfo blog = blogService.getById(id);
        if (blog.getLevel() > getLogin().getLevel())
            throw new BusinessException("level is to high");

        blogService.lambdaUpdate().eq(BlogInfo::getId, id).set(BlogInfo::getDel, true).update();
        return success();
    }

    @RequestMapping("/subject")
    public Result<?> subject() {
        List<BlogInfo> data = blogService.lambdaQuery().isNull(BlogInfo::getPid).eq(BlogInfo::getDel, false).le(BlogInfo::getLevel, getLogin().getLevel()).list();
        return success(data);
    }

    @RequestMapping("/tree")
    public Result<?> tree(@JsonParam Integer gid) {
        SystemUser login = getLogin();
        BlogInfo gBlog = blogService.getById(gid);
        if (gBlog.getLevel() > login.getLevel())
            throw new BusinessException("level is to high");

        List<BlogInfo> list = blogService.lambdaQuery().eq(BlogInfo::getGid, gid).eq(BlogInfo::getDel, false).list();
        TreeHolder<BlogInfo> treeHolder = new TreeHolder<>(BlogInfo::getId, BlogInfo::getPid, list);
        List<TreeHolder.LabelNode> labels = treeHolder.labelTree(v -> v.getLevel() > login.getLevel() ? null : v.getTitle());
        return success(labels);
    }

    @RequestMapping("/info")
    public Result<?> info(@JsonParam Integer id) {
        BlogInfo blog = blogService.lambdaQuery().eq(BlogInfo::getId, id).eq(BlogInfo::getDel, false).one();
        if (blog.getLevel() > getLogin().getLevel())
            throw new BusinessException("level is to high");

        return success(blog);
    }
}