package oit.is.z4272.kaizi.janken.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z4272.kaizi.janken.model.Janken;
import oit.is.z4272.kaizi.janken.model.Janken.Hand;

@Controller
public class JankenController {

  // 首頁（GET）：輸入名字
  @GetMapping("/")
  public String index() {
    return "index";
  }

  // 接收名字（POST）：寫入 Session，然後導到對戰畫面
  @PostMapping("/enter")
  public String enter(@RequestParam("username") String username, HttpSession session) {
    session.setAttribute("username", username);
    return "redirect:/janken.html";
  }

  // 對戰畫面（GET）— 支援 /janken 與 /janken.html
  @GetMapping({ "/janken", "/janken.html" })
  public String janken(Model model, HttpSession session) {
    Object name = session.getAttribute("username");
    if (name != null) {
      model.addAttribute("username", name.toString());
    }
    return "janken";
  }

  // 出拳（GET）：/play?hand=rock|scissors|paper
  // CPU 固定出 ROCK（石頭）
  @GetMapping("/play")
  public String play(@RequestParam("hand") String handStr, Model model, HttpSession session) {
    Object name = session.getAttribute("username");
    if (name != null) {
      model.addAttribute("username", name.toString()); // 只有從 index 進來才會有 Hi
    }

    Hand you = Janken.Hand.from(handStr);
    Hand[] hands = Janken.Hand.values();
    Hand cpu = hands[(int) (Math.random() * hands.length)];
    String result = Janken.judge(you, cpu);

    model.addAttribute("you", you);
    model.addAttribute("cpu", cpu);
    model.addAttribute("result", result);

    return "janken";
  }
}
