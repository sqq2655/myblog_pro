package cn.jhc.sqq.myblog.utils;

/**
 * @author sqq
 * @ 2019/8/29 14:22
 */
public class PageUtil {

    /*
    <li><a href='/index.html?page=1&'>首页</a></li>
    <li class='disabled'><a href='#'>上一页</a></li>
    <li class='active'><a href='/index.html?page=1&'>1</a></li>
    <li class='disabled'><a href='#'>下一页</a></li>
    <li><a href='/index.html?page=1&'>尾页</a></li>
    */

    public static String getPagination(String targetUrl,long totalNum,int currentPage,int pageSize,String param){
        StringBuffer pageCode = new StringBuffer();
        /*总页数*/
        if(totalNum==0){
            return "<h3 class='text-center'>暂无博客</h3>";
        }
        long totalPage = 1;
        if(totalNum%pageSize==0){
            totalPage = totalNum/pageSize;
        }else{
            totalPage = totalNum/pageSize+1L;
        }
        pageCode.append("<li><a href='"+targetUrl+"?page=1&"+param+"'>首页</a></li>");

        if(currentPage>1){
            /*当前页不是上一页，显示上一页*/
            pageCode.append("<li><a href='" + targetUrl+"?page=" + (currentPage-1) + "&" + param + "'>上一页</a></li>");
        }else{
            /*当前页是首页，不能点击上一页*/
            pageCode.append("<li class='disabled'><a>上一页</a></li>");
        }

        for (int i = 1; i <= totalPage; i++) {
            if(currentPage==i){
                pageCode.append("<li class='active'><a href='"+targetUrl+"?page="+i+"&"+param+"'>"+i+"</a></li>");
            }else{
                pageCode.append("<li><a href='"+targetUrl+"?page="+i+"&"+param+"'>"+i+"</a></li>");

            }
        }

        if(currentPage<totalPage){
            /*当前页不是下一页，显示下一页*/
            pageCode.append("<li><a href='" + targetUrl+"?page=" + (currentPage+1) + "&" + param + "'>下一页</a></li>");
        }else{
            /*当前页是尾页，不能点击下一页*/
            pageCode.append("<li class='disabled'><a>下一页</a></li>");
        }

        /*尾页*/
        pageCode.append("<li><a href='"+targetUrl+"?page="+totalPage+"&"+param+"'>尾页</a></li>");
        return pageCode.toString();
    }
}
