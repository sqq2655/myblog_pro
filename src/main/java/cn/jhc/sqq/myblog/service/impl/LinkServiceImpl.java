package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.mapper.LinkMapper;
import cn.jhc.sqq.myblog.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/27 13:41
 */
@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkMapper linkMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Link findById(Integer id) {
        return linkMapper.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Link> conditionlist(Map<String, Object> paramtMap) {
        return linkMapper.conditionlist(paramtMap);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Long getTotal(Map<String, Object> paramtMap) {
        return linkMapper.getTotal(paramtMap);
    }

    @Override
    public Integer add(Link link) {
        return linkMapper.add(link);
    }

    @Override
    public Integer update(Link link) {
        return linkMapper.update(link);
    }

    @Override
    public Integer delete(Integer id) {
        return linkMapper.delete(id);
    }

    @Override
    public Integer deleteMore(int[] arr) {
        return linkMapper.deleteMore(arr);
    }
}
