package cn.jhc.sqq.myblog.service;

import cn.jhc.sqq.myblog.entity.Blogger;

/**
 * @author sqq
 * @ 2019/10/22 18:04
 */
public interface BloggerService {
    Blogger findBloggerByName(String userName);

    Integer update(Blogger blogger);

    Blogger findOne();

    Integer insertBlogger(Blogger blogger);

    Blogger findById(Integer id);
}
