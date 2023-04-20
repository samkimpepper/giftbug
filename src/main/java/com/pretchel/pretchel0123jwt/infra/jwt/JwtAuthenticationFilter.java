package com.pretchel.pretchel0123jwt.infra.jwt;

import com.pretchel.pretchel0123jwt.global.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, ACCESS_TOKEN);
//        Cookie cookie;
//        String token = null;
//        if(cookieOptional.isPresent()){
//            cookie = cookieOptional.get();
//            token = cookie.getValue();
//        }
//        if(StringUtils.hasText(token) && token.startsWith(BEARER_TYPE)){
//            token = token.substring(7);
//        }
        String token = resolveToken(request);
        //String refreshToken = resolveRefreshToken(request);

        if(token != null) {
            if(jwtTokenProvider.validateToken(token)) {
                String isLogout = (String) redisTemplate.opsForValue().get(token);
                if(ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);
        Cookie cookie = null;
        if(cookieOptional.isPresent()) {
            cookie = cookieOptional.get();
            String refreshToken = cookie.getValue();
            return refreshToken;
        }
        return null;
    }
}
