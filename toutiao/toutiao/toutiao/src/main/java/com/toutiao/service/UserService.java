package com.toutiao.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toutiao.dao.LoginTicketDAO;
import com.toutiao.dao.UserDAO;
import com.toutiao.model.LoginTicket;
import com.toutiao.model.User;
import com.toutiao.util.ToutiaoUtil;

@Service
public class UserService {
	@Autowired
	private UserDAO userDao;
	@Autowired
	private LoginTicketDAO ticketDAO;
	//用户注册
	public Map<String, Object> RegistUser(String username,String password){
		Map<String,Object> map = new HashMap<>();
		//合法性检测
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
		User user = userDao.selectByName(username);
		if(user != null){
            map.put("msgname", "用户名已被注册");
            return map;
		}
		user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0,5));
		user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));//md5加密
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		userDao.addUser(user);
		
		//注册完直接登录
		String ticket = addTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}
	//用户登录
	public Map<String, Object> LoginUser(String username, String password) {
		Map<String,Object> map = new HashMap<>();
		//合法性检测
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
		User user = userDao.selectByName(username);
		if(user == null){
            map.put("msgname", "用户名不存在");
            return map;
		}
		//验证密码是否正确
		if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
			map.put("msgpwd", "密码错误");
			return map;
		}
		
		String ticket = addTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}
	//用户登出
	public void logout(String ticket){
		ticketDAO.updateStatus(1, ticket);
	}
	//用户登录时需要添加ticket
	private String addTicket(int userId) {
		LoginTicket ticket = new LoginTicket();
		ticket.setUserId(userId);
		ticket.setStatus(0);
		Date date = new Date();
		date.setTime(date.getTime()+1000*3600*24);//当前时间推迟24小时，单位毫秒
		ticket.setExpired(date);
		ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		ticketDAO.addTicket(ticket);
		return ticket.getTicket();
	}
	public User SelectUser(int id){
		return userDao.selectById(id);
	}
	public void UpdateUser(User user){
		userDao.updateUser(user);
	}
	public void DeleteById(int id){
		userDao.deleteById(id);
	}

}
