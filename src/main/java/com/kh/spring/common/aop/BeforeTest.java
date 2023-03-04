package com.kh.spring.common.aop;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kh.spring.member.model.vo.Member;

@Component
@Aspect
public class BeforeTest {
	
	private Logger logger = LoggerFactory.getLogger(BeforeTest.class);
	
	private String logMP[] = {"iphone", "ipod", "android", "blackberry", "opera mobi"};

	
	// joinpoint : advice가 적용될 수 있는 모든 후보지(AOP를 적용할 수 있는 모든 지점)
	
	// JoinPoint라는 인터페이스가 있음 
	// -> advice가 실제로 적용되는 Target Object의 
	//	  정보나 전달되는 매개변수, 메소드, 반환값, 예외 등의 정보를 얻을 수 있는 메소드 제공
	
	// (주의사항) JoinPoint 인터페이스는 항상 첫번째 매개변수로 작성되어야한다.

	@Before("CommonPointcut.implPointcut()")
	public void logService(JoinPoint jp) { //실행될 시점 처음에 끼워줘야하는 데 그것을 도와주는 것이 JoinPoint이다.
		
		StringBuilder sb = new StringBuilder();
		sb.append("==========================================================\n");
		
		// Start  : 현재 실행된 클래스명 - 실행된 메소드(parameter들)
		// JoinPoint 안의 메소드 중 getTarget() : aop가 적용된 개체를 반환해준다.(ServiceImpl)
		sb.append("start : "+jp.getTarget().getClass().getSimpleName()+" - "); //패키지명을 제외한 클래스 명 , targer은 주소값
		
		// JoinPoint 안의 메소드 중 getSignature() : 현재 수행되었거나 수행되는 메소드의 정보 반환해준다.
		sb.append(jp.getSignature().getName());
		
		// JoinPoint 안의 메소드 중 getArgs() : 메소드 호출 시, 전달된 매개변수 반환해준다.
		sb.append("("+ Arrays.toString(jp.getArgs()) +")\n");
				
		
		
		// ip	  : web/mobile http://xxx.xxxxx....
		//HttpServletRequest객체 얻어오기
		//스케줄러에서 예외발생 -> 예외처리 반드시 해줘야함!(요청객체가 존재하지않기때문에)
		try {
			HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		
			String currentDevice = "web";
			String logUA = req.getHeader("user-agent").toLowerCase();
			for( String device :logMP) {
				if(logUA.indexOf(device) > 0) {//일치하는 문자잇을경우
					currentDevice = "mobile";
					break;
				}
			}
			// http://domain:port+uri_
			//http + req.getServerPort() + uri
			sb.append("ip : " + currentDevice+getIp(req) + " "
					  + (req.isSecure() ? "https" : "http")+ "://" 
					  + req.getServerName() + req.getServerPort()
					  + req.getRequestURI());
		
			// userId : 유저아이디
			Member loginUser = (Member)req.getSession().getAttribute("loginUser");
			if(loginUser != null) {
				sb.append("\nuserId : "+loginUser.getUserId());
			}
			
		}catch(Exception e) {
			sb.append("스케줄러 예외발생");
		}
		
		logger.info(sb.toString());
		
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
