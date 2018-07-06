package com.toutiao.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
	private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
	
	@Before("execution(* com.toutiao.controller.IndexController.*(..))")
	public void before(JoinPoint point){
		StringBuilder sb = new StringBuilder();
		for(Object o : point.getArgs()){
			sb.append(o.toString()+"|");
		}
		logger.info("before:"+sb.toString());
	}
	@After("execution(* com.toutiao.controller.IndexController.*(..))")
	public void after(){
		logger.info("after:");
	}
}
