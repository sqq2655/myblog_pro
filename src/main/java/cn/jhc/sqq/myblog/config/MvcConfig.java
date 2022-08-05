package cn.jhc.sqq.myblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author sqq
 * @ 2019/10/19 13:38
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    @Value("${file.upload.path}")
    private String filePath;

    @Value("${file.upload.path.relative}")
    private String relativePath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/","file:D:/sources/");
        registry.addResourceHandler(relativePath).addResourceLocations("file:/"+filePath);
        super.addResourceHandlers(registry);
    }
}
