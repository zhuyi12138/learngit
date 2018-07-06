package com.toutiao.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.toutiao.model.User;

@Mapper
public interface UserDAO {
	String TABLE_NAME = "user";
	String SELECT_FILED = "id,name,password,salt,head_url";
	String INSERT_FILED = "name,password,salt,head_url";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FILED,") values(#{name},#{password},#{salt},#{headUrl})"})
	int addUser(User user);
	@Select({"select",SELECT_FILED,"from",TABLE_NAME,"where id = #{id}"})
	User selectById(int id);
	@Select({"select",SELECT_FILED,"from",TABLE_NAME,"where name = #{name}"})
	User selectByName(String name);
	@Update({"update",TABLE_NAME,"set password = #{password} where id = #{id}"})
	void updateUser(User user);
	@Delete({"delete from",TABLE_NAME,"where id = #{id}"})
	void deleteById(int id);
}
