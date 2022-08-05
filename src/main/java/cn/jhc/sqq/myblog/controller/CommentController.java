package cn.jhc.sqq.myblog.controller;

import cn.jhc.sqq.myblog.entity.Comment;
import cn.jhc.sqq.myblog.service.CommentService;
import cn.jhc.sqq.myblog.utils.ImageCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/11/8 12:09
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getCode")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            ImageCodeUtil imageCodeUtil = new ImageCodeUtil();
            imageCodeUtil.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            logger.error("获取验证码失败>>>>   ", e);
        }
    }

    @RequestMapping("/submitComment")
    @ResponseBody
    public Map submitComment(Comment comment, String code, HttpSession session,HttpServletRequest request){
        String addr = request.getRemoteAddr();
        comment.setUserIp(addr);
        Map<String, Object> map = new HashMap<>();
        if(!code.equals(session.getAttribute("code"))){
            map.put("success",false);
            map.put("msg","验证码错误");
        }
        else{
            commentService.add(comment);
            map.put("success",true);
            map.put("msg","发表成功,等待管理员审核后显示");
        }
        return map;
    }
}
