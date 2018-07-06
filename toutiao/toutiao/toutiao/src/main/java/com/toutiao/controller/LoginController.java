package com.toutiao.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toutiao.async.EventModel;
import com.toutiao.async.EventProducer;
import com.toutiao.async.EventType;
import com.toutiao.service.UserService;
import com.toutiao.util.ToutiaoUtil;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	UserService userService;
	@Autowired
	EventProducer eventProducer;
	
    //用户注册
    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String regist(Model model, @RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@RequestParam(value = "rember",defaultValue = "0") int rememberMe,
    		HttpServletResponse response) {
    	try{
            Map<String, Object> map = userService.RegistUser(username,password);
            //map中包含ticket表示已登录（注册成功）
            if(map.containsKey("ticket")){
            	Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            	cookie.setPath("/");//设置cookie全站有效
            	if(rememberMe > 0){
            		cookie.setMaxAge(5*24*3600);//cookie过期时间为5天（单位为秒）否则浏览器关闭就失效了
            	}
            	response.addCookie(cookie);
            	return ToutiaoUtil.getJSONString(0, "注册成功");
            }else{
            	//返回错误信息
            	return ToutiaoUtil.getJSONString(1, map);
            }
    	}catch(Exception e){
    		logger.error("注册失败"+e.getMessage());
    		return ToutiaoUtil.getJSONString(1, "注册失败");
    	}
    }
    //用户登录
    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@RequestParam(value = "rember",defaultValue = "0") int rememberMe,
    		HttpServletResponse response) {
    	try{
            Map<String, Object> map = userService.LoginUser(username,password);
            //map中包含ticket表示已登录（注册成功）
            if(map.containsKey("ticket")){
            	Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            	cookie.setPath("/");//设置cookie全站有效
            	if(rememberMe > 0){
            		cookie.setMaxAge(5*24*3600);//cookie过期时间为5天（单位为秒）否则浏览器关闭就失效了
            	}
            	response.addCookie(cookie);
            	/*
            	 * 异步化：如果登录则系统发送一封通知邮件
            	 * */
               /* eventProducer.fireEvent(new
                        EventModel(EventType.LOGIN).setExt("username", username).setExt("to", "806762273@qq.com"));
            	*/
            	return ToutiaoUtil.getJSONString(0, "登录成功");
            }else{
            	//返回错误信息
            	return ToutiaoUtil.getJSONString(1, map);
            }
    	}catch(Exception e){
    		logger.error("登录失败"+e.getMessage());
    		return ToutiaoUtil.getJSONString(1, "登录失败");
    	}
    }
   //用户登出
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket")String ticket){
    	userService.logout(ticket);
    	return "redirect:/";
    }

}
