package com.pretchel.pretchel0123jwt.v1.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;


@Controller
public class OpenbankingTokenApiController {
    @Value("${openbanking.client-id}")
    private String clientId;

    @Value("${openbanking.redirect-uri}")
    private String redirectUri;

    @Value("${openbanking.response-type}")
    private String responseType;

    @Value("${openbanking.scope}")
    private String scope;

    @Value("${openbanking.state}")
    private String state;

    @Value("${openbanking.auth-type}")
    private String authType;

    @Value("${openbanking.code-request-url}")
    private String codeRequestUrl;

    @GetMapping("/api/openbanking")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        String targetUrl = UriComponentsBuilder.fromHttpUrl(codeRequestUrl)
                .queryParam("response_type", responseType)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scope)
                .queryParam("state", state)
                .queryParam("auth_type", authType)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}
