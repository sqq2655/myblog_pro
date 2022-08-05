package cn.jhc.sqq.myblog.controller;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.*;
import cn.jhc.sqq.myblog.lucene.BlogIndex;
import cn.jhc.sqq.myblog.service.*;
import cn.jhc.sqq.myblog.utils.PageUtil;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/11/2 14:27
 */
@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BloggerService bloggerService;

    @Autowired
    private CommentService commentService;

    private BlogIndex blogIndex = new BlogIndex();


    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/toDetails/{bid}/{id}"+".html")
    public String toDetails(
                            @PathVariable(value = "bid",required = false) Integer bid,
                            @PathVariable(value = "id",required = false) Integer id,
                            Model model, HttpServletRequest request){
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

        Blog blog = blogService.findById(id);
        model.addAttribute("blog", blog);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("blogId", id);
        map.put("state", 1);
//        map.put("bloggerid",bloggerid);
        List<Comment> comments = commentService.list(map);
        System.out.println(comments);
        model.addAttribute("comments", comments);
//        阅读人数+1
        blog.setClickHit(blog.getClickHit() + 1);
        blogService.update(blog);

        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList",hotestList);

        model.addAttribute("pageCode", getUpAndDownPage(bid,blogService.getLastBlog(flag,id), blogService.getNextBlog(flag,id), request.getContextPath()));
        model.addAttribute("mainPage", "foreground/blog/details.html");
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("state", 1);
        map1.put("bloggerid",flag);
        /*总数*/
        model.addAttribute("blogTotal",blogService.getTotal(map1));
        System.out.println(blogService.getTotal(map1));
        model.addAttribute("CommentTotal",commentService.getTotal(map1));
        model.addAttribute("clickTotal",blogService.getClickTotal(flag));
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

    /*上一篇下一篇*/
    public String getUpAndDownPage(Integer bid,Blog lastBlog, Blog nextBlog, String context) {
        StringBuffer pageCode = new StringBuffer();
        if (lastBlog == null) {
            pageCode.append("<p><i class=\"fa fa-chevron-circle-up\" aria-hidden=\"true\"></i>上一篇:  没有了</p>");
        } else {
            pageCode.append("<p><i class=\"fa fa-chevron-circle-up\" aria-hidden=\"true\"></i>上一篇：<a href='" + context + "/toDetails/" + bid+"/" + lastBlog.getId() + ".html'>" + lastBlog.getTitle() + "</a></p>");
        }

        if (nextBlog == null) {
            pageCode.append("<p><i class=\"fa fa-chevron-circle-down\" aria-hidden=\"true\"></i>下一篇:  没有了</p>");
        } else {
            pageCode.append("<p><i class=\"fa fa-chevron-circle-down\" aria-hidden=\"true\"></i>下一篇：<a href='" + context + "/toDetails/" + bid +"/"  + nextBlog.getId() + ".html'>" + nextBlog.getTitle() + "</a></p>");
        }
        return pageCode.toString();
    }


    @RequestMapping("/toBlogType")
    public String toBlogType(Model model,
                            @RequestParam(value = "bid",required = false)Integer bid){
        model.addAttribute("bid",bid);
        return "foreground/blog/blogByType";
    }

    @RequestMapping("/getByBlogType")
    public String getByBlogType(@RequestParam(value = "typeId",required = false) String typeId, Model model,
                                @RequestParam(value = "page",required = false)String page,
                                @RequestParam(value = "bid",required = false)Integer bid,
                                HttpServletRequest request){
        Integer bloggerid = 0;
        if (SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)!=null){
            bloggerid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        }
        Integer flag = (bloggerid==0?null:bloggerid);

        if(page==null||"".equals(page)){
            page="1";
        }
        PageBean pageBean = new PageBean(Integer.parseInt(page),3);

        Map<String, Object> map = new HashMap<>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("typeId",Integer.parseInt(typeId));
        map.put("bloggerid",flag);
        List<Blog> blogList = blogService.conditionlist(map);

        //        分页栏
        StringBuffer param = new StringBuffer();
        if(typeId!=null){
            param.append("typeId="+typeId+"&");
        }
        model.addAttribute("typeId",typeId == null ? 0 : typeId);

        String pageCode = PageUtil.getPagination(request.getContextPath()+"/getByBlogType",blogService.getTotal(map),
                Integer.parseInt(page),3,param.toString());

        model.addAttribute("blogListByType",blogList);
        model.addAttribute("pageCode",pageCode);

        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList",hotestList);
        /*总数*/
//        model.addAttribute("blogTotal",blogService.getTotal(null));
//        model.addAttribute("CommentTotal",commentService.getTotal(null));
//        model.addAttribute("clickTotal",blogService.getClickTotal(uid));
        model.addAttribute("bid",bid);
        return "foreground/blog/blogByType";
    }


    @RequestMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q,
                         @RequestParam(value = "page", required = false) String pageNumber,
                         Model model, HttpServletRequest request) throws ParseException, InvalidTokenOffsetsException, IOException {
        Integer bloggerid = 0;
        if (SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)!=null){
            bloggerid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        }else{
            bloggerid = 0;
            findInfoByBlogId(null,request);
        }
        Integer flag = (bloggerid==0?null:bloggerid);
        Map<String, Object> map = new HashMap<>();
        map.put("bloggerid",flag);
        map.put("state",1);
        if (pageNumber == null || pageNumber == "") {
            pageNumber = "1";
        }
        List<Blog> blogList = blogIndex.searchBlog(q.trim());
        for (Blog blog : blogList) {
            Blog b = blogService.findById(blog.getId());
            Blogger b1 = new Blogger();
            b1.setId(b.getBlogger().getId());
            blog.setBlogger(b1);
        }

        model.addAttribute("q", q);
        model.addAttribute("resultTotal", blogList.size());
        model.addAttribute("pageTitle", "搜索关键字(" + q + ")的结果");
        model.addAttribute("mainPage", "foreground/blog/searchBlog.html");

        /*分页*/
        int totalIndex = 0;
        if(blogList.size() <= Integer.parseInt(pageNumber)*3){
            totalIndex = blogList.size();
        }else{
            totalIndex = Integer.parseInt(pageNumber)*3;
        }
        model.addAttribute("blogList", blogList.subList((Integer.parseInt(pageNumber) - 1) * 3,totalIndex));
        model.addAttribute("pageCode", getResultByPage(Integer.parseInt(pageNumber), blogList.size(), q, 3, request.getContextPath()));


        /*热门*/
        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList",hotestList);
        /*总数*/
        model.addAttribute("blogTotal",blogService.getTotal(map));
        model.addAttribute("CommentTotal",commentService.getTotal(map));
        model.addAttribute("clickTotal",blogService.getClickTotal(flag));
        return "index";
    }

    /* 对lucene查询结果分页*/
    public String getResultByPage(int pageNumber, int totalNum, String q, int pageSize, String context) {
        StringBuffer pageCode = new StringBuffer();
        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        if (totalPage == 0) {
            return "";
        }
        /*当前页大于1*/
        if (pageNumber > 1) {
            pageCode.append("<li><a href='" + context + "/search?page=" + (pageNumber - 1) + "&q=" + q + "'>上一页</a></li>");
        } else {
            pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
        }

        if (pageNumber < totalPage) {
            pageCode.append("<li><a href='" + context + "/search?page=" + (pageNumber + 1) + "&q=" + q + "'>下一页</a></li>");
        } else {
            pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
        }

        return pageCode.toString();
    }
}
