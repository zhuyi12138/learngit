package com.toutiao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.toutiao.model.News;

@Mapper
public interface NewsDAO {
	String TABLE_NAME = "news";
	String SELECT_FILED = "id,title,link,image,like_count,comment_count,created_date,user_id";
	String INSERT_FILED = "title,link,image,like_count,comment_count,created_date,user_id";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FILED,") values(#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
	int addNews(News news);
	@Select({"select",SELECT_FILED,"from",TABLE_NAME,"where id=#{newsId}"})
	News selectNews(int newsId);
    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
	public List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
            @Param("limit") int limit);
    @Update({"update ", TABLE_NAME, " set like_count = #{likeCount} where id=#{newsId}"})
	void updateLikeCount(@Param("newsId")int newsId,@Param("likeCount") int likeCount);
}
