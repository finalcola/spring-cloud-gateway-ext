package org.finalcola.gateway.ext.nacos.filter;

import com.alibaba.nacos.api.config.listener.AbstractListener;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.finalcola.gateway.ext.nacos.access.NacosConfigAccess;
import org.finalcola.gateway.ext.nacos.config.NacosExtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * @author: finalcola
 * @date: 2023/3/13 22:25
 */
@Slf4j
public class RateLimitFilter implements GlobalFilter {


    @Autowired
    private NacosExtProperties properties;
    @Autowired
    private NacosConfigAccess configAccess;

    private volatile RateLimiter rateLimiter = null;

    @PostConstruct
    public void init() {
        String group = properties.getGroup();
        String rateLimitDataId = properties.getLocalRateLimitDataId();
        Retry retry = Retry.of("scw-ext-rateLimit", RetryConfig.custom()
                .maxAttempts(10)
                .failAfterMaxAttempts(true)
                .build());
        Retry.decorateCheckedRunnable(retry, () -> {
                    configAccess.subscribeConfig(rateLimitDataId, group, new AbstractListener() {
                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            int rateLimit = NumberUtils.toInt(configInfo, -1);
                            if (rateLimit <= -1) {
                                log.info("invalid rate limit config:{}", configInfo);
                                return;
                            }
                            RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                                    .limitForPeriod(rateLimit)
                                    .limitRefreshPeriod(Duration.ofSeconds(1))
                                    .build();
                            rateLimiter = RateLimiter.of("scw-nacos-local", rateLimiterConfig);
                        }
                    });
                })
                .unchecked()
                .run();

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return null;
    }
}
