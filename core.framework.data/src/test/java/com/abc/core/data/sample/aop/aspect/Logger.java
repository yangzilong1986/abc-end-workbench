package com.abc.core.data.sample.aop.aspect;

import java.util.Arrays;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Logger {
    
//    @Resource
//    private LogService logService;
	@Around("(execution(* com.abc.core.data.sample.aop.service.impl..*.*(..))) "
			+ "or execution(* com.aijava.distributed.ssh.service..*.update*(..)) "
			+ "or execution(* com.aijava.distributed.ssh.service..*.delete*(..))) and !bean(logService)")
    public Object record(ProceedingJoinPoint pjp){
        
        Log log = new Log();
        try {
            log.setOperator("admin");
            String mname = pjp.getSignature().getName();
            log.setOperName(mname);
            
            //方法参数,本例中是User user
            Object[] args = pjp.getArgs();
            log.setOperParams(Arrays.toString(args));
            
            //执行目标方法，返回的是目标方法的返回值，本例中 void
            Object obj = pjp.proceed();
            if(obj != null){
                log.setResultMsg(obj.toString());
            }else{
                log.setResultMsg(null);
            }
            
            log.setOperResult("success");
            log.setOperTime(new Date());
            
            return obj;
        } catch (Throwable e) {
            log.setOperResult("failure");
            log.setResultMsg(e.getMessage());
        } finally{
//            logService.saveLog(log);
        }
        return null;
    }
}
