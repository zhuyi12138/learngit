package com.toutiao.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.toutiao.model.LoginTicket;
import com.toutiao.model.User;

@Mapper
public interface LoginTicketDAO {
	String TABLE_NAME = "login_ticket";
	String SELECT_FILED = "id,user_id,ticket,expired,status";
	String INSERT_FILED = "user_id,ticket,expired,status";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FILED,") values(#{userId},#{ticket},#{expired},#{status})"})
	int addTicket(LoginTicket loginTicket);
	@Select({"select",SELECT_FILED,"from",TABLE_NAME,"where ticket = #{ticket}"})
	LoginTicket selectByTicket(String ticket);
	//用户登出的时候需要更新
	@Update({"update",TABLE_NAME,"set status = #{status} where ticket = #{ticket}"})
	void updateStatus(@Param("status")int status,@Param("ticket") String ticket);

}
