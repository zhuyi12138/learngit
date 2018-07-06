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
public class PassportInterceptor implements HandlerInterceptor{

	@Autowired
	private LoginTicketDAO ticketDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private HostHolder hostHolder;
	/*
	 * 1.当preHandle方法返回false时，从当前拦截器往回执行所有拦截器的afterCompletion方法
	 * 2.当preHandle方法全为true时，执行下一个拦截器,直到所有拦截器执行完。再运行被拦截的Controller。
	 *	然后进入拦截器链，运行所有拦截器的postHandle方法,完后从最后一个拦截器往回执行所有拦截器的afterCompletion方法.
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		//先从cookie中获取ticket
		String ticket = null;
		if(request.getCookies() != null){
			for(Cookie cookie : request.getCookies()){
				if(cookie.getName().equals("ticket")){
					ticket = cookie.getValue();
				}
			}
		}
		//ticket即使不会空，还是要判断其有效性
		if(ticket != null){
			LoginTicket  loginTicket = ticketDAO.selectByTicket(ticket);
			if(loginTicket == null || loginTicket.getStatus() != 0 || loginTicket.getExpired().before(new Date()))
				return true;//表明ticket无效，则不做任何操作，继续向下执行
			//ticket有效（登录信息有效），则将当前登录的user保存在HostHolder中
			//放在hostHolder中而不是request，因为可能service或者其他层会用到user（可以将request传入service，但是这样分层就没有意义了）
			User user = userDAO.selectById(loginTicket.getUserId());
			hostHolder.setUser(user);			
		}
		return true;
	}
	/*
	 * 该方法将在请求处理之后，DispatcherServlet进行视图返回渲染之前进行调用，
	 * 可以在这个方法中对Controller 处理之后的ModelAndView 对象进行操作。 
	 * */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	//该方法是在渲染之前调用，我们将user保存入modelview，就可以在页面上使用user（后端代码和前端渲染的交互）
		if(modelAndView != null && hostHolder.getUser() != null){
			modelAndView.addObject("user", hostHolder.getUser());
		}
	}
	/*
	 * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行，
	 * 该方法将在整个请求结束之后，也就是在DispatcherServlet 渲染了对应的视图之后执行。用于进行资源清理。
	 * */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//收尾工作
		hostHolder.clear();
	}


}
