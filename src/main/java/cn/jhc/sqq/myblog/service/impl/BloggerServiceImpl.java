package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.mapper.BloggerMapper;
import cn.jhc.sqq.myblog.service.BloggerService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sqq
 * @ 2019/10/22 18:03
 */
@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class BloggerServiceImpl implements BloggerService {

    @Autowired
    private BloggerMapper bloggerMapper;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blogger findBloggerByName(String userName) {
        return bloggerMapper.findBloggerByName(userName);
    }

    @Override
    public Integer update(Blogger blogger) {
        SecurityUtils.getSubject().getSession().setAttribute(ConstantFied.CURRENT_USER,blogger);
        return bloggerMapper.update(blogger);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blogger findOne() {
        return bloggerMapper.findOne();
    }

    @Override
    public Integer insertBlogger(Blogger blogger) {
        return bloggerMapper.insertBlogger(blogger);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Blogger findById(Integer id) {
        return bloggerMapper.findById(id);
    }
}
