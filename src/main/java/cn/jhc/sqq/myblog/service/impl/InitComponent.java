package cn.jhc.sqq.myblog.service.impl;

import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.service.BlogService;
import cn.jhc.sqq.myblog.service.BlogTypeService;
import cn.jhc.sqq.myblog.service.BloggerService;
import cn.jhc.sqq.myblog.service.LinkService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/26 13:01
 */
@WebListener
public class InitComponent implements ServletContextListener, ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

//        BloggerService bloggerService = (BloggerService) applicationContext.getBean("bloggerServiceImpl");
//        Blogger blogger = bloggerService.findOne();
//        blogger.setPassword(null);
//        servletContext.setAttribute("blogger",blogger);

        BlogTypeService blogTypeService = (BlogTypeService) applicationContext.getBean("blogTypeServiceImpl");
        List<BlogType> blogTypeList = blogTypeService.findAllBlogType(null);
        servletContext.setAttribute("blogTypeList",blogTypeList);


        /*按年月分类的博客数量*/
        BlogService blogService= (BlogService) applicationContext.getBean("blogServiceImpl");
        List<Blog> blogCountList = blogService.findAllBlog(null);
        servletContext.setAttribute("blogCountList",blogCountList);


        /*友情链接*/
//        Map<String,Object> map = new HashMap<>();
//        map.put("bloggerid",blogger.getId());
        LinkService linkService = (LinkService) applicationContext.getBean("linkServiceImpl");
        List<Link> linkList = linkService.conditionlist(null);
        servletContext.setAttribute("linkList",linkList);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
