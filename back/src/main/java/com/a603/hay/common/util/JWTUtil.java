package com.a603.hay.common.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Setter
@PropertySources({
    @PropertySource("classpath:application.properties"),
})
public class JWTUtil {
//  @Value("${spring.jwt.secretKey}")
  private String secretKey = "test";

  private static final String HEADER_AUTH = "Authorization";

  //FIXME 60분으로 변경 필요
  private long tokenValidTime = 1000L * 60 * 60 * 24 * 7; // 60분
  private long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7; // 7일

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String userId) {
    Claims claims = Jwts.claims().setSubject(userId);
    Date now = new Date();

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + tokenValidTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String createRefreshToken() {
    Date now = new Date();

    return Jwts.builder()
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public boolean validateTokenExpiration(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getUserIdByHeaderAccessToken(HttpServletRequest req) {
    String token = req.getHeader(HEADER_AUTH);
    token = token.substring(7,token.length()); // 토큰 앞에 `Bearer ` 붙어서 넘어오니까 제거해주고 토큰 확인
    return this.getUserId(token);
  }

  public String getUserId(String accessToken) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getSubject();
    } catch(Exception e) {
      //TODO
      throw new RuntimeException();
    }
  }

}
