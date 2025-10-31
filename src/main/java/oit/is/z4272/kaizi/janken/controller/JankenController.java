package oit.is.z4272.kaizi.janken.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z4272.kaizi.janken.model.User;
import oit.is.z4272.kaizi.janken.model.UserMapper;
import oit.is.z4272.kaizi.janken.model.MatchInfo;
import oit.is.z4272.kaizi.janken.model.MatchInfoMapper;
import oit.is.z4272.kaizi.janken.model.Match;
import oit.is.z4272.kaizi.janken.model.MatchMapper;
import oit.is.z4272.kaizi.janken.service.AsyncKekka;

@Controller
public class JankenController {

  @Autowired
  UserMapper userMapper;
  @Autowired
  MatchInfoMapper matchInfoMapper;
  @Autowired
  MatchMapper matchMapper;
  @Autowired
  AsyncKekka asyncKekka;

  @GetMapping("/janken")
  public String janken(ModelMap model, Principal prin) {
    String loginName = prin.getName();
    User me = userMapper.selectByName(loginName);
    ArrayList<User> users = userMapper.selectAll();
    ArrayList<MatchInfo> actives = matchInfoMapper.selectActiveByUser(me.getId());
    model.addAttribute("loginUser", me);
    model.addAttribute("users", users);
    model.addAttribute("activeInfos", actives);
    return "janken.html";
  }

  @GetMapping("/match")
  public String match(@RequestParam Integer id, ModelMap model, Principal prin) {
    String loginName = prin.getName();
    User me = userMapper.selectByName(loginName);
    User opp = userMapper.selectById(id);
    model.addAttribute("loginUser", me);
    model.addAttribute("opponent", opp);
    return "match.html";
  }

  @GetMapping("/fight")
  @Transactional
  public String fight(@RequestParam Integer id, @RequestParam String hand, ModelMap model, Principal prin) {
    String loginName = prin.getName();
    User me = userMapper.selectByName(loginName);
    User opp = userMapper.selectById(id);

    // 對方是否已先出手？
    MatchInfo oppFirst = matchInfoMapper.selectActiveByUsers(opp.getId(), me.getId());
    if (oppFirst != null) {
      // 生成最終對戰，並關閉 matchinfo（講義：終われば FALSE）:contentReference[oaicite:11]{index=11}
      Match m = new Match();
      m.setUser1(opp.getId());
      m.setUser2(me.getId());
      m.setUser1Hand(oppFirst.getUser1Hand());
      m.setUser2Hand(hand);
      matchMapper.insert(m);
      matchInfoMapper.setInactive(oppFirst.getId());
    } else {
      MatchInfo mi = new MatchInfo();
      mi.setUser1(me.getId());
      mi.setUser2(opp.getId());
      mi.setUser1Hand(hand);
      matchInfoMapper.insert(mi);
    }

    model.addAttribute("loginUser", me);
    model.addAttribute("opponent", opp);
    return "wait.html";
  }

  // SSE 端點（放在本 Controller；不新增其他 Controller）
  @GetMapping(value = "/wait/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter waitStream(Principal prin) throws IOException {
    User me = userMapper.selectByName(prin.getName());
    SseEmitter emitter = new SseEmitter(0L); // 不限時
    asyncKekka.watch(emitter, me.getId());
    return emitter;
  }
}
