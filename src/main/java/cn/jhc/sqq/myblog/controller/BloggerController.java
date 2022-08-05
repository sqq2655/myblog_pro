package cn.jhc.sqq.myblog.controller;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.service.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/11/3 16:26
 */
@Controller
public class BloggerController {

    @Autowired
    private BloggerService bloggerService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/toLogin")
    public String toLogin(HttpServletRequest request){
        if(((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER))!=null && "blogger".equals(((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getPerms())){
            return "admin/main";
        }
        if(((Blogger) SecurityUtils.getSubject().getSession().getAttribute("guest"))!=null && "blogger".equals(((Blogger) SecurityUtils.getSubject().getSession().getAttribute("guest")).getPerms())){
            System.out.println((Blogger) SecurityUtils.getSubject().getSession().getAttribute("guest"));
            SecurityUtils.getSubject().getSession().setAttribute(ConstantFied.CURRENT_USER,(Blogger) SecurityUtils.getSubject().getSession().getAttribute("guest"));
            int uid  = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute("guest")).getId();
            findInfoByBlogId(uid,request);
            return "admin/main";
        }
        return "login";
    }
    @RequestMapping("/doLogin")
    public String doLogin(Blogger blogger, Model model, HttpServletRequest request) throws IOException {
        String userName  = blogger.getUserName();
        String password = blogger.getPassword();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userName, password);
            try {
                SecurityUtils.getSubject().login(usernamePasswordToken);
                Subject subject = SecurityUtils.getSubject();
                boolean isAdmin = subject.isPermitted("blogger");
                Blogger b = bloggerService.findBloggerByName(userName);
                if (isAdmin==true){
//                    获取登陆后博主相应内容
                    SecurityUtils.getSubject().getSession().setAttribute(ConstantFied.CURRENT_USER,b);
                  int uid  = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
                    findInfoByBlogId(uid,request);
                    return "admin/main";
                }
                SecurityUtils.getSubject().getSession().setAttribute("guest",b);
                return "redirect:/index";
            } catch (UnknownAccountException e) {
                model.addAttribute("blogger_false",blogger);
                model.addAttribute("errorMsg","用户名不存在");
            } catch (IncorrectCredentialsException e){
                model.addAttribute("blogger_false",blogger);
                model.addAttribute("errorMsg","密码错误");
            }
            return "login";
    }


    @RequestMapping("/doAjaxLogin")
    @ResponseBody
    public Boolean doAjaxLogin(Blogger blogger, Model model, HttpServletRequest request) throws IOException {
        String userName  = blogger.getUserName();
        String password = blogger.getPassword();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userName, password);
        try {
            SecurityUtils.getSubject().login(usernamePasswordToken);
            Blogger bloggerByName = bloggerService.findBloggerByName(userName);
            SecurityUtils.getSubject().getSession().setAttribute("guest",bloggerByName);

            return true;
        } catch (UnknownAccountException e) {
            model.addAttribute("errorMsg","用户名不存在");
        } catch (IncorrectCredentialsException e){
            model.addAttribute("errorMsg","密码错误");
        }
       return false;
    }


    public void findInfoByBlogId(Integer id,HttpServletRequest request){
        ServletContext servletContext = request.getServletContext();
        BloggerService bloggerService = (BloggerService) applicationContext.getBean("bloggerServiceImpl");
        if(id != null){
            Blogger b = bloggerService.findById(id);
            b.setPassword(null);
            servletContext.setAttribute("blogger",b);
        }else{
            if(servletContext.getAttribute("blogger")!=null){
                servletContext.removeAttribute("blogger");
            }
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


    @RequestMapping("/register")
    @ResponseBody
    public Boolean register(Blogger blogger,Integer flag){
        String newPwd = new SimpleHash("MD5",blogger.getPassword(),
                ByteSource.Util.bytes(blogger.getUserName()),1024).toString();
        blogger.setPassword(newPwd);
        String perms = (flag==1 ? "blogger" : "user");
        blogger.setPerms(perms);
        Integer result = bloggerService.insertBlogger(blogger);
        if(result > 0){
            return true;
        }
        return false;
    }


    @RequestMapping("/aboutMe")
    public String aboutMe(Model model, HttpServletRequest request,
                          @RequestParam(value = "bid",required = false)Integer bid){
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
        Map<String, Object> map = new HashMap<>();
        map.put("bloggerid",bloggerid);
        map.put("state",1);

        model.addAttribute("mainPage","foreground/about/page.html");

        List<Blog> hotestList = blogService.findHotest(flag);
        model.addAttribute("hotestList",hotestList);
        /*总数*/
        model.addAttribute("blogTotal",blogService.getTotal(map));
        model.addAttribute("CommentTotal",commentService.getTotal(map));
        model.addAttribute("clickTotal",blogService.getClickTotal(flag));
        return "index";
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Boolean logout(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        if (subject.isPermitted("blogger")){
            ServletContext servletContext = request.getServletContext();
            servletContext.removeAttribute("blogger");

            BlogTypeService blogTypeService = (BlogTypeService) applicationContext.getBean("blogTypeServiceImpl");
            List<BlogType> blogTypeList = blogTypeService.findAllBlogType(null);
            servletContext.setAttribute("blogTypeList",blogTypeList);

            BlogService blogService= (BlogService) applicationContext.getBean("blogServiceImpl");
            List<Blog> blogCountList = blogService.findAllBlog(null);
            servletContext.setAttribute("blogCountList",blogCountList);

            /*友情链接*/
            LinkService linkService = (LinkService) applicationContext.getBean("linkServiceImpl");
            List<Link> linkList = linkService.conditionlist(null);
            servletContext.setAttribute("linkList",linkList);
        }

        SecurityUtils.getSubject().logout();
        return true;
    }
}
