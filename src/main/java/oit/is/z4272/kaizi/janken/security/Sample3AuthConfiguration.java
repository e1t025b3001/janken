package oit.is.z4272.kaizi.janken.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Sample3AuthConfiguration {
  /**
   * 認可処理に関する設定（認証されたユーザがどこにアクセスできるか）
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(login -> login
        .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")) // ログアウト後に / にリダイレクト
        // 片段：filterChain() 補上 h2-console 相關
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/h2-console/**", "/error", "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/janken/**").authenticated()
            .anyRequest().permitAll())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/sample2*/**"))
        .headers(h -> h.frameOptions(f -> f.sameOrigin()));

    return http.build();
  }

  /**
   * 認証処理に関する設定（誰がどのようなロールでログインできるか）
   *
   * @return
   */
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    // ユーザ名，パスワード，ロールを指定してbuildする
    // このときパスワードはBCryptでハッシュ化されているため，{bcrypt}とつける
    // ハッシュ化せずに平文でパスワードを指定する場合は{noop}をつける
    // user1/p@ss,user2/p@ss,admin/p@ss

    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$10$ngxCDmuVK1TaGchiYQfJ1OAKkd64IH6skGsNw1sLabrTICOHPxC0e").roles("USER").build();
    UserDetails user2 = User.withUsername("user2")
        .password("{bcrypt}$2y$10$ngxCDmuVK1TaGchiYQfJ1OAKkd64IH6skGsNw1sLabrTICOHPxC0e").roles("USER").build();
    UserDetails honda = User.withUsername("ほんだ")
        .password("{bcrypt}$2y$05$X4nQfUmH1fLqFCs.TVfXpO9I0C.5d31V7ozTtfCl5F4nfMVhVm8tO")
        .roles("USER").build();
    UserDetails igaki = User.withUsername("いがき")
        .password("{bcrypt}$2y$05$X4nQfUmH1fLqFCs.TVfXpO9I0C.5d31V7ozTtfCl5F4nfMVhVm8tO")
        .roles("USER").build();

    // 生成したユーザをImMemoryUserDetailsManagerに渡す（いくつでも良い）
    return new InMemoryUserDetailsManager(user1, user2, honda,igaki);
  }

}
