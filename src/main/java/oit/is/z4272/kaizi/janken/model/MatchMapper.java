package oit.is.z4272.kaizi.janken.model;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {

  @Insert("INSERT INTO matches(user1, user2, user1Hand, user2Hand) "
      + "VALUES(#{user1}, #{user2}, #{user1Hand}, #{user2Hand})")
  void insert(Match m);

  @Select("SELECT m.id, u1.name AS user1Name, u2.name AS user2Name, m.user1Hand, m.user2Hand "
      + "FROM matches m "
      + "JOIN users u1 ON m.user1 = u1.id "
      + "JOIN users u2 ON m.user2 = u2.id "
      + "ORDER BY m.id DESC")
  ArrayList<MatchView> selectViews();
}
