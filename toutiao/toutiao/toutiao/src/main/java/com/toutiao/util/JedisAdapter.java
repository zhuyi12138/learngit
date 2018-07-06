package com.toutiao.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;

@Component
public class JedisAdapter implements InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
	private JedisPool pool = null;
	@Override
	public void afterPropertiesSet() throws Exception {
		pool = new JedisPool("localhost",6379);//默认值也是这个;
	}
	//获取k-v中的value
	public String get(String key){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			return jedis.get(key);
		}catch(Exception e){
			logger.error("value获取失败"+e.getMessage());
			return null;
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	//添加k-v
	public void set(String key,String value){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			jedis.set(key, value);
		}catch(Exception e){
			logger.error("k-v添加失败"+e.getMessage());
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}	
	//向阻塞队列中添加value
	public void lpush(String key,String value){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			jedis.lpush(key, value);
		}catch(Exception e){
			logger.error("k-v添加失败"+e.getMessage());
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	
	//向阻塞队列尾部取出value
	public List<String> brpop(int timeout,String key){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			return jedis.brpop(timeout,key);
		}catch(Exception e){
			logger.error("k-v添加失败"+e.getMessage());
			return null;
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	
	//向集合key中添加元素value
	public void sadd(String key,String value){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			jedis.sadd(key, value);
		}catch(Exception e){
			logger.error("集合元素添加失败"+e.getMessage());
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	//向集合key中删除元素value
	public void srem(String key,String value){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			jedis.srem(key, value);
		}catch(Exception e){
			logger.error("集合元素删除失败"+e.getMessage());
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	
	//判断是否为集合中元素
	public boolean sismember(String key,String value){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			return jedis.sismember(key, value);
		}catch(Exception e){
			logger.error("判断发生异常"+e.getMessage());
			return false;
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	//获取集合中元素数量
	public long scard(String key){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			return jedis.scard(key);
		}catch(Exception e){
			logger.error("判断发生异常"+e.getMessage());
			return 0;
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
	}
	//利用json序列化和反序列化，将object存入set，实现异步化队列
	public void setObject(String key,Object obj){
		set(key,JSON.toJSONString(obj));
	}
	public <T>T getObject(String key,Class<T> clz){
		String value = get(key);
		if(value != null){
			return JSON.parseObject(value, clz);
		}
		return null;
	}
}
