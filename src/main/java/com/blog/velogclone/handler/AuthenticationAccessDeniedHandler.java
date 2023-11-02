package com.blog.velogclone.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    private String denied_url;

    public AuthenticationAccessDeniedHandler() {
        this.denied_url = "/common/accessDenied";
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect(denied_url);
    }
}
