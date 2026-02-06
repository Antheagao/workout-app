package com.workoutapp.middleware;

import com.workoutapp.model.User;
import com.workoutapp.service.ITokenService;
import com.workoutapp.service.IUserService;
import com.workoutapp.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final ITokenService tokenService;
    private final IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Vary", "Authorization");
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || authHeader.isEmpty()) {
            request.setAttribute("currentUser", createAnonymousUser());
            return true;
        }

        String[] headerParts = authHeader.split(" ");
        if (headerParts.length != 2 || !"Bearer".equals(headerParts[0])) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid authorization header\"}");
            return false;
        }

        String token = headerParts[1];
        Optional<Long> userIdOpt = tokenService.getUserIdByToken(token, TokenUtil.SCOPE_AUTH);

        if (userIdOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"token expired or invalid\"}");
            return false;
        }

        User user = userService.getUserById(userIdOpt.get())
                .orElse(createAnonymousUser());
        
        request.setAttribute("currentUser", user);
        return true;
    }

    private User createAnonymousUser() {
        User anonymous = new User();
        anonymous.setId(null);
        return anonymous;
    }
}
