package com.binarybeast.cipherstore.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final char SPACE = ' ';
    private static final char NEWLINE = '\n';
    private static final char HEADER_DELIMITER = ':';

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final long startTime = System.currentTimeMillis();
        StringBuilder logBuilder = new StringBuilder();
        buildRequestLog(logBuilder, request, body);
        try {
            ClientHttpResponse clientHttpResponse = execution.execute(request, body);
            buildResponseLog(logBuilder, clientHttpResponse);
            return clientHttpResponse;
        } catch (Throwable e) {
            logBuilder.append(" Exception: ")
                      .append(e.getMessage()).append(NEWLINE)
                      .append(ExceptionUtils.getStackTrace(e)).append(NEWLINE);
            throw e;
        } finally {
            if (log.isDebugEnabled()) {
                long processTime = System.currentTimeMillis() - startTime;
                log.debug("ProcessTime: {} ms\n{}", processTime, logBuilder.toString());
            }
        }
    }

    private void buildRequestLog(StringBuilder logBuilder, HttpRequest request, byte[] body) {
        try {
            if (log.isDebugEnabled()) {
                // URI
                logBuilder.append(SPACE).append(request.getMethod())
                          .append(SPACE).append(request.getURI()).append(NEWLINE);

                // headers
                HttpHeaders httpHeaders = request.getHeaders();

                for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                    logBuilder.append(" >> ")
                              .append(entry.getKey()).append(HEADER_DELIMITER).append(entry.getValue())
                              .append(NEWLINE);
                }

                // body
                logBuilder.append(" >> ")
                          .append(new String(body, StandardCharsets.UTF_8))
                          .append(NEWLINE);
            }
        } catch (Exception e) {
            log.warn("Cannot write request logs.", e);
        }
    }

    private void buildResponseLog(StringBuilder logBuilder, ClientHttpResponse clientHttpResponse) {
        // response logging
        try {
            if (log.isDebugEnabled()) {
                // headers
                HttpHeaders httpHeaders = clientHttpResponse.getHeaders();

                for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                    logBuilder.append(" << ")
                              .append(entry.getKey()).append(HEADER_DELIMITER).append(entry.getValue())
                              .append(NEWLINE);
                }

                // body
                InputStream inputStream = clientHttpResponse.getBody();
                logBuilder.append(" << ")
                          .append(IOUtils.toString(inputStream, StandardCharsets.UTF_8))
                          .append(NEWLINE);
            }
        } catch (Exception e) {
            log.warn("Cannot write response logs.", e);
        }
    }
}