package com.nowcoder;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.toutiao.ToutiaoApplication;
import com.toutiao.dao.CommentDAO;
import com.toutiao.dao.LoginTicketDAO;
import com.toutiao.dao.NewsDAO;
import com.toutiao.dao.UserDAO;
import com.toutiao.model.Comment;
import com.toutiao.model.EntityType;
import com.toutiao.model.LoginTicket;
import com.toutiao.model.News;
import com.toutiao.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private NewsDAO newsDAO;
    @Autowired
    private LoginTicketDAO ticketDAO;
    @Autowired
    private CommentDAO commentDAO;
    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 1; i <= 10; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);
     	    user.setPassword("password");
     	    userDAO.updateUser(user);
     	    
            News news = new News();
            news.setCommentCount(1);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*5*i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLikeCount(1);
            news.setUserId(i);
            news.setTitle(String.format("TITLE{%d}", i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);
            // 给每个资讯插入一条评论
                Comment comment = new Comment();
                comment.setUserId(i);
                comment.setCreatedDate(new Date());
                comment.setStatus(0);
                comment.setContent("这里是一个评论啊！");
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                commentDAO.addComment(comment);
    
    		LoginTicket loginTicket = new LoginTicket();
    		loginTicket.setUserId(user.getId());
    		loginTicket.setStatus(0);
    		loginTicket.setExpired(date);
    		loginTicket.setTicket(String.format("TICKET%d", i));
    		ticketDAO.addTicket(loginTicket);
    		ticketDAO.updateStatus(1, String.format("TICKET%d", i));
        };
    }

}
