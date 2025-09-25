package com.cottage.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import java.io.IOException;
import java.util.regex.Pattern;

@Configuration
public class SecurityEnhancementConfig implements WebMvcConfigurer {
    
    // SQL Injection patterns to detect and block
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("(?i).*(union|select|insert|update|delete|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*('|(\\-\\-)|(;)|(\\||\\|)|(\\*|\\*))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(script|javascript|vbscript)", Pattern.CASE_INSENSITIVE)
    };
    
    /**
     * SQL Injection Prevention Filter
     */
    @Bean
    public FilterRegistrationBean<Filter> sqlInjectionFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                // Check query parameters
                if (httpRequest.getQueryString() != null && containsSqlInjection(httpRequest.getQueryString())) {
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.getWriter().write("Invalid request parameters detected");
                    return;
                }
                
                // Check request parameters
                if (httpRequest.getParameterMap() != null) {
                    for (String[] values : httpRequest.getParameterMap().values()) {
                        for (String value : values) {
                            if (value != null && containsSqlInjection(value)) {
                                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                httpResponse.getWriter().write("Invalid request parameters detected");
                                return;
                            }
                        }
                    }
                }
                
                chain.doFilter(request, response);
            }
            
            private boolean containsSqlInjection(String input) {
                for (Pattern pattern : SQL_INJECTION_PATTERNS) {
                    if (pattern.matcher(input).matches()) {
                        return true;
                    }
                }
                return false;
            }
        });
        
        registrationBean.setUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("sqlInjectionFilter");
        
        return registrationBean;
    }
    
    /**
     * Security Headers Filter
     */
    @Bean
    public FilterRegistrationBean<Filter> securityHeadersFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                // Add security headers
                httpResponse.setHeader("X-Content-Type-Options", "nosniff");
                httpResponse.setHeader("X-Frame-Options", "DENY");
                httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
                httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                httpResponse.setHeader("Content-Security-Policy", 
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'");
                httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                
                chain.doFilter(request, response);
            }
        });
        
        registrationBean.setUrlPatterns("/*");
        registrationBean.setOrder(2);
        registrationBean.setName("securityHeadersFilter");
        
        return registrationBean;
    }
    
    /**
     * Rate Limiting Filter (basic implementation)
     */
    @Bean
    public FilterRegistrationBean<Filter> rateLimitFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new Filter() {
            private final java.util.Map<String, Long> requestCounts = new java.util.concurrent.ConcurrentHashMap<>();
            private final java.util.Map<String, Long> requestTimes = new java.util.concurrent.ConcurrentHashMap<>();
            private static final int MAX_REQUESTS_PER_MINUTE = 100;
            
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                String clientIP = getClientIP(httpRequest);
                long currentTime = System.currentTimeMillis();
                
                // Clean old entries
                cleanOldEntries(currentTime);
                
                // Check rate limit
                Long requestCount = requestCounts.get(clientIP);
                if (requestCount != null && requestCount > MAX_REQUESTS_PER_MINUTE) {
                    httpResponse.setStatus(429); // Too Many Requests
                    httpResponse.getWriter().write("Rate limit exceeded");
                    return;
                }
                
                // Update counters
                requestCounts.put(clientIP, requestCount == null ? 1 : requestCount + 1);
                requestTimes.put(clientIP, currentTime);
                
                chain.doFilter(request, response);
            }
            
            private String getClientIP(HttpServletRequest request) {
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader == null) {
                    return request.getRemoteAddr();
                }
                return xfHeader.split(",")[0];
            }
            
            private void cleanOldEntries(long currentTime) {
                requestTimes.entrySet().removeIf(entry -> {
                    boolean isOld = currentTime - entry.getValue() > 60000; // 1 minute
                    if (isOld) {
                        requestCounts.remove(entry.getKey());
                    }
                    return isOld;
                });
            }
        });
        
        registrationBean.setUrlPatterns("/api/*");
        registrationBean.setOrder(3);
        registrationBean.setName("rateLimitFilter");
        
        return registrationBean;
    }
}
