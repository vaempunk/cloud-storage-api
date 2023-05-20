package dev.vaem.cloudstorage.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.vaem.cloudstorage.domain.user.UserAccount;
import dev.vaem.cloudstorage.util.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BearerAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserAccount userAccount = jwtUtility.parseAccessToken(token);
        if (userAccount == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var userToken = new UsernamePasswordAuthenticationToken(userAccount, null, userAccount.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(userToken);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        return authHeader.substring(7);
    }

}
