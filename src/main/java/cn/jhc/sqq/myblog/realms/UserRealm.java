package cn.jhc.sqq.myblog.realms;


import cn.jhc.sqq.myblog.constant.ConstantFied;
import cn.jhc.sqq.myblog.entity.Blogger;
import cn.jhc.sqq.myblog.service.BloggerService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sqq
 * @ 2019/10/22 17:46
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private BloggerService bloggerService;


    public Blogger getBlogger(String userName){
        return bloggerService.findBloggerByName(userName);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权策略");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String userName = (String) principalCollection.getPrimaryPrincipal();
        Blogger blogger = getBlogger(userName);
        if(blogger != null){
            simpleAuthorizationInfo.addStringPermission(blogger.getPerms());
            return simpleAuthorizationInfo;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("开始认证");
        String userName = (String) authenticationToken.getPrincipal();
        Blogger blogger = bloggerService.findBloggerByName(userName);
        ByteSource salt = ByteSource.Util.bytes(userName);
        if(blogger != null){
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(blogger.getUserName(),blogger.getPassword(),salt,getName());
            return authenticationInfo;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(new SimpleHash("MD5","qwe1234","yk1",1024));
    }
}
