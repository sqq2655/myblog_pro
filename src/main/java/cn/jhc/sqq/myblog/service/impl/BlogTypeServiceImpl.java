package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.mapper.BlogTypeMapper;
import cn.jhc.sqq.myblog.service.BlogTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/9/24 16:39
 */
@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class BlogTypeServiceImpl implements BlogTypeService {
    @Autowired
    private BlogTypeMapper blogTypeMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<BlogType> findAllBlogType(Integer bloggerid) {
        return blogTypeMapper.findAllBlogType(bloggerid);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public BlogType findById(Integer id) {
        return blogTypeMapper.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<BlogType> conditionList(Map<String, Object> paramMap) {
        return blogTypeMapper.conditionList(paramMap);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Long getTotal(Integer id) {
        return blogTypeMapper.getTotal(id);
    }


    @Override
    public Integer add(BlogType blogType) {
        return blogTypeMapper.add(blogType);
    }

    @Override
    public Integer update(BlogType blogType) {
        return blogTypeMapper.update(blogType);
    }

    @Override
    public Integer delete(Integer id) {
        return blogTypeMapper.delete(id);
    }

    @Override
    public Integer deleteMore(int[] arr) {
        return blogTypeMapper.deleteMore(arr);
    }
}
