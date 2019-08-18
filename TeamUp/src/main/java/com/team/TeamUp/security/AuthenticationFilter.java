package com.team.TeamUp.security;

import com.team.TeamUp.controller.RestGetController;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Order
@CrossOrigin
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);
    private final UserValidation userValidation;

    @Autowired
    public AuthenticationFilter(UserValidation userValidation) {
        this.userValidation = userValidation;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String tokenHeader = req.getHeader("token");

        if (isAuthorized(req.getRequestURI(), req.getMethod(), tokenHeader)) {
            LOGGER.info(String.format("User with token %s is eligible to access %s", tokenHeader, req.getRequestURI()));
            chain.doFilter(request, response);
        } else {
            LOGGER.info("User not eligible");
            res.sendError(403);
        }
    }

    private boolean isAuthorized(String uri, String method, String token) {
        List<String> openURIs = List.of("/api/login");
        List<String> adminAuthenticationPOST = List.of(
                "/api/users",
                "/api/project",
                "/api/team"
        );
        List<String> adminAuthenticationPUT = List.of(
                "/api/user"
        );
        List<String> adminAuthenticationDELETE = List.of(
                "/api/user/**"
        );
        if (checkMatching(openURIs, uri)) {
            return true;
        } else {
            switch (method) {
                case "GET":
                    // any url requires only basic authentication
                    return userValidation.isValid(token);
                case "POST":
                    if (adminAuthenticationPOST.stream().anyMatch(protectedUri -> protectedUri.equals(uri))) {
                        return userValidation.isValid(token, UserStatus.ADMIN);
                    } else {
                        return userValidation.isValid(token);
                    }
                case "PUT":
                    if (adminAuthenticationPUT.stream().anyMatch(protectedUri -> protectedUri.equals(uri))) {
                        return userValidation.isValid(token, UserStatus.ADMIN);
                    } else {
                        return userValidation.isValid(token);
                    }
                case "DELETE":
                    if (adminAuthenticationDELETE.stream().anyMatch(protectedUri -> protectedUri.equals(uri))) {
                        return userValidation.isValid(token, UserStatus.ADMIN);
                    } else {
                        return userValidation.isValid(token);
                    }
            }
        }

        return false;
    }

    private boolean checkMatching(List<String> regexUri, String uriToMatch) {
        return regexUri.stream().anyMatch(uriToMatch::matches);
    }
}
