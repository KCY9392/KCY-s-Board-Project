package com.kh.spring.common.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// aop Test클래스

@Component // 런타임시 필요한 위치에 코드를 끼워넣을 수 있도록 bean으로 등록시켜줘야한다.
@Aspect	   // 공통관심사(특정흐름사이에 끼여서 수행할 코드 -> 메인코드 X)가 작성된 클래스임을 명시해줘야함
		   
		   // Aspect어노테이션이 붙은 클래스에는 실행할 코드(advice)와 pointCut이 정의되어있어야한다.
		   /*
		    * advice(끼어들어서 실제로 수행할 메소드, 코드 등)
		    * @PointCut(advice가 끼어들어서 수행될 클래스, 메소드 위치 등을 정의)이 작성되어있어야한다.
		    */
public class Test {
	private Logger logger = LoggerFactory.getLogger(Test.class);
	
	// joinpoint : 클라이언트가 호출하는 모든 비지니스 메소드 , advice가 적용될 수 있는 예비 후보
	//				ex) 클래스의 인스턴스 생성시점. 메소드가 호출되는 시점, 예외발생 등 전부
	// Pointcut	 : joinpoint들 중에서 "실제로" advice가 끼워들어서 실행될 지점을 선택
	
	/*
	 * Pointcut 작성방법
	 * 
	 * @PointCut("execution([접근제한자] 반환형 패키지+클래스명.메소드명([매개변수]))")
	 * 
	 * PointCut 표현식
	 * * 	: 모든 리턴값 허용
	 * ..	: 하위 | 아래(하위패키지) | 0개이상의 매개변수
	 * 
	 * @Before : PointCut에 지정된 메소드가 수행되기"전"에 advice수행을 명시하는 어노테이션
	 * 
	 * com.kh.spring.board패키지 아래에 있는 impl로 끝나는 클래스의 모든 메소드에 (매개변수 관계없이) 포인트컷 지정.
	 * --> 보통 Impl로 경로를 지정해놓음 
	 * "execution(* com.kh.spring.board..*Impl*.*(..))"
	 */
	
//	@Before("execution(* com.kh.spring.board..*Impl*.*(..))")
//	@Before("testPointcut()")
	public void start() { // 서비스 수행전에 실행되는 메소드(advice)
		logger.info("====================Service start====================");
	}
	
	// @After : Pointcut에 지정된 메소드가 수행된 후 advice 수행을 하라고 지시하는 어노테이션
//	@After("testPointcut()")
	public void end() {
		logger.info("==================== Service end ====================");
	}
	
	// @Pointcut을 작성해놓은 메소드
	// -> Pointcut의 패턴이 작성되는 부분에 testPointcut() 메소드 이름을 작성하면, Pointcut에 정의한 패턴이 적용된다.
	@Pointcut("execution(* com.kh.spring.board..*Service*.*(..))")
	public void testPointcut() {} //내용작성 X
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
