package com.example.inzent.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//토큰 생성하고  검증하는 컴포넌트
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
	private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);



	@Value("${spring.jwt.secret")
	private String secretKey;

	//토큰 유효시간 30분
	private long tokenVaildTime = 30 * 60 * 1000L;

	private final CustomUserDetailService customUserDetailService;
	
	//객체 초기화, secretKey를 Base64로 인코딩
	//@PostConstruct
	//private void init() {
	//	secretKey =  Base64.getEncoder().encodeToString(secretKey.getBytes());
	//}

	//jwt토큰 생성
	public String createToken() {
		Map<String, Object> headers = new HashMap<>(); headers.put("typ", "JWT");
		//Claims claims = Jwts.claims().setSubject(username); //jwt payload에 저장되는 정보 단위
		//claims.put("roles", roles); //정보는 key/value쌍으로 저장
		//claims.put("username",username);
		Date now = new Date();
		return Jwts.builder()
				//.setClaims(claims) //정보 저장
				.setHeader(headers)
				.setIssuedAt(now) //토큰 발행 시간 정보
				.setExpiration(new Date(now.getTime() + tokenVaildTime)) //토큰 만료 시간
				.signWith(SignatureAlgorithm.HS256, secretKey) //사용할 암호화 알고리즘곽 signature에 들어갈 secret값 세팅
				.compact();
	}
	
	//토큰에서 인증 정보 조회
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	// 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    
    //Request의 Header에서 token값을 가져옴 "X-AUTH-TOKEN" : "TOKEN값"
    public String resolveToken(HttpServletRequest request) {
    	return request.getHeader("Authorization");
    }
    
    //토큰의 유효성 + 만료 일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			logger.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			logger.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			logger.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
        }

}
