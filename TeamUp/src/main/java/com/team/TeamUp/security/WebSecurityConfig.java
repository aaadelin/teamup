package com.team.TeamUp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${application.cors.origins}")
    private String allowedOrigins;

    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint entryPoint = (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());

        http
                // Disable csrf fo apis
                .cors().and()
                .csrf().disable()
                // Skip event creation as rest apis are stateless
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(entryPoint)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "*"
                ).permitAll();
        http.headers().cacheControl();
    }

    @Bean(name = "corsFilter")
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        for (String allowedOrigin : allowedOrigins.split(",")) {
            config.addAllowedOrigin(allowedOrigin);
        }
        config.addAllowedHeader("*");
        config.addExposedHeader("token");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
