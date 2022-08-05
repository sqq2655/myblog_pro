package cn.jhc.sqq.myblog.exceptionHander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sqq
 * @ 2019/10/13 15:14
 */

@ControllerAdvice
public class GlobalException {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(value = {Exception.class})
    public ModelAndView exceptionHandler(Exception e, HttpServletRequest request){
        logger.error("Request URL : {} , Exception : {}",request.getRequestURL(),e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception",e.getMessage());
        mav.addObject("url",request.getRequestURL());
        mav.setViewName("error/error");
        return mav;
    }
}
