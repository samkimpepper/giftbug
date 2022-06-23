package com.pretchel.pretchel0123jwt.config;

//import com.siot.IamportRestClient.IamportClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Value("${import.key}")
    private String key;

    @Value("${import.secret}")
    private String secret;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Bean
//    public IamportClient iamportClient() {
//
//        IamportClient api = new IamportClient(key, secret);
//        return api;
//    }



}
