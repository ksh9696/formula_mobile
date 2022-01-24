package com.example.inzent.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean{
	private final JwtTokenProvider jwtToknProvider;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//헤더에서 jwt를 받아 온다
		String token = jwtToknProvider.resolveToken((HttpServletRequest) request);

		//유효한 토큰인지 확인
		if(token != null && jwtToknProvider.validateToken(token)) {
			//토큰이 유효하면 토큰으로부터 유저 정보를 받아오기
			Authentication authentication = jwtToknProvider.getAuthentication(token);
			//securityContext에 Authentication 객체를 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);
	}
}
