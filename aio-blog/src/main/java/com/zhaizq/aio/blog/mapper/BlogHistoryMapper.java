package com.zhaizq.aio.blog.mapper;

import com.zhaizq.aio.blog.mapper.entity.BlogHistory;
import com.zhaizq.aio.blog.mapper.entity.BlogInfo;
import com.zhaizq.aio.system.mapper.LambdaBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogHistoryMapper extends LambdaBaseMapper<BlogHistory> {
    default int save(BlogInfo blogInfo) {
        BlogHistory blogHistory = new BlogHistory();
        blogHistory.setBid(blogInfo.getId());
        blogHistory.setTitle(blogInfo.getTitle());
        blogHistory.setMarkdown(blogInfo.getMarkdown());
        blogHistory.setCreateUid(blogInfo.getUpdateUid());
        blogHistory.setCreateTime(blogInfo.getUpdateTime());
        return this.insert(blogHistory);
    }
}
