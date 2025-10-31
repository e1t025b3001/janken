package oit.is.z4272.kaizi.janken.model;

import org.apache.ibatis.annotations.*;
import java.util.ArrayList;

@Mapper
public interface MatchInfoMapper {

  @Insert("""
        INSERT INTO matchinfo (user1,user2,user1_hand,is_active)
        VALUES (#{user1},#{user2},#{user1Hand},TRUE)
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(MatchInfo mi);

  // ★ 映射：把資料表的 user1_hand / is_active 指到 Java 的 user1Hand / isActive
  @Results(id = "MatchInfoMap", value = {
      @Result(column = "id", property = "id"),
      @Result(column = "user1", property = "user1"),
      @Result(column = "user2", property = "user2"),
      @Result(column = "user1_hand", property = "user1Hand"),
      @Result(column = "is_active", property = "isActive")
  })
  @Select("""
        SELECT * FROM matchinfo
        WHERE is_active=TRUE AND user1=#{user1} AND user2=#{user2}
        ORDER BY id DESC LIMIT 1
      """)
  MatchInfo selectActiveByUsers(@Param("user1") int user1, @Param("user2") int user2);

  @ResultMap("MatchInfoMap")
  @Select("""
        SELECT * FROM matchinfo
        WHERE is_active=TRUE AND (user1=#{uid} OR user2=#{uid})
        ORDER BY id DESC
      """)
  ArrayList<MatchInfo> selectActiveByUser(@Param("uid") int uid);

  @Update("UPDATE matchinfo SET is_active=FALSE WHERE id=#{id}")
  void setInactive(int id);
}
