package com.toutiao.async.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toutiao.async.EventHandler;
import com.toutiao.async.EventModel;
import com.toutiao.async.EventType;
import com.toutiao.model.HostHolder;
import com.toutiao.service.MailService;
import com.toutiao.service.UserService;

@Component
public class LoginHandler implements EventHandler {
	private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
	@Autowired
	HostHolder hostHolder;
	@Autowired
	UserService userService;
	@Autowired
	MailService mailService;

	@Override
	public void doHandle(EventModel model) {
		// TODO Auto-generated method stub
		System.out.println("LOGIN");
		//如果登录则发送一封通知邮件
		try{
			Map<String, Object> map = new HashMap<>();
			map.put("username", model.getExt("username"));
			//map就相当于渲染模板的model
			mailService.sendWithHTMLTemplate(model.getExt("to"), "登录提醒", "mails/welcome.html", map);
		}catch(Exception e){
			logger.error("系统消息发送失败"+e.getMessage());
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
        return Arrays.asList(EventType.LOGIN);
	}

}
