package com.toutiao.async;

import java.util.HashMap;
import java.util.Map;

//异步化队列中的事件
public class EventModel {
	private EventType type;
	private int actorId;
	private int entityType;
	private int entityId;
	private int entityOwnerId;
	//事件扩展信息
	private Map<String,String> exts = new HashMap<>();
	
	public EventModel() {		
	}
	public EventModel(EventType type){
		this.type =type;
	}
	public EventType getType() {
		return type;
	}
	public EventModel setType(EventType type) {
		this.type = type;
		return this;
	}
	public int getActorId() {
		return actorId;
	}
	public EventModel setActorId(int actorId) {
		this.actorId = actorId;
		return this;
	}
	public int getEntityType() {
		return entityType;
	}
	public EventModel setEntityType(int entityType) {
		this.entityType = entityType;
		return this;
	}
	public int getEntityId() {
		return entityId;
	}
	public EventModel setEntityId(int entityId) {
		this.entityId = entityId;
		return this;
	}
	public int getEntityOwnerId() {
		return entityOwnerId;
	}
	public EventModel setEntityOwnerId(int entityOwnerId) {
		this.entityOwnerId = entityOwnerId;
		return this;
	}
	public Map<String, String> getExts() {
		return exts;
	}
	public EventModel setExts(Map<String, String> exts) {
		this.exts = exts;
		return this;
	}
	public EventModel setExt(String string, String string2) {
		// TODO Auto-generated method stub
		exts.put(string, string2);
		return this;
	}
	public String getExt(String string) {
		// TODO Auto-generated method stub
		return exts.get(string);
	}
	
}
