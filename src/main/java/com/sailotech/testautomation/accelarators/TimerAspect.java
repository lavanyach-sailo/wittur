package com.sailotech.testautomation.accelarators;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

//@Aspect
public class TimerAspect {

//	@Around("execution(public * *(..))")
    public void aroundAllmethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.err.println("in around before " + joinPoint);
        long startTime = System.currentTimeMillis();
        System.err.println("Before executing method:"+ startTime);
        // Executes the actual method
        joinPoint.proceed();  
     
        long endTime = System.currentTimeMillis();
     
        long duration = (endTime - startTime);  //Total execution time in milli seconds
        
        TestBase.log.info("After executing method:"+ endTime);
        TestBase.log.info(duration);

        
//        System.err.println("in around after " + joinPoint);
    }
	private static boolean runAround = true;

    public static void main(String[] args) {
        new TimerAspect().hello();
        runAround = false;
        new TimerAspect().hello();
    }

    public void hello() {
        System.err.println("in hello");
    }

//    @After("execution(void aspects.TimerAspect.hello())")
    public void afterHello(JoinPoint joinPoint) {
        System.err.println("after " + joinPoint);
    }

//    @Around("execution(void aspects.TimerAspect.hello())")
    public void aroundHello(ProceedingJoinPoint joinPoint) throws Throwable {
        System.err.println("in around before " + joinPoint);
        if (runAround) {
            joinPoint.proceed();
        }
        System.err.println("in around after " + joinPoint);
    }

//    @Before("execution(void aspects.TimerAspect.hello())")
    public void beforeHello(JoinPoint joinPoint) {
        System.err.println("before " + joinPoint);
    }

}
