package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.Link;
import cn.jhc.sqq.myblog.entity.PageBean;
import cn.jhc.sqq.myblog.service.LinkService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqq
 * @ 2019/10/4 13:18
 */
@Controller
@RequestMapping("/admin/link")
public class LinkAdminController {

    @Autowired
    private LinkService linkService;

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(@RequestParam(value = "page", required = false) String pageNumber,
                                    @RequestParam(value = "rows", required = false) String pageSize) {
        int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        PageBean pageBean = new PageBean(Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mapResult = new HashMap<>();
        map.put("size", pageBean.getPageSize());
        map.put("start", pageBean.getStart());
        map.put("bloggerid",uid);
        List<Link> list = linkService.conditionlist(map);
        Long total = linkService.getTotal(map);

        mapResult.put("rows", list);
        mapResult.put("total", total);
        return mapResult;
    }

    @RequestMapping("/add")
    @ResponseBody
    public Boolean add(Link link) {
        Blogger b = new Blogger();
        int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        b.setId(uid);
        link.setBlogger(b);
        Integer result = linkService.add(link);
        if (result == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public Boolean edit(Link link) {
        Integer result = linkService.update(link);
        if (result == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Boolean delete(String id, String ids) {
        Integer result = 0;
        if (id != null) {
            result = linkService.delete(Integer.parseInt(id));
        } else {
            String[] strings = ids.split(",");
            int[] arr = new int[strings.length];
            for (int i = 0; i < strings.length; i++) {
                arr[i] = Integer.parseInt(strings[i]);
            }
            result = linkService.deleteMore(arr);
        }
        if (result == 0) {
            return false;
        }
        return true;
    }
}
