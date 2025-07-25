package br.com.assembleia.assembleia.adapters.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.assembleia.assembleia.adapters.dtos.ErrorResponseDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Duration;
import io.github.bucket4j.Bucket;

public class RateLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private Bucket createNewBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(20)
            .refillGreedy(20, Duration.ofSeconds(60*5)))
            .build();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        
            logger.debug("Requisição permitida para o IP: {}", "teste");
            filterChain.doFilter(servletRequest, servletResponse);
        HttpSession session = httpRequest.getSession(true);
        String appKey = "assembleia-v1";
        Bucket bucket = (Bucket) session.getAttribute("throttler-" + appKey);
        if (bucket == null) {
            logger.debug("Criando novo bucket para o IP: {}", appKey);
            bucket = createNewBucket();
            session.setAttribute("throttler-" + appKey, bucket);
        }
        if (bucket.tryConsume(10)) {
            logger.debug("Requisição permitida para o IP: {}", appKey);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            logger.warn("Limite de requisições excedido para o IP: {}", httpRequest.getRemoteAddr());
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setHeader("Content-Type", "application/json");
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(new ErrorResponseDTO(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate limit exceeded",
                System.currentTimeMillis()
            )));
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
