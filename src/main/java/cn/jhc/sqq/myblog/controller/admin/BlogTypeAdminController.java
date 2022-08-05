package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.BlogType;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.entity.PageBean;
import cn.jhc.sqq.myblog.service.BlogTypeService;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sqq
 * @ 2019/10/26 13:53
 */
@Controller
@RequestMapping("/admin/blogType")
public class BlogTypeAdminController {

    //文件上传位置
    @Value("${file.upload.path}")
    private String filePath;

    @Autowired
    private BlogTypeService blogTypeService;


    @RequestMapping("/list")
    @ResponseBody
    public Map list(@RequestParam(value = "page",required = false)String pageNumber,
                    @RequestParam(value = "rows",required = false)String pageSize){
        int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
        HashMap<String, Object> map = new HashMap<>();
        PageBean pageBean = new PageBean(Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("bloggerid",uid);
        List<BlogType> list = blogTypeService.conditionList(map);
        Long total = blogTypeService.getTotal(uid);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows",list);
        resultMap.put("total",total);
        return resultMap;
    }





    @RequestMapping("/saveAndEdit")
    @ResponseBody
    public Boolean save(BlogType blogType, @RequestParam(value = "typeImageFile",required = false) MultipartFile typeImageFile) throws IOException {

        if(! typeImageFile.isEmpty()){
            String filename = typeImageFile.getOriginalFilename();
            String ext = FilenameUtils.getExtension(filename);
            String newName = UUID.randomUUID().toString().replace("-","")+"."+ext;
            String dataPath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File dest = new File(filePath+dataPath+"/"+newName);

            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }

            typeImageFile.transferTo(dest);
            blogType.setTypeImage(dataPath+"/"+newName);
        }

        int result = 0;
        if (blogType.getId() != null) {
            result = blogTypeService.update(blogType);
        } else {
            int uid = ((Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER)).getId();
            Blogger b = new Blogger();
            b.setId(uid);
            blogType.setBlogger(b);
            result = blogTypeService.add(blogType);
        }
        if (result == 1) {
            return true;
        } else {
            return false;
        }

    }

    /*批量删除*/
    @RequestMapping("/delete")
    @ResponseBody
    public Map delete(@RequestParam(value = "ids", required = false) String ids,
                      @RequestParam(value = "id", required = false) String id) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            Integer result = 0;
            if (id != null) {
                result = blogTypeService.delete(Integer.parseInt(id));
            }
            if (ids != null) {
                String[] idStr = ids.split(",");
                int[] arr = new int[idStr.length];
                for (int i = 0; i < idStr.length; i++) {
                    arr[i] = Integer.parseInt(idStr[i]);
                }
                result = blogTypeService.deleteMore(arr);
            }
            map.put("success",true);
            map.put("del",result);
        } catch (Exception e) {
            map.put("success",false);
        }

        return map;
    }
}
