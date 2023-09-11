package com.sidam_backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationManager implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        Integer exception = (Integer) request.getAttribute("exception");

        if(exception == null) {

        }
        else if(exception.equals(CustomCode.WRONG_TYPE_TOKEN.getCode())) {
            setResponse(response, CustomCode.WRONG_TYPE_TOKEN);
        }
        else if(exception.equals(CustomCode.EXPIRED_TOKEN.getCode())) {
            setResponse(response, CustomCode.EXPIRED_TOKEN);
        }
        else if(exception.equals(CustomCode.UNSUPPORTED_TOKEN.getCode())) {
            setResponse(response, CustomCode.UNSUPPORTED_TOKEN);
        }
        else {
            setResponse(response, CustomCode.ACCESS_DENIED);
        }
    }
    private void setResponse(HttpServletResponse response, CustomCode code) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", code.getMessage());
        responseJson.put("code", code.getCode());
        log.info("response={}",responseJson);
        response.getWriter().print(responseJson);
    }
}
