package cn.jhc.sqq.myblog;

import cn.jhc.sqq.myblog.entity.Comment;
import cn.jhc.sqq.myblog.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
class MyblogApplicationTests {
    @Autowired
    private CommentService commentService;
    @Test
    void contextLoads() {
        HashMap<String, Object> map = new HashMap<>();
        List<Comment> list = commentService.list(map);
        System.out.println(list);
    }

}
