package oit.is.z4272.kaizi.janken.model;

import org.apache.ibatis.annotations.*;

@Mapper
public interface MatchMapper {

  @Insert("""
        INSERT INTO matches (user1,user2,user1_hand,user2_hand,is_active)
        VALUES (#{user1},#{user2},#{user1Hand},#{user2Hand},TRUE)
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(Match m);

  // ★ 映射：user1_hand / user2_hand / is_active → user1Hand / user2Hand / isActive
  @Results(id = "MatchMap", value = {
      @Result(column = "id", property = "id"),
      @Result(column = "user1", property = "user1"),
      @Result(column = "user2", property = "user2"),
      @Result(column = "user1_hand", property = "user1Hand"),
      @Result(column = "user2_hand", property = "user2Hand"),
      @Result(column = "is_active", property = "isActive")
  })
  @Select("""
        SELECT * FROM matches
        WHERE is_active=TRUE AND (user1=#{uid} OR user2=#{uid})
        ORDER BY id DESC LIMIT 1
      """)
  Match selectActiveByUser(@Param("uid") int uid);

  @Update("UPDATE matches SET is_active=FALSE WHERE id=#{id}")
  void setInactive(int id);
}
