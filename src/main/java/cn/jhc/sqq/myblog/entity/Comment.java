package cn.jhc.sqq.myblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author sqq
 * @ 2019/9/30 12:54
 */
public class Comment {
    private int id;

    private Blogger blogger;

    private String userIp;

    private Blog blog;

    private String content;

    //前端的字符串格式化成日期类型到后端
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    将日期格式化成json到前端
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date commentDate;

    private Integer state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Blogger getBlogger() {
        return blogger;
    }

    public void setBlogger(Blogger blogger) {
        this.blogger = blogger;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", blogger=" + blogger +
                ", userIp='" + userIp + '\'' +
                ", blog=" + blog +
                ", content='" + content + '\'' +
                ", commentDate=" + commentDate +
                ", state=" + state +
                '}';
    }
}
