package oit.is.z4272.kaizi.janken.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z4272.kaizi.janken.model.User;
import oit.is.z4272.kaizi.janken.model.UserMapper;
import oit.is.z4272.kaizi.janken.model.Match;
import oit.is.z4272.kaizi.janken.model.MatchMapper;
import oit.is.z4272.kaizi.janken.model.MatchView;

@Controller
public class JankenController {

  private final UserMapper userMapper;
  private final MatchMapper matchMapper;

  public JankenController(UserMapper userMapper, MatchMapper matchMapper) {
    this.userMapper = userMapper;
    this.matchMapper = matchMapper;
  }

  @GetMapping("/janken")
  public String janken(Model model, Principal principal) {
    model.addAttribute("loginUser", principal.getName());
    ArrayList<User> users = userMapper.findAll();
    model.addAttribute("users", users);
    ArrayList<MatchView> results = matchMapper.selectViews();
    model.addAttribute("results", results);
    return "janken";
  }

  @GetMapping("/match")
  public String match(@RequestParam("id") int opponentId, Model model, Principal principal) {
    model.addAttribute("loginUser", principal.getName());
    User me = userMapper.findByName(principal.getName());
    User opponent = userMapper.findById(opponentId);
    if (opponent == null || !"CPU".equals(opponent.getName())) {
      model.addAttribute("errorMsg", "只能與 CPU 對戰。");
      return "match";
    }
    model.addAttribute("me", me);
    model.addAttribute("opponent", opponent);
    return "match";
  }

  @GetMapping("/fight")
  public String fight(@RequestParam("id") int opponentId,
      @RequestParam("hand") String myHand,
      Model model, Principal principal) {
    User me = userMapper.findByName(principal.getName());
    User opponent = userMapper.findById(opponentId);
    if (me == null || opponent == null) {
      model.addAttribute("errorMsg", "使用者資料不完整。");
      return "match";
    }
    String cpuHand = "Gu"; // 題目允許固定
    Match m = new Match();
    m.setUser1(me.getId());
    m.setUser2(opponent.getId());
    m.setUser1Hand(myHand);
    m.setUser2Hand(cpuHand);
    matchMapper.insert(m);
    return "redirect:/janken";
  }
}
