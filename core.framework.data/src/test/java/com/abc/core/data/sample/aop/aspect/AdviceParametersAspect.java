package com.abc.core.data.sample.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.abc.core.data.sample.domain.Account;

@Component
@Aspect
public class AdviceParametersAspect {
	private static final String POINT_EXPRESS = "execution(* com.abc.core.data.sample.aop.service.impl..*.*(..))"
			+ " && args(account,..)";

	private static final String ANNOTION_EXPRESS = "execution(* "
			+ "com.abc.core.data.sample.aop.service.impl..*.*(..))" + " && @annotation(auditable)";

	private static final String ADVICE_EXPRESS = "execution(* com.abc.core.data..SampleImpl+."
			+ "sampleGenericMethod(*)) && args(param)";

	private static final String POINT_PACKAGE = "execution(* com.abc.core.data..*.*(..))";

	/**
	 * If the first parameter is of the JoinPoint, ProceedingJoinPoint, or
	 * JoinPoint.StaticPart type, you may leave out the name of the parameter
	 * from the value of the "argNames" attribute. For example, if you modify
	 * the preceding advice to receive the join point object, the "argNames"
	 * attribute need not include it:
	 */
	@Before(value = POINT_PACKAGE + " && target(bean) && @annotation(auditable)", argNames = "bean,auditable")
	public void audit(JoinPoint jp, Object bean, Auditable auditable) {
		AuditCode code = auditable.aduit();
		System.out.println("AdviceParametersAspect @Before Advice audit...");
		System.out.println(code);
	}

//	@Before(value = POINT_PACKAGE + " && target(bean) && @annotation(auditable)", argNames = "bean,auditable")
//	public void audit(Object bean, Auditable auditable) {
//		AuditCode code = auditable.aduit();
//		System.out.println("AdviceParametersAspect @Before Advice audit...");
//		System.out.println(code);
//	}

	// @Before(ADVICE_EXPRESS)
	// public void beforeSampleMethod(Account param) {
	// System.out.println("AdviceParametersAspect @Before Advice
	// beforeSampleMethod...");
	// }

	@After(POINT_EXPRESS)
	public void validateAccount(Account account) {
		System.out.println("AdviceParametersAspect @Before Advice doAccessCheck...");
	}

	@AfterReturning(ANNOTION_EXPRESS)
	public void audit(Auditable auditable) {
		AuditCode code = auditable.aduit();
		System.out.println("AdviceParametersAspect @Before Advice audit...");
	}

}
