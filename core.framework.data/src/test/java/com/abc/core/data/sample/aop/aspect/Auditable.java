package com.abc.core.data.sample.aop.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
	
	String value() default "DAO";
	
	AuditCode aduit() default AuditCode.FIELD;
	
	
}