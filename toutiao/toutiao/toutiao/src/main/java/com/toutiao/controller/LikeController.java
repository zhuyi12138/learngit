package com.toutiao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toutiao.async.EventModel;
import com.toutiao.async.EventProducer;
import com.toutiao.async.EventType;
import com.toutiao.model.EntityType;
import com.toutiao.model.HostHolder;
import com.toutiao.service.LikeService;
import com.toutiao.service.NewsService;
import com.toutiao.util.ToutiaoUtil;

@Controller
public class LikeController {
	private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
	@Autowired
	HostHolder hostHolder;
	@Autowired
	LikeService likeService;
	@Autowired
	NewsService newsService;
	@Autowired
	EventProducer eventProducer;
	
	@RequestMapping(path = {"/like"},method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String like(@RequestParam("newsId")int newsId){
		try{
			long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
			newsService.updateLikeCount(newsId,(int)likeCount);
			
			/*
			 * 点赞后触发异步事件
			 * */
			eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
					.setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS)
					.setEntityOwnerId(newsService.getNews(newsId).getUserId()));
			
			return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
		}catch(Exception e){
			logger.error("点赞失败"+e.getMessage());
			return ToutiaoUtil.getJSONString(1, "点赞失败");
		}
	}
	@RequestMapping(path = {"/dislike"},method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String dislike(@RequestParam("newsId")int newsId){
		try{
			System.out.println("dislike");
			long likeCount = likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
			newsService.updateLikeCount(newsId,(int)likeCount);
			return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
		}catch(Exception e){
			logger.error("踩失败"+e.getMessage());
			return ToutiaoUtil.getJSONString(1, "踩失败");
		}
	}
}
