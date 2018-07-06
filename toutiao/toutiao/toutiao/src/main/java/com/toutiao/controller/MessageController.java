package com.toutiao.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toutiao.model.HostHolder;
import com.toutiao.model.Message;
import com.toutiao.model.User;
import com.toutiao.model.ViewObject;
import com.toutiao.service.MessageService;
import com.toutiao.service.UserService;
import com.toutiao.util.ToutiaoUtil;

@Controller
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	MessageService messageService;
	@Autowired
	UserService userService;
	@Autowired
	HostHolder hostHolder;

    //发送站内信
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId")int fromId,
    		@RequestParam("toId")int toId,
    		@RequestParam("content")String content) {
    	try{
    		Message msg = new Message();
    		msg.setContent(content);
    		msg.setCreatedDate(new Date());
    		msg.setFromId(fromId);
    		msg.setToId(toId);
    		msg.setHasRead(0);//0未读，1已读
    		msg.setConversationId(fromId < toId?String.format("%d_%d", fromId,toId):String.format("%d_%d", toId,fromId));
    		messageService.addMessage(msg);
    		return ToutiaoUtil.getJSONString(0, "消息发型成功");
    	}catch(Exception e){
    		logger.error("消息发送失败"+e.getMessage());
    		return ToutiaoUtil.getJSONString(1, "消息发送失败");
    	}
    }
    //展示两个人的具体消息（同一个conversation_id对应的所有message）
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(@RequestParam("conversationId")String conversationId,Model model){
    	try{
    		List<Message> list = messageService.showConversationDetails(conversationId, 0, 10);
    		List<ViewObject> vos = new ArrayList<>();
    		for(Message msg : list){
    			ViewObject vo = new ViewObject();
    			vo.set("message", msg);
    			//发送者
    			User user = userService.SelectUser(msg.getFromId());
    			if(user == null){
    				continue;//匿名用户
    			}else{
    				vo.set("headUrl", user.getHeadUrl());
    				vo.set("userName", user.getName());
    			}
    			vos.add(vo);
    		}
    		model.addAttribute("messages",vos);
    		//读取详情后应该更新未读数
    		messageService.updateHasRead(hostHolder.getUser().getId(),conversationId,1);
    	}catch(Exception e){
    		logger.error("获取消息详情失败"+e.getMessage());
    	}
		return "letterDetail";
    }
    //展示一个人所有站内信
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model){
    	try{
    		User cur = hostHolder.getUser();
    		List<Message> list = messageService.getConversationList(cur.getId(),0,10);
    		List<ViewObject> vos = new ArrayList<>();
    		for(Message msg : list){
    			ViewObject vo = new ViewObject();
    			//获取消息中的另一方
    			int targetId = cur.getId() == msg.getFromId()?msg.getToId():msg.getFromId();
    			User target = userService.SelectUser(targetId);
    			vo.set("conversation", msg);
    			vo.set("headUrl", target.getHeadUrl());
    			//需要显示另一方的用户名，头像
    			vo.set("userName", target.getName());
    			vo.set("targetId", targetId);
    			vo.set("totalCount",messageService.getConversationTotalCount(msg.getConversationId()));
    			vo.set("unreadCount", messageService.getUnReadCount(cur.getId(),msg.getConversationId()));
    			vos.add(vo);
    		}
    		model.addAttribute("conversations", vos);
    		
    	}catch(Exception e){
    		logger.error("获取消息列表失败"+e.getMessage());
    	}
    	return "letter";
    }
}
