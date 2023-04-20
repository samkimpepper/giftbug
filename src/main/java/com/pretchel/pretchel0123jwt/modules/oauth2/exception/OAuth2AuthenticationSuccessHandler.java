package com.pretchel.pretchel0123jwt.modules.oauth2.exception;

import com.pretchel.pretchel0123jwt.infra.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository;
import com.pretchel.pretchel0123jwt.global.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.*;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private JwtTokenProvider tokenProvider;
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private String authorizedRedirectUri;
    private RedisTemplate redisTemplate;

    @Autowired
    OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if(response.isCommitted()) {
            logger.debug("이미 커밋됐음. 이거 못씀 + " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            return null;
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        UserResponseDto.TokenInfo tokeninfo = tokenProvider.generateToken(authentication);

        // refresh token은 쿠키에 저장
        int cookieMaxAge =  tokeninfo.getRefreshTokenExpirationTime().intValue() / 60;
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, tokeninfo.getRefreshToken(), cookieMaxAge);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokeninfo.getAccessToken())
                .build()
                .toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedURI = URI.create("http://localhost:3000/oauth2/redirect");
        if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort()) {
            return true;
        }
        return false;
    }
}
