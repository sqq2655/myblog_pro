package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.entity.Comment;
import cn.jhc.sqq.myblog.mapper.CommentMapper;
import cn.jhc.sqq.myblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/27 12:15
 */
@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Integer add(Comment comment) {
        return commentMapper.add(comment);
    }

    @Override
    public Integer update(Comment comment) {
        return commentMapper.update(comment);
    }

    @Override
    public Integer updateMore(int[] arr, Integer state) {
        return commentMapper.updateMore(arr,state);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Comment> list(Map<String, Object> paramMap) {
        return commentMapper.list(paramMap);
    }

    @Override
    public Integer delete(Integer id) {
        return commentMapper.delete(id);
    }

    @Override
    public Integer deleteByBlogId(Integer blogId) {
        return commentMapper.deleteByBlogId(blogId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Long getTotal(Map<String, Object> paramMap) {
        return commentMapper.getTotal(paramMap);
    }

    @Override
    public Integer deleteMore(int[] arr) {
        return commentMapper.deleteMore(arr);
    }

    @Override
    public Integer deleteMoreByBlogId(int[] arr) {
        return commentMapper.deleteMoreByBlogId(arr);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Integer findCommentCount(Integer blogId) {
        return commentMapper.findCommentCount(blogId);
    }
}
