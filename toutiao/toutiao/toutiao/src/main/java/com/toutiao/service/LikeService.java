package com.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toutiao.util.JedisAdapter;
import com.toutiao.util.RedisKeyUtil;
/*
 * 处理三种功能
 * 喜欢
 * 不喜欢
 * 判断用户是否喜欢
 * 
 * */
@Service
public class LikeService {

	@Autowired
	JedisAdapter jedisAdapter;
	
	//1:喜欢, 0：无感 ,-1:不喜欢
	public int getLikeStatus(int userId,int entityType,int entityId){
		String likeKey = RedisKeyUtil.getListKey(entityType, entityId);
		String disKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
		if(jedisAdapter.sismember(likeKey, String.valueOf(userId))){
			return 1;
		}
		else return jedisAdapter.sismember(disKey, String.valueOf(userId))?-1:0;
	}
	//赞
	public long like(int userId,int entityType,int entityId){
		String likeKey = RedisKeyUtil.getListKey(entityType, entityId);
		String disKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
		//两步：like中添加 ，dislike中删除
		jedisAdapter.sadd(likeKey, String.valueOf(userId));
		jedisAdapter.srem(disKey, String.valueOf(userId));
		//返回点赞的人数
		return jedisAdapter.scard(likeKey);
	}
	//踩
	public long dislike(int userId,int entityType,int entityId){
		String likeKey = RedisKeyUtil.getListKey(entityType, entityId);
		String disKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
		//两步：like中添加 ，dislike中删除
		jedisAdapter.sadd(disKey, String.valueOf(userId));
		jedisAdapter.srem(likeKey, String.valueOf(userId));
		//返回点赞的人数
		return jedisAdapter.scard(likeKey);
	}
}
