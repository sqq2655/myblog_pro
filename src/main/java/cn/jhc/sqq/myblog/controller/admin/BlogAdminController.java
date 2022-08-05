package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.PageBean;
import cn.jhc.sqq.myblog.lucene.BlogIndex;
import cn.jhc.sqq.myblog.service.BlogService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/26 12:01
 */
@Controller
@RequestMapping("/admin/blog")
public class BlogAdminController {

    @Autowired
    private BlogService blogService;

    private BlogIndex blogIndex = new BlogIndex();

    @RequestMapping("/save")
    @ResponseBody
    public Boolean save(Blog blog) throws IOException {
        int uid = ((Blogger)SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        Integer result = 0;
        if (blog.getId()!=null){
            blogIndex.updateIndex(blog);
            result = blogService.update(blog);
        }else {
            Blogger b = new Blogger();
            b.setId(uid);
            blog.setBlogger(b);
            result = blogService.add(blog);
            blogIndex.addIndex(blog);
        }
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map list(@RequestParam(value = "page",required = false) String pageNumber,
                    @RequestParam(value = "rows",required = false) String pageSize,
                    Blog blog){
        int uid = ((Blogger)SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        Map<String, Object> map = new HashMap<>();
        PageBean pageBean = new PageBean(Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
        map.put("bloggerid",uid);
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        if (blog.getTitle() != null && blog.getTitle() != "") {
            map.put("title", blog.getTitle());
        }
        List<Blog> list = blogService.conditionlist(map);

        Long total = blogService.getTotal(map);

        Map<String, Object> mapByPage = new HashMap<>();
        mapByPage.put("rows",list);
        mapByPage.put("total",total);
        return mapByPage;
    }


    @RequestMapping("/findById")
    @ResponseBody
    public Blog findById(String id){
        return blogService.findById(Integer.parseInt(id));
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Boolean delete(String id,String ids) throws IOException {
        Integer result=0;
        if(ids!=null){
            String[] idStr = ids.split(",");
            int[] arr = new int[idStr.length];
            for (int i = 0; i < idStr.length; i++) {
                arr[i] = Integer.parseInt(idStr[i]);
                blogIndex.deleteIndex(idStr[i]);
            }
            result = blogService.deleteMore(arr);
        }
        if(id!=null){
            result =  blogService.delete(Integer.parseInt(id));
            blogIndex.deleteIndex(id);
        }
       return result>0 ? true : false;

    }
}
