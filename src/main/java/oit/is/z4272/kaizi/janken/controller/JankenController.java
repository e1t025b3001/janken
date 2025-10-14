package oit.is.z4272.kaizi.janken.controller;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z4272.kaizi.janken.model.Entry; // ← 這行也依你的 package 調整

@Controller
public class JankenController {

  private final Entry entry;

  public JankenController(Entry entry) {
    this.entry = entry;
  }

  // 首頁導到 static 的 index.html
  @GetMapping("/")
  public String root() {
    return "redirect:/index.html";
  }

  // 顯示 janken 畫面（需要登入；由 Security 規則保護）
  @GetMapping("/janken")
  public String janken(Principal principal, ModelMap model) {
    String username = (principal != null) ? principal.getName() : null;

    if (username != null) {
      entry.enter(username); // 記錄進入者（DI 共享）
      model.addAttribute("joined", true);
      model.addAttribute("username", username);
    }

    model.addAttribute("entries", entry.getAll());
    return "janken"; // templates/janken.html
  }

  // 出拳（POST）：CPU 改為「隨機」出手
  @PostMapping("/janken/play")
  public String play(@RequestParam("hand") String hand,
      Principal principal,
      ModelMap model) {
    String username = (principal != null) ? principal.getName() : null;

    if (username != null) {
      entry.enter(username);
      model.addAttribute("joined", true);
      model.addAttribute("username", username);
    }
    model.addAttribute("entries", entry.getAll());

    // ---- 隨機 CPU 手勢 ----
    List<String> hands = List.of("グー", "チョキ", "パー");
    String cpu = hands.get(ThreadLocalRandom.current().nextInt(hands.size()));

    // 判定
    String result;
    if (hand == null || (!hand.equals("グー") && !hand.equals("チョキ") && !hand.equals("パー"))) {
      result = "不正な手";
    } else if (hand.equals(cpu)) {
      result = "引き分け";
    } else if ((hand.equals("グー") && cpu.equals("チョキ")) ||
        (hand.equals("チョキ") && cpu.equals("パー")) ||
        (hand.equals("パー") && cpu.equals("グー"))) {
      result = "あなたの勝ち";
    } else {
      result = "あなたの負け";
    }

    model.addAttribute("yourHand", hand);
    model.addAttribute("opponent", cpu);
    model.addAttribute("result", result);

    return "janken";
  }
}
