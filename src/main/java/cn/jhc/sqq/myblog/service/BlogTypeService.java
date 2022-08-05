package cn.jhc.sqq.myblog.service;

import cn.jhc.sqq.myblog.entity.BlogType;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/9/24 16:39
 */
public interface BlogTypeService {

    //全查
    List<BlogType> findAllBlogType(Integer bloggerid);
    //id查询
    BlogType findById(Integer id);
    //条件查询
    List<BlogType> conditionList(Map<String, Object> paramMap);

    Long getTotal(Integer id);

    Integer add(BlogType blogType);

    Integer update(BlogType blogType);

    Integer delete(Integer id);

    Integer deleteMore(int[] arr);
}
