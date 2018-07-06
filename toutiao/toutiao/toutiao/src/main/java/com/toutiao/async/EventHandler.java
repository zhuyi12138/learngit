package com.toutiao.async;

import java.util.List;

//不同的handler对相同model处理方法不同，因而抽象成接口
public interface EventHandler {
	public void doHandle(EventModel model);
	//获取当前handler可以处理哪些eventType
	List<EventType> getSupportEventTypes();
}
