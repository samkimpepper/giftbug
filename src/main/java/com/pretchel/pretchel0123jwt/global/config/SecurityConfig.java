package com.pretchel.pretchel0123jwt.global.config;

import com.pretchel.pretchel0123jwt.infra.jwt.JwtAuthenticationFilter;
import com.pretchel.pretchel0123jwt.infra.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.modules.account.exception.RestAuthenticationEntryPoint;
import com.pretchel.pretchel0123jwt.modules.account.exception.TokenAccessDeniedHandler;
import com.pretchel.pretchel0123jwt.modules.oauth2.service.CustomOAuth2UserService;
import com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository;
import com.pretchel.pretchel0123jwt.modules.oauth2.exception.OAuth2AuthenticationFailureHandler;
import com.pretchel.pretchel0123jwt.modules.oauth2.exception.OAuth2AuthenticationSuccessHandler;
import com.pretchel.pretchel0123jwt.modules.account.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler failureHandler;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(tokenAccessDeniedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/user/**",
                            "/api/user/signup",
                            "/api/user/login",
                            "/api/user/find-password",
                            "/api/user/confirm-password",
                            "/api/user/reissue",
                            "/api/event/page/**",
                            "/payment",
                            "/openbanking/**",
                            "/test/**",
                            "/test/payments/**",
                            "/api/payments/**",
                            "/oauth2/redirect",
                            "/oauth2/**",
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/swagger/**",
                            "/v2/api-docs/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/configuration/**").permitAll()
                    //.anyRequest().hasAuthority(Authority.ROLE_USER.name())
                    .anyRequest().authenticated()
                    .and()
                .oauth2Login()
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                        .and()
                    .redirectionEndpoint()
                    .baseUri("/oauth2/callback/**")
                        .and()
                .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                    .successHandler(successHandler)
                    .failureHandler(failureHandler);
        http.addFilterAfter(new CookieAttributeFilter(), BasicAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
