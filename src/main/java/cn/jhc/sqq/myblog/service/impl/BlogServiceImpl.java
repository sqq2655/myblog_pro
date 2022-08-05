package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.mapper.BlogMapper;
import cn.jhc.sqq.myblog.service.BlogService;
import cn.jhc.sqq.myblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/26 11:54
 */
@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private CommentService commentService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Blog> findAllBlog(Integer bloggerid) {
        return blogMapper.findAllBlog(bloggerid);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blog findById(Integer id) {
        return blogMapper.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Blog> conditionlist(Map<String, Object> paramtMap) {
        return blogMapper.conditionlist(paramtMap);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Long getTotal(Map<String, Object> paramMap) {
        return blogMapper.getTotal(paramMap);
    }

    @Override
    public Integer add(Blog blog) {
        return blogMapper.add(blog);
    }

    @Override
    public Integer delete(Integer id) {
        commentService.deleteByBlogId(id);
        return blogMapper.delete(id);
    }

    @Override
    public Integer update(Blog blog) {
        return blogMapper.update(blog);
    }

    @Override
    public Integer deleteMore(int[] arr) {
        commentService.deleteMoreByBlogId(arr);
        return blogMapper.deleteMore(arr);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blog getLastBlog(Integer bloggerid, Integer id) {
        return blogMapper.getLastBlog(bloggerid,id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blog getNextBlog(Integer bloggerid, Integer id) {
        return blogMapper.getNextBlog(bloggerid,id);
    }

//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
//    public Integer getBlogCountByTypeId(Integer paramId) {
//        return blogMapper.getBlogCountByTypeId(paramId);
//    }


    @Override
    public void setReplyHit() {
        blogMapper.setReplyHit();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Blog> findHotest(Integer id) {
        return blogMapper.findHotest(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Long getClickTotal(Integer id) {
        return blogMapper.getClickTotal(id);
    }


}
