package com.example.inzent.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService{
	private final HttpServletRequest request;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HttpSession session= request.getSession();
		String sessionId = (String)session.getAttribute("sessionId");

		User user = new User();
		return user;
	}
}
