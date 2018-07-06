package com.toutiao.controller;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.toutiao.model.Comment;
import com.toutiao.model.EntityType;
import com.toutiao.model.HostHolder;
import com.toutiao.model.News;
import com.toutiao.model.User;
import com.toutiao.model.ViewObject;
import com.toutiao.service.CommentService;
import com.toutiao.service.LikeService;
import com.toutiao.service.NewsService;
import com.toutiao.service.QiniuService;
import com.toutiao.service.UserService;
import com.toutiao.util.ToutiaoUtil;

@Controller
public class NewsController {
	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	@Autowired
	NewsService newsService;
	@Autowired
	UserService userService;
	@Autowired
	QiniuService qiniuService;
	@Autowired
	CommentService commentService;
	@Autowired
	HostHolder hostHolder;
	@Autowired
	LikeService likeService;
	//上传图片
	@RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
	@ResponseBody//之前出错因为忘写@ResponseBody,导致返回的是一个.html
	public String uploadImage(@RequestParam("file") MultipartFile image){
		try{
			String url = newsService.uploadImage(image);
			//String url = qiniuService.uploadImage(image);
			if(url == null){
				return ToutiaoUtil.getJSONString(1, "图片上传失败");
			}
			return ToutiaoUtil.getJSONString(0, url);
		}catch(Exception e){
			logger.error(e.getMessage()+"图片上传失败");
			return ToutiaoUtil.getJSONString(1, "图片上传失败");
		}
	}
	//获取图片，将图片数据写入response输出流
	@RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
	@ResponseBody
	public void getImage(@RequestParam("name")String imageName,
			HttpServletResponse response){
		response.setContentType("image/jpeg");
		try {
			//读取文件用inputstream
			StreamUtils.copy(new FileInputStream(ToutiaoUtil.IMAGE_DIR+imageName), response.getOutputStream());
		} catch (Exception e) {
			logger.error("图片获取失败"+e.getMessage());
		}
	}
	//发布news
	@RequestMapping(path={"/user/addNews"},method = {RequestMethod.POST})
	@ResponseBody
	public String addNews(@RequestParam("image")String image,
			@RequestParam("title")String title,
			@RequestParam("link")String link){
		try{
			News news = new News();
			User user = hostHolder.getUser();
			if(user == null){
				//定义0为匿名用户
				news.setUserId(0);
			}else{
				news.setUserId(user.getId());
			}
			news.setImage(image);
			news.setTitle(title);
			news.setLink(link);
			news.setCommentCount(0);
			news.setLikeCount(0);
			news.setCreatedDate(new Date());
			newsService.InsertNews(news);
			return ToutiaoUtil.getJSONString(0, "发布成功");
		}catch(Exception e){
			logger.error("news发布失败"+e.getMessage());
			return ToutiaoUtil.getJSONString(1, "news发布失败");
		}
		
	}
	//news详情
	@RequestMapping(path={"/news/{newsId}"},method = {RequestMethod.GET})
	public String newsDetail(@PathVariable("newsId")int newsId,Model model){
		News news = newsService.getNews(newsId);
		if(news != null){
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            //将赞和踩的信息返回
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }
			//显示评论
			List<Comment> comments = commentService.getCommentsByEntity(news.getId(),EntityType.ENTITY_NEWS);
			List<ViewObject> commentVos = new ArrayList<>();
			for (Comment comment : comments) {
				ViewObject vo = new ViewObject();
				vo.set("comment", comment);
				vo.set("user", userService.SelectUser(comment.getUserId()));
				commentVos.add(vo);
			}
			model.addAttribute("comments", commentVos);
		}
		model.addAttribute("news", news);
		model.addAttribute("owner", userService.SelectUser(news.getUserId()));
		return "detail";
	}
	//发布评论(用户已登录)
	@RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
	public String addComment(@RequestParam("newsId")int newsId,
			@RequestParam("content") String content){
		try{
			//增加评论
			Comment comment = new Comment();
			comment.setContent(content);
			comment.setCreatedDate(new Date());
			comment.setEntityType(EntityType.ENTITY_NEWS);
			comment.setEntityId(newsId);
			comment.setStatus(0);
			comment.setUserId(hostHolder.getUser().getId());
			commentService.addComment(comment);
			//更新评论数
			int count = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
			newsService.updateCommentCount(newsId, count);
			
		}catch(Exception e){
			logger.error("评论发表失败"+e.getMessage());
		}
		return "redirect:/news/"+newsId;
	}
}
