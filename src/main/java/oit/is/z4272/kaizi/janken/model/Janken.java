package oit.is.z4272.kaizi.janken.model;

public class Janken {
  public enum Hand {
    ROCK, SCISSORS, PAPER;

    public static Hand from(String s) {
      if (s == null)
        return null;
      switch (s.toLowerCase()) {
        case "rock":
          return ROCK;
        case "scissors":
          return SCISSORS;
        case "paper":
          return PAPER;
        default:
          return null;
      }
    }
  }

  // 判定：ROCK > SCISSORS, SCISSORS > PAPER, PAPER > ROCK
  public static String judge(Hand you, Hand cpu) {
    if (you == null || cpu == null)
      return "不正な手";
    if (you == cpu)
      return "あいこ";
    switch (you) {
      case ROCK:
        return (cpu == Hand.SCISSORS) ? "あなたの勝ち" : "あなたの負け";
      case SCISSORS:
        return (cpu == Hand.PAPER) ? "あなたの勝ち" : "あなたの負け";
      case PAPER:
        return (cpu == Hand.ROCK) ? "あなたの勝ち" : "あなたの負け";
      default:
        return "不正な手";
    }
  }
}
