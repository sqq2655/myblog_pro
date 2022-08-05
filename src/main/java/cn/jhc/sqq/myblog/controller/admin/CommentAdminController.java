package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Comment;
import cn.jhc.sqq.myblog.entity.PageBean;
import cn.jhc.sqq.myblog.service.CommentService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/27 12:20
 */
@Controller
@RequestMapping("/admin/comment")
public class CommentAdminController {

    @Autowired
    private CommentService commentService;
    @RequestMapping("/list")
    @ResponseBody
    public Map review(@RequestParam(value = "page",required = false) String pageNumber,
                      @RequestParam(value = "rows",required = false) String pageSize,
                      @RequestParam(value = "state",required = false) String state, Model model){
        int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        PageBean pageBean = new PageBean(Integer.parseInt(pageNumber),Integer.parseInt(pageSize));
        Map<String, Object> map = new HashMap<>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("state",state);
        map.put("bloggerid",uid);
        List<Comment> list = commentService.list(map);

        Long total = commentService.getTotal(map);
        Map<String, Object> mapResult = new HashMap<>();
        if(total==0){
            model.addAttribute("msg","暂无评论信息");
        }
        mapResult.put("rows",list);
        mapResult.put("total",total);
        return mapResult;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Map delete(String id,String ids){
        Map<String, Object> map = new HashMap<>();
        try {
            Integer result=0;
            if (id != null) {
                result = commentService.delete(Integer.parseInt(id));
            }
            if(ids != null){
                String[] strings = ids.split(",");
                int arr[] = new int[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    arr[i] = Integer.parseInt(strings[i]);
                }
                result = commentService.deleteMore(arr);
            }

            map.put("res",true);
            map.put("count",result);
        } catch (NumberFormatException e) {
            map.put("res",false);
        }
        return map;
    }


    @RequestMapping("/commentReview")
    @ResponseBody
    public Map review(String ids,Integer state){
        Map<String, Object> map = new HashMap<>();
        String[] strings = ids.split(",");
        Integer result=0;
        Comment c = new Comment();
        c.setId(Integer.parseInt(strings[0]));
        c.setState(state);
        try {
            if (strings.length==1){
                result = commentService.update(c);
            }
            if(strings.length>1){
                int[] arr = new int[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    arr[i] = Integer.parseInt(strings[i]);
                }
                result = commentService.updateMore(arr,state);
            }
            map.put("success",true);
            map.put("result",result);
        } catch (NumberFormatException e) {
            map.put("success",false);
        }
        return map;
    }
}