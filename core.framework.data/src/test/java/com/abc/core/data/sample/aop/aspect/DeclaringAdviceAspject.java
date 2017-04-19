package com.abc.core.data.sample.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DeclaringAdviceAspject {
	private static final String POINT_EXPRESS="execution(* com.abc.core.data.sample.aop.service.impl..*.*(..))";
	private static final String PONIT_CUT= "execution(* com.abc.core.data.sample.aop.service.impl..*.*(..))";
	
	@Before(POINT_EXPRESS)
	public void doAccessCheck() {
		System.out.println("DeclaringAdviceAspject Before Advice doAccessCheck...");
	}

	@AfterReturning(pointcut =PONIT_CUT, returning = "retVal")
	public void doAccessCheck(Object retVal) {
		System.out.println("DeclaringAdviceAspject AfterReturning Advice doAccessCheck...");
	}

	@AfterThrowing(pointcut = PONIT_CUT, throwing = "ex")
	public void doRecoveryActions(Exception ex) {
		System.out.println("DeclaringAdviceAspject AfterThrowing Advice doAccessCheck...");
	}

	@After(PONIT_CUT)
	public void doReleaseLock() {
		System.out.println("DeclaringAdviceAspject AfterReturning After Advice doAccessCheck...");
	}

	@Around(PONIT_CUT)
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("DeclaringAdviceAspject AfterReturning Around Advice doAccessCheck...");
		// start stopwatch
		Object retVal = pjp.proceed();
		// stop stopwatch
		return retVal;
	}
}
