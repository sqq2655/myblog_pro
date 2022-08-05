package cn.jhc.sqq.myblog.service;

import cn.jhc.sqq.myblog.entity.Blog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/26 11:54
 */

public interface BlogService {

    List<Blog> findAllBlog(Integer bloggerid);

    Blog findById(Integer id);

    List<Blog> conditionlist(Map<String,Object> paramtMap);

    Long getTotal(Map<String,Object> paramMap);

    Integer add(Blog blog);

    Integer delete(Integer id);

    Integer update(Blog blog);

    Integer deleteMore(int[] arr);

    /** 根据类型查询博客数量*/
//    Integer getBlogCountByTypeId(Integer paramId);

    /*获取上一篇博客*/
    Blog getLastBlog(@Param("bloggerid") Integer bloggerid, @Param("id") Integer id);
    /*获取下一篇博客*/
    Blog getNextBlog(@Param("bloggerid") Integer bloggerid, @Param("id") Integer id);

    /*设置回复数*/
    void setReplyHit();

    List<Blog> findHotest(Integer id);

    /*查询点击总数*/
    Long getClickTotal(Integer id);
}
