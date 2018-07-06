package com.toutiao.model;

import org.springframework.stereotype.Component;
/*
 * 用来存放当前登录的用户
 * */
@Component
public class HostHolder {
	//ThreadLocal 主要用来提供线程局部变量，也就是变量只对当前线程可见
	private static final ThreadLocal<User> users = new ThreadLocal<>();
	
	public User getUser(){
		return users.get();
	}
	public void setUser(User user){
		users.set(user);
	}
	public void clear(){
		users.remove();
	}
}
