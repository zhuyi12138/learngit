package com.toutiao.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.toutiao.util.JedisAdapter;
import com.toutiao.util.RedisKeyUtil;

/*
 * consumer的任务就是开启线程处理异步队列中的事件
 * */

/*ApplicationContextAware:Spring加载配置文件是会自动调用方法，获得ApplicationContext对象*/
/*InitializingBean 实现类实例化后初始化bean*/
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	//上下文
	private ApplicationContext application = null;
	//要统一管理：每种eventType对应哪些handler，这样就可以当处理eventmodel的时候可以知道调用哪些handler
	private Map<EventType,List<EventHandler>> config = new HashMap<>();
	
	@Autowired
	JedisAdapter jedisAdapter;
	
	@Override
	public void setApplicationContext(ApplicationContext application)
			throws BeansException {
		// TODO Auto-generated method stub
		this.application = application;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//获取所有注册了的handler
		Map<String, EventHandler> beans = application.getBeansOfType(EventHandler.class);
		//遍历handler，初始化Map<EventType,List<EventHandler>>
		for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
			//handler支持的eventtypes
			List<EventType> supportEventTypes = entry.getValue().getSupportEventTypes();
			for (EventType eventType : supportEventTypes) {
				if(!config.containsKey(eventType)){
					config.put(eventType, new ArrayList<EventHandler>());
				}
				config.get(eventType).add(entry.getValue());
			}
		}
	
		//实现runnable接口，为了使线程能够执行run()方法，需要在Thread类的构造函数中传入 Runnable的实例对象
		//或者是创建一个Thread子类的实例
	new Thread(new Runnable(){
		
		@Override
		public void run() {
	
				// 无限循环 ，不断读取阻塞队列中的事件
				while(true){
					String key = RedisKeyUtil.getEventQueueKey();
					List<String> jsons = jedisAdapter.brpop(0, key);
					for (String json : jsons) {
						//第一个string是队列名
						if(json.equals(key)){
							continue;
						}
						EventModel model = JSON.parseObject(json, EventModel.class);
						if(!config.containsKey(model.getType())){
							logger.error("无法识别当前evenType");
							continue;
						}
						//获取modelType对应的所有handler
						List<EventHandler> handlers = config.get(model.getType());
						for (EventHandler handler : handlers) {
							handler.doHandle(model);
						}
					}
				}
	
		}
		
	}).start();
	
   
	}
}



