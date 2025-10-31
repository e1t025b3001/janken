package oit.is.z4272.kaizi.janken.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z4272.kaizi.janken.model.Match;
import oit.is.z4272.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {

  @Autowired
  MatchMapper matchMapper;

  @Async
  public void watch(SseEmitter emitter, int myId) {
    try {
      for (int i = 0; i < 120; i++) { // 最多等120秒
        Match m = matchMapper.selectActiveByUser(myId);
        if (m != null) {
          String res = judge(m.getUser1Hand(), m.getUser2Hand());
          String json = String.format(
              "{\"user1Hand\":\"%s\",\"user2Hand\":\"%s\",\"result\":\"%s\"}",
              m.getUser1Hand(), m.getUser2Hand(), res);
          emitter.send(SseEmitter.event().id("kekka").data(json));
          matchMapper.setInactive(m.getId()); // 試合の結果が表示されれば FALSE に更新
          emitter.complete();
          return;
        }
        Thread.sleep(1000);
      }
      emitter.complete();
    } catch (IOException | InterruptedException e) {
      emitter.complete();
      Thread.currentThread().interrupt();
    }
  }

  private String judge(String a, String b) {
    if (a.equals(b))
      return "引き分け";
    if (a.equals("Gu") && b.equals("Choki"))
      return "user1の勝ち";
    if (a.equals("Choki") && b.equals("Pa"))
      return "user1の勝ち";
    if (a.equals("Pa") && b.equals("Gu"))
      return "user1の勝ち";
    return "user2の勝ち";
  }
}
