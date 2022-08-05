package cn.jhc.sqq.myblog.mapper;

import cn.jhc.sqq.myblog.entity.Link;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/4 13:00
 */
@Repository
public interface LinkMapper {
    //  根据主键查询
    Link findById(Integer id);
    //    条件查询
    List<Link> conditionlist(Map<String, Object> paramtMap);

    Long getTotal(Map<String, Object> paramtMap);

    Integer add(Link link);

    Integer update(Link link);

    Integer delete(Integer id);

    Integer deleteMore(int[] arr);

}
