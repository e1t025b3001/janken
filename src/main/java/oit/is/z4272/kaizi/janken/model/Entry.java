package oit.is.z4272.kaizi.janken.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class Entry {

  // 用 Set 避免重複，LinkedHashSet 保持加入順序
  private final Set<String> users = Collections.synchronizedSet(new LinkedHashSet<>());

  public void enter(String username) {
    if (username != null && !username.isBlank()) {
      users.add(username);
    }
  }

  public Set<String> getAll() {
    // 傳不可變複本避免外部修改
    synchronized (users) {
      return Set.copyOf(users);
    }
  }

  // 如需清除可加上這個方法（本課題不強制）
  public void clear() {
    users.clear();
  }
}
