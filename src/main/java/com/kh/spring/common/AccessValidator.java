package com.kh.spring.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

public class AccessValidator extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		
		//요청 url 정보 spring/board/list.bo -> board/list.bo
		String requestUrl = req.getRequestURI().substring(req.getContextPath().length());
		
		//권한체크
		String role = getGrade(req.getSession());
		
		//로그인안한 사용자
		if(role == null || !role.equals("A")) {
			try {
				req.setAttribute("errorMsg", "로그인 후 이용가능합니다.");
				req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req, res);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		//로그인했지만, 권한이 없는 사용자 (관리자가 아니면서, admin인페이지를 요청했을 때
		if(requestUrl.indexOf("admin") > -1 && !role.equals("A")) {
			try {
				req.setAttribute("errorMsg", "권한이 없습니다.");
				req.getRequestDispatcher("/WEB-INF/views/common/errorPage.jsp").forward(req, res);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		//모든 권한 가진 사용자일경우(즉, 관리자)
		return true;
	}
	
	public String getGrade(HttpSession session) {
		
		Member member = (Member)session.getAttribute("loginUser");
		
		if(member == null) {
			return null;
		}
		return member.getRole();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
