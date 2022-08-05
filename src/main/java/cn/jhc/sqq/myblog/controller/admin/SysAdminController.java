package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.service.BlogService;
import cn.jhc.sqq.myblog.service.BlogTypeService;
import cn.jhc.sqq.myblog.service.BloggerService;
import cn.jhc.sqq.myblog.service.LinkService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/24 14:55
 */
@Controller
@RequestMapping("/admin/system")
public class SysAdminController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BloggerService bloggerService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private BlogTypeService blogTypeService;

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("/refreshSystem")
    @ResponseBody
    public Boolean refreshSystem(HttpServletRequest request){
        try {

            int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
            ServletContext servletContext = RequestContextUtils.findWebApplicationContext(request).getServletContext();
            List<Blog> blogCountList = blogService.findAllBlog(uid);
            servletContext.setAttribute("blogCountList",blogCountList);
            List<BlogType> blogTypeList = blogTypeService.findAllBlogType(uid);
            servletContext.setAttribute("blogTypeList",blogTypeList);
            Map<String, Object> map = new HashMap<>();
            map.put("bloggerid",uid);
            List<Link> linkList = linkService.conditionlist(map);
            servletContext.setAttribute("linkList",linkList);
            Blogger blogger = bloggerService.findById(uid);
            blogger.setPassword(null);
            servletContext.setAttribute("blogger",blogger);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request){
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
        return "login";
    }
}
