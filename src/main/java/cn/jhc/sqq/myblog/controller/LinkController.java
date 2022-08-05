package cn.jhc.sqq.myblog.controller;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.service.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/11/10 13:46
 */
@Controller
@RequestMapping("/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/toLink")
    public String toLink(Model model, HttpServletRequest request,
                         @RequestParam(value = "bid",required = false)Integer bid
                         ) {
        Integer bloggerid = 0;
        if(bid==null){
            if (SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)!=null){
                bloggerid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
            }else{
                bloggerid=0;
                findInfoByBlogId(null,request);
            }
        }else{
            bloggerid=bid;
            findInfoByBlogId(bloggerid,request);
            model.addAttribute("bid",bid);
        }

        Integer flag = (bloggerid==0?null:bloggerid);
        Map<String,Object> map = new HashMap<>();
        map.put("bloggerid",flag);
        map.put("state",1);
        Long linktotal = linkService.getTotal(map);
        List<Link> linkList = linkService.conditionlist(map);
        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList", hotestList);
        model.addAttribute("linktotal", linktotal);
        model.addAttribute("allLink", linkList);
        model.addAttribute("blogTotal",blogService.getTotal(map));
        model.addAttribute("CommentTotal",commentService.getTotal(map));
        model.addAttribute("clickTotal",blogService.getClickTotal(flag));
        model.addAttribute("mainPage", "foreground/about/link.html");
        return "index";
    }

    public void findInfoByBlogId(Integer id,HttpServletRequest request){
        ServletContext servletContext = request.getServletContext();
        BloggerService bloggerService = (BloggerService) applicationContext.getBean("bloggerServiceImpl");
        if(id != null){
            Blogger b = bloggerService.findById(id);
            b.setPassword(null);
            servletContext.setAttribute("blogger",b);
        }else{
            servletContext.removeAttribute("blogger");
        }
        BlogTypeService blogTypeService = (BlogTypeService) applicationContext.getBean("blogTypeServiceImpl");
        List<BlogType> blogTypeList = blogTypeService.findAllBlogType(id);
        servletContext.setAttribute("blogTypeList",blogTypeList);

        BlogService blogService= (BlogService) applicationContext.getBean("blogServiceImpl");
        List<Blog> blogCountList = blogService.findAllBlog(id);
        servletContext.setAttribute("blogCountList",blogCountList);


        /*友情链接*/
        LinkService linkService = (LinkService) applicationContext.getBean("linkServiceImpl");
        Map<String, Object> map = new HashMap<>();
        map.put("bloggerid",id);
        List<Link> linkList = linkService.conditionlist(map);
        servletContext.setAttribute("linkList",linkList);


    }

}
