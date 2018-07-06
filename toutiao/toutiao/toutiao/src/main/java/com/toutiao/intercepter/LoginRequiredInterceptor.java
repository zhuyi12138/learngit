package com.toutiao.intercepter;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.toutiao.dao.LoginTicketDAO;
import com.toutiao.dao.UserDAO;
import com.toutiao.model.HostHolder;
import com.toutiao.model.LoginTicket;
import com.toutiao.model.User;

@Component//@Component泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。 
public class LoginRequiredInterceptor implements HandlerInterceptor{

	@Autowired
	private HostHolder hostHolder;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if(hostHolder.getUser() == null){
			response.sendRedirect("/?pop=1");//返回首页
			return false;//不再向下执行（不处理controller）
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}


}
