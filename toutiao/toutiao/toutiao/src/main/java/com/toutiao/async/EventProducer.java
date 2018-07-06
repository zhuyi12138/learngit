package com.toutiao.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.toutiao.util.JedisAdapter;
import com.toutiao.util.RedisKeyUtil;

@Service
/*
 * 事件触发者，唯一任务就是将model放入队列
 * */
public class EventProducer {

	@Autowired
	JedisAdapter jedisAdapter;
	
	public boolean fireEvent(EventModel model){
		try{
			String key = RedisKeyUtil.getEventQueueKey();
			String json = JSON.toJSONString(model);
			jedisAdapter.lpush(key, json);
			return true;
		}catch(Exception e){
			return false;
		}

	}
	
}
