package com.itacademy.myhospital.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;


@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LogManager.getLogger(LoggingAspect.class);


    @Around("applicationServiceBean() && applicationServicePackage()")
    public Object logAroundRepository(ProceedingJoinPoint joinPoint) throws Throwable {

        LOGGER.info("Request for {}.{}() with arguments [s]={}",joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        //Start  of execution
        Instant start = Instant.now();
        
        //        Execution target
        Object returnValue= joinPoint.proceed();
            if (returnValue!=null) {
                Instant finish = Instant.now();
                //End  of execution
                long timeOfExecution = Duration.between(start, finish).toMillis();

                LOGGER.info("Response for {}.{} Result ={}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), returnValue.toString());

                LOGGER.trace("Time taken =" + new SimpleDateFormat("mm:ss:SSS").format(new Date(timeOfExecution)));
            }
    return returnValue;
    }

    @Pointcut("within(com.itacademy.myhospital..*)")
    public void applicationExceptionPackage(){

    }

    @AfterThrowing(pointcut = "applicationExceptionPackage()",throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e){
        LOGGER.error("Exception in {}.{}  with message =  {}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                e.getMessage()!=null? e.getMessage(): "NULL"
                );
    }


    @Pointcut("within(com.itacademy.myhospital.service..*)")
    public void applicationServicePackage(){

    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void applicationServiceBean(){
    }

    @Around("applicationControllerBean() && applicationControllerPackage()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {

        LOGGER.info("Request for {}.{}() ",joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        var returnValue =joinPoint.proceed();

        LOGGER.info("Response for {}.{} ", joinPoint.getSignature().getDeclaringTypeName()
                ,joinPoint.getSignature().getName());
        return returnValue;

    }
    @Pointcut("within(com.itacademy.myhospital.controller.*)")
    public void applicationControllerPackage(){

    }

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void applicationControllerBean(){

    }
    @Around("applicationControllerBean() && applicationHospitalErrorControllerPackage()")
    public Object logAroundHospitalErrorController(ProceedingJoinPoint joinPoint) throws Throwable {

        var returnValue =joinPoint.proceed();
        LOGGER.error("Exception for {}.{}   with result = [s]={} ,{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()),
                returnValue.toString() );
        return returnValue;

    }
    @Pointcut("within(com.itacademy.myhospital.errorController.HospitalErrorController)")
    public void applicationHospitalErrorControllerPackage(){

    }



}
