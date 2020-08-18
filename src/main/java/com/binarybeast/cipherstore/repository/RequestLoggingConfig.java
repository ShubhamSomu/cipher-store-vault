package com.binarybeast.cipherstore.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.binarybeast.cipherstore.interceptor.RequestLoggingInterceptor;

//@Configuration
public class RequestLoggingConfig {

    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }
}