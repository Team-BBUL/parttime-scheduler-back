package com.sidam_backend.security;

import com.sidam_backend.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;


@Slf4j
@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private EmployeeService employeeService;

    @Pointcut("within(com.sidam_backend.controller..*)")
    public void allControllerMethods() {}

    @Before("allControllerMethods()")
    public void checkAuthorization(JoinPoint joinPoint) throws Exception {
        log.info("AuthorizationAspect.checkAuthorization running..");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long id = tokenProvider.validateAndGetSubject(token);

            Object[] paramValues = joinPoint.getArgs();
            String paramName;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            for (int i = 0; i< method.getParameters().length; i++){
                paramName = method.getParameters()[i].getName();
                if(paramName.equals("roleId")){
                    Long roleId = (Long) paramValues[i];
                    log.info("roleId = {}", roleId);
                    if (!employeeService.checkIfUserHasRole(id, roleId)) {
                        log.info("No Authorize");
                        throw new Exception("No Authorizes.");
                    }
                }
            }
        }
    }
}
