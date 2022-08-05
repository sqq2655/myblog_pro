package cn.jhc.sqq.myblog.mapper;


import cn.jhc.sqq.myblog.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/9/30 12:53
 */
@Repository
public interface CommentMapper {

    Integer add(Comment comment);

    Integer update(Comment comment);

    Integer updateMore(@Param("ids") int[] arr, @Param("state") Integer state);

    List<Comment> list(Map<String, Object> paramMap);

    Integer delete(Integer id);

    Integer deleteByBlogId(Integer blogId);

    Long getTotal(Map<String, Object> paramMap);

    Integer deleteMore(int[] arr);

    Integer deleteMoreByBlogId(int[] arr);

    Integer findCommentCount(Integer blogId);
}
