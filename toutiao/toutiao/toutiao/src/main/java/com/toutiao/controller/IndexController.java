package com.toutiao.controller;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/*
 * 这只是个测试controller
 * */
@Controller
public class IndexController {

	@RequestMapping(path={"/setting"})
	@ResponseBody
	public String index(){
		return "setting:ok";
	}
	@RequestMapping(value="/index/{groupId}/{userId}")
	@ResponseBody
	/*
	 * http://localhost:8080/index/01/002?name=zhuyi&age=6
	 * 显示{01},{2},{zhuyi},{6}
	 * */
	public String index(@PathVariable("groupId") String groupId,
			@PathVariable("userId") int userId,
			@RequestParam(value="name") String name,
			@RequestParam(value="age") int age){
		return String.format("{%s},{%d},{%s},{%d}",groupId,userId,name,age);
	}
	@RequestMapping(path={"vm"})
	/*
	 * 无responsebody注解
	 * */
	public String hellovm(Model model){
		model.addAttribute("value", "vvvvv");
		List<String> colors = Arrays.asList(new String[]{"red","white","green","yellow"});
		model.addAttribute("colors",colors);
		Map<Integer,String> map = new HashMap<>();
		map.put(1, "a");map.put(2, "b");map.put(3, "c");
		model.addAttribute("map", map);
		//自动补充后缀.vm
		return "hello";
	}
	@RequestMapping("/request")
	@ResponseBody
	public String request(HttpServletRequest request,
			HttpServletResponse response,HttpSession session){
		Enumeration<String> headnames = request.getHeaderNames();
		StringBuilder sb = new StringBuilder();
		while(headnames.hasMoreElements()){
			String name = headnames.nextElement();
			sb.append(name + ":"+request.getHeader(name)+"<br>");//名称:内容
		}
		return sb.toString();
	}
	@RequestMapping("/redirect")
	public String redirect(HttpSession session){
		session.setAttribute("msg", "lalalalala");
		return "redirect:/";//重定向到首页（地址栏变化）
	}
	@RequestMapping("/admin")
	@ResponseBody
	public String admin(@RequestParam(value = "key",required =false)String key) throws Exception{
		if("admin".equals(key))
			return "Hello admin!";
		else 
			throw new Exception("key错误");
	}
	@ExceptionHandler
	@ResponseBody
	public String error(Exception e){
		return "Error"+":"+e.getMessage();
	}
}
