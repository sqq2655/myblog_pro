package cn.jhc.sqq.myblog.controller.admin;

import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.service.BloggerService;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sqq
 * @ 2019/10/27 14:02
 */
@Controller
@RequestMapping("/admin/blogger")
public class BloggerAdminController {

    @Autowired
    private BloggerService bloggerService;

    //文件上传位置
    @Value("${file.upload.path}")
    private String filePath;

    @RequestMapping("/find")
    @ResponseBody
    public Blogger find() {
        Blogger blogger = (Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER);
        return blogger;
    }

    @RequestMapping("/save")
    @ResponseBody
    public Boolean save(Blogger blogger,
                        @RequestParam(value = "imageNameFile",required = false) MultipartFile avatar,
                        Model model) throws IOException {
        if(!avatar.isEmpty()){
            String filename = avatar.getOriginalFilename();
            String ext = FilenameUtils.getExtension(filename);
            String newName = UUID.randomUUID().toString().replace("-","")+"."+ext;
            String dataPath = new SimpleDateFormat("yyyyMMdd").format(new Date());

            File dest = new File(filePath+dataPath+"/"+newName);
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }
            avatar.transferTo(dest);
            model.addAttribute("imageName",dataPath+"/"+newName);
            blogger.setImageName(dataPath+"/"+newName);
        }
        Integer result = bloggerService.update(blogger);
        if(result>0){
            return true;
        }
        return false;
    }

    @RequestMapping("/modifyPwd")
    @ResponseBody
    public Map<String, Object> modifyPassword(Blogger blogger, String oldPassword){

        Map<String, Object> map = new HashMap<>();
        /*当前用户*/
        Blogger currentUser = (Blogger) SecurityUtils.getSubject().getSession().getAttribute(ConstantFied.CURRENT_USER);
//        /*旧密码加密比较*/
        String pwd = new SimpleHash("MD5", oldPassword,
                ByteSource.Util.bytes(currentUser.getUserName()), 1024).toString();
        if (currentUser.getPassword().equals(pwd)) {
            String newPwd = new SimpleHash("MD5", blogger.getPassword(),
                    ByteSource.Util.bytes(currentUser.getUserName()), 1024).toString();
            blogger.setPassword(newPwd);
            Integer result = bloggerService.update(blogger);
            if (result > 0) {
                map.put("success", true);
            } else {
                map.put("err", true);
            }

        } else {
            map.put("oldPwd_err", true);
        }

        return map;
    }
}
