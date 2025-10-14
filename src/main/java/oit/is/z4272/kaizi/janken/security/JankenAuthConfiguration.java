package oit.is.z4272.kaizi.janken.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class JankenAuthConfiguration {

  // 依課題：user1 / user2，密碼都是 isdev（要用 bcrypt）
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    // ROLE 名稱在 Spring Security 內部會變成 "ROLE_USER"
    UserDetails user1 = User.withUsername("user1")
        .password(encoder.encode("isdev"))
        .roles("USER")
        .build();

    UserDetails user2 = User.withUsername("user2")
        .password(encoder.encode("isdev"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user1, user2);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 認可規則：/janken 開頭要登入，首頁與靜態資源可匿名
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/janken/**").authenticated()
            .anyRequest().permitAll())
        // 使用預設的 Login Form
        .formLogin(Customizer.withDefaults())
        // 登出成功回到首頁
        .logout(logout -> logout.logoutSuccessUrl("/"))
    // CSRF 保持預設啟用即可
    ;

    return http.build();
  }
}
