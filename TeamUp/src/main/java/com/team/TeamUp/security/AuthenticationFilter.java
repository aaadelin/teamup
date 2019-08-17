package com.team.TeamUp.security;

import com.team.TeamUp.validation.UserValidation;
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

    private final UserValidation userValidation;

    @Autowired
    public AuthenticationFilter(UserValidation userValidation) {
        this.userValidation = userValidation;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(authenticationNeeded(req.getRequestURI())){
            String tokenHeader = req.getHeader("token");
            if(userValidation.isValid(tokenHeader)){
                chain.doFilter(request, response);
            }else{
                res.sendError(403);
            }
        }else{
            chain.doFilter(request, response);
        }
    }

    private boolean authenticationNeeded(String URI){
        List<String> openURIs = List.of("/api/login");
        return !openURIs.contains(URI);
    }
}
