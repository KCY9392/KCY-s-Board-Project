package com.kh.spring.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

//권장하진않지만 HandlerInterceptorAdapter를 상속받아야한다.
public class LoggingInterceptor extends HandlerInterceptorAdapter{

	static Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
	
	//사용자가 사용하고있는 핸드폰 종류가 뭔지 String배열형태로 선언하기
	static String logMP[] = {"iphone", "ipod", "android", "blackberry", "opera mobi"};
	
	
	/*
	 * 모든 경로로 들어오는 요청에 대한 로그정보를 남기기위한 메소드
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		//접속된 장비가 무엇인지(웹인지, 모바일인지)
		
		String currentDevice = "web";
		String logUA = request.getHeader("user-agent").toLowerCase();
		for( String device :logMP) {
			if(logUA.indexOf(device) > 0) {//일치하는 문자잇을경우
				currentDevice = "mobile";
				break;
			}
		}
		
		//사용자의 접속 url, 서버 정보 추가
		//uri -> list.bo 같은 우리들이 만든 url
		//url -> 실제 존재하는 경로 .jsp
		//String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
		
		HttpSession session = request.getSession();
		String currentDomain = request.getServerName();
		int currentPort = request.getServerPort();
		String queryString = "";
		
		if(request.getMethod().equals("GET")) {
			queryString = request.getQueryString();
		}
		
		else { //POST방식이라면,
			
			Map map = request.getParameterMap();
			Object[] keys = map.keySet().toArray();
			
			for(int i = 0; i< keys.length; i++) {
				if(i > 0) {//한번이상 반복을 했다면,
					queryString += "&";
				}
				String[] values = (String[])map.get(keys[i]);
				queryString += keys[i] +"=";
				
				int count = 0;
				for( String str : values ) {
					if(count>0) {
						queryString += ",";
					}
					queryString += str;
					count++; //value값이 여러개잇을수도 있기 때문에,
				}
			}
		}
		
		//파라미터가 아예 없다면 -> 아예 로그에 포함시키지 않을 예정
		if(queryString == null || queryString.trim().length() == 0) {
			queryString = null;
		}
		
		// 아이디 정보 추가 
		String userId = "";
		Member user = (Member)session.getAttribute("loginUser");
		if(user != null) { //유저잇다면,
			userId = user.getUserId();
		}
		
		// ip정보 추가
		String uri = request.getRequestURI();
		String ip = getIp(request);
		
		//기기에 따라 제약이 있음
//		String ip = request.getHeader("X-forwarded-For");
		
		
		//프로토콜 정보 추가 (HTTP냐 HTTPS냐)
		String protocol = (request.isSecure()) ? "https" : "http"; //보안되어있냐(https) 아니냐(http)
		
		logger.info(ip+" : "+currentDevice+" : "+userId+" : "+protocol+"://"+currentDomain+currentPort+uri+
				(queryString != null ? "?"+queryString : ""));
		
		return true;
	}
	
	
	
	
	public String getIp(HttpServletRequest request){
		 String ip = request.getHeader("X-Forwarded-For");
	
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
