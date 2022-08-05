package cn.jhc.sqq.myblog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sqq
 * @ 2019/9/7 13:45
 */
public class DateUtil {

    public static String getCurrentDateStr(){
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }

    public static String formateDate(Date date,String format){
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if(date!=null){
            result = sdf.format(date);
        }
        return result;
    }
}
