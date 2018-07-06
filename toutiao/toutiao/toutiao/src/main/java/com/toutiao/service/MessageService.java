package com.toutiao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toutiao.dao.MessageDAO;
import com.toutiao.model.Message;

@Service
public class MessageService {

	@Autowired
	MessageDAO messageDAO;
	
	public int addMessage(Message message){
		return messageDAO.addMessge(message);
	}
	public List<Message> showConversationDetails(String conversationId,int offset,int limit){
		return messageDAO.getConversationDetail(conversationId, offset, limit);
	}
	public List<Message> getConversationList(int userId, int offset, int limit){
		return messageDAO.getConversationList(userId,offset,limit);
	}
	public int getUnReadCount(int userId, String conversationId) {
		// TODO Auto-generated method stub
		return messageDAO.getUnReadCount(userId,conversationId);
	}
	public int getConversationTotalCount(String conversationId) {
		// TODO Auto-generated method stub
		return messageDAO.getConversationTotalCount(conversationId);
	}
	public void updateHasRead(int id, String conversationId, int flag) {
		messageDAO.updateHasRead(id,conversationId,flag);
	}

}
