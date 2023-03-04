package com.kh.spring.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebFilter("/*")
public class InitFilter extends HttpFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(InitFilter.class);
	
    
    public InitFilter() {
    }

	public void destroy() {
		logger.info("초기화 필터 종료");
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//application 내장객체 얻어오기
		//getServletContext로 servlet객체를 얻어온다.
		ServletContext application = ((HttpServletRequest)request).getSession().getServletContext();
		
		//모든 영역에서 spring을 사용하려고함
		//application scope - session scope - request scope - page scope
		String contextPath = ((HttpServletRequest)request).getContextPath();
		
		application.setAttribute("contextPath", contextPath);
		
		chain.doFilter(request, response);
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		logger.info("초기화 필터 생성");
	}

}
