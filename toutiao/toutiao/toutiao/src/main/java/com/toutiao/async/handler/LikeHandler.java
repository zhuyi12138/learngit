package com.toutiao.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toutiao.async.EventConsumer;
import com.toutiao.async.EventHandler;
import com.toutiao.async.EventModel;
import com.toutiao.async.EventType;
import com.toutiao.model.HostHolder;
import com.toutiao.model.Message;
import com.toutiao.service.MessageService;
import com.toutiao.service.UserService;

@Component
public class LikeHandler implements EventHandler {
	private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
	@Autowired
	HostHolder hostHolder;
	@Autowired
	UserService userService;
	@Autowired
	MessageService messageService;

	@Override
	public void doHandle(EventModel model) {
		// TODO Auto-generated method stub
		System.out.println("LIKE");
		//如果有人点赞就给咨询分享人发送站内信
		try{
			
			Message msg = new Message();
			msg.setContent("用户"+hostHolder.getUser().getName()+"给你的分享,http://localhost:8080/news/"+model.getEntityId()+"点了赞");
			msg.setCreatedDate(new Date());
			msg.setFromId(14);
			msg.setToId(model.getEntityOwnerId());
			msg.setHasRead(0);//0未读，1已读
			msg.setConversationId(14 < model.getEntityOwnerId()?String.format("%d_%d", 14,model.getEntityOwnerId()):String.format("%d_%d", model.getEntityOwnerId(),14));
			messageService.addMessage(msg);
		}catch(Exception e){
			logger.error("系统消息发送失败"+e.getMessage());
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
        return Arrays.asList(EventType.LIKE);
	}

}
