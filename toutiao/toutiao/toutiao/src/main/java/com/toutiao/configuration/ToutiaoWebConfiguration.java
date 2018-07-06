package com.toutiao.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.toutiao.intercepter.LoginRequiredInterceptor;
import com.toutiao.intercepter.PassportInterceptor;

@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter{

	@Autowired
	PassportInterceptor passportInterceptor;
	@Autowired
	LoginRequiredInterceptor loginRequiredInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册拦截器
		//先注册的先执行pre
		registry.addInterceptor(passportInterceptor);//先注册将user存入hostHolder
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
		super.addInterceptors(registry);
	}
	
	
	
}
