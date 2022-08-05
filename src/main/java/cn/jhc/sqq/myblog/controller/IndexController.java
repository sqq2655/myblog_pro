package cn.jhc.sqq.myblog.controller;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.*;
import cn.jhc.sqq.myblog.service.*;
import cn.jhc.sqq.myblog.utils.PageUtil;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/19 13:05
 */
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*传什么跳转页面*/
    @RequestMapping("/get/{url}")
    public String moveTo(@PathVariable(value = "url",required = false) String pageName,
                         @RequestParam(value = "id",required = false) String id, Model model){
        model.addAttribute("id",id);
        return "admin/"+ pageName;
    }


    @RequestMapping({"/","/index"})
    public String index(@RequestParam(value = "page",required = false)String page,
                        @RequestParam(value = "typeId",required = false)String typeId,
                        @RequestParam(value = "releaseDateStr",required = false)String releaseDateStr,
                        @RequestParam(value = "bid",required = false)Integer bid,
                        Model model, HttpServletRequest request){
        Integer bloggerid = 0;
        if(bid==null){
            if (SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)!=null){
                bloggerid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
            }else {
                bloggerid = 0;
                findInfoByBlogId(null,request);
            }
        }else {
            bloggerid = bid;
            findInfoByBlogId(bloggerid,request);
            model.addAttribute("bid",bid);
        }

        if(page==null||"".equals(page)){
            page="1";
        }
        PageBean pageBean = new PageBean(Integer.parseInt(page),5);
        Map<String, Object> map = new HashMap<>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("typeId",typeId);
        map.put("releaseDateStr",releaseDateStr);
        map.put("state",1);
        if(bloggerid != 0){
            map.put("bloggerid",bloggerid);
        }
        Integer flag = (bloggerid==0?null:bloggerid);
        List<Blog> blogList = blogService.conditionlist(map);

        blogService.setReplyHit();

//        分页栏
        StringBuffer param = new StringBuffer();
        if(typeId!=null && typeId!=""){
            param.append("typeId="+typeId+"&");
        }

        if(releaseDateStr!=null && releaseDateStr!=""){
            param.append("releaseDateStr="+releaseDateStr);
        }
        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList",hotestList);
        String pageCode = PageUtil.getPagination(request.getContextPath()+"/index",blogService.getTotal(map),
                                                Integer.parseInt(page),5,param.toString());

        model.addAttribute("mainPage","foreground/blog/blog.html");
        model.addAttribute("blogTotal",blogService.getTotal(map));
        model.addAttribute("CommentTotal",commentService.getTotal(map));
        model.addAttribute("clickTotal",blogService.getClickTotal(flag));
        model.addAttribute("pageCode",pageCode);
        model.addAttribute("blogList",blogList);

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






    @RequestMapping("/403")
        public String error_403(){
            return "error/403";
    }
}
