package com.team.teamup.security;

import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.parameters.P;
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
@Slf4j
public class AuthenticationFilter implements Filter {

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
//            if((userValidation.isUserLoggedIn(tokenHeader) || req.getRequestURI().equals("/api/login") || req.getRequestURI().contains(""))){ // - for swagger
            if ((userValidation.isUserLoggedIn(tokenHeader) || req.getRequestURI().equals("/api/login") || req.getRequestURI().equals("/api/requests"))) {
                log.debug(String.format("User with token %s is eligible to access %s", tokenHeader, req.getRequestURI()));
                chain.doFilter(request, response);
            } else {
                log.debug("User is or just has been logged out");
                res.sendError(405);
            }
        } else {
            log.debug("User not eligible");
            res.sendError(403);
        }
    }

    private boolean isAuthorized(String uri, String method, String token) {
//        List<String> openURIs = List.of("/api/login", ".*"); //- for swagger
        List<String> openURIs = List.of("/api/login");
        List<String> adminAuthenticationPOST = List.of(
                "/api/users",
//                "/api/project",
                "/api/team",
                "/api/requests"
        );
        List<String> adminAuthenticationPUT = List.of(
                "/api/user"
        );
        List<String> adminAuthenticationDELETE = List.of(
                "\\/api\\/user\\/\\d+"
        );
        if (checkMatching(openURIs, uri)) {
            return true;
        }
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
                } else if (uri.equals("/api/requests")) {
                    return true;
                } else {
                    return userValidation.isValid(token);
                }
            case "DELETE":
                if (adminAuthenticationDELETE.stream().anyMatch(uri::matches)) {
                    return userValidation.isValid(token, UserStatus.ADMIN);
                } else {
                    return userValidation.isValid(token);
                }
            default:
                return false;
        }
    }


    private boolean checkMatching(List<String> regexUri, String uriToMatch) {
        return regexUri.stream().anyMatch(uriToMatch::matches);
    }
}
