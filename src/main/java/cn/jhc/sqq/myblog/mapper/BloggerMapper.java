package cn.jhc.sqq.myblog.mapper;


import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.Blogger;
import org.springframework.stereotype.Repository;

/**
 * @author sqq
 * @ 2019/10/22 17:53
 */
@Repository
public interface BloggerMapper {

    Blogger findBloggerByName(String userName);

    Integer update(Blogger blogger);

    Blogger findOne();

    Integer insertBlogger(Blogger blogger);

    Blogger findById(Integer id);
}
