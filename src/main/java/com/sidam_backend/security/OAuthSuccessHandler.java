package com.sidam_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String LOCAL_REDIRECT_URL = "http://localhost:8088";
    private static final String FLUTTER_REDIRECT_URL = "http://10.0.2.2:8088";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("auth success");
        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.create(authentication);
        Map<String, Object> data = new HashMap<>();
        data.put("status", response.getStatus());
        data.put("token", token);
/*        Optional<Cookie> oCookie = Arrays.stream(request.getCookies()).filter(cookie ->
                cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();
        Optional<String> redirectUri = oCookie.map(Cookie::getValue);*/
//        response.setContentType("application/json");
        log.info("token {}", token);
//        ResponseEntity.ok().body(jsonData);
//        response.getWriter().write(result);
//        response.sendRedirect(redirectUri.orElseGet(() -> LOCAL_REDIRExCT_URL);
        response.sendRedirect(FLUTTER_REDIRECT_URL+"/sociallogin?token="+token);
    }
}
