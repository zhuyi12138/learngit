package com.toutiao.dao;


import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.toutiao.model.Comment;
import com.toutiao.model.Message;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id,to_id, content, has_read,created_date,conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{createdDate},#{conversationId})"})
    int addMessge(Message message);

    //获取两人的所有对话
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit} "})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);
    //获取当前用户的所有消息
    @Select({"select ", SELECT_FIELDS, " ,count(id) from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc limit #{offset},#{limit}"})
	List<Message> getConversationList(@Param("userId")int userId, @Param("offset")int offset,@Param("limit") int limit);
    @Select({"select count(id) from",TABLE_NAME,"where has_read = 0 and to_id=#{userId} and conversation_id=#{conversationId}"})
	int getUnReadCount(@Param("userId")int userId, @Param("conversationId")String conversationId);
    @Select({"select count(id) from",TABLE_NAME,"where conversation_id=#{conversationId}"})
	int getConversationTotalCount(@Param("conversationId")String conversationId);
    @Update({"update",TABLE_NAME,"set has_read = #{hasRead} where to_id = #{toId} and conversation_id =#{conversationId}"})
	void updateHasRead(@Param("toId")int userId,@Param("conversationId") String conversationId, @Param("hasRead")int hasRead);
}
