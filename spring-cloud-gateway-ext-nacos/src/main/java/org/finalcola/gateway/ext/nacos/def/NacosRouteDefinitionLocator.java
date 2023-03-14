package org.finalcola.gateway.ext.nacos.def;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.finalcola.gateway.ext.nacos.access.NacosConfigAccess;
import org.finalcola.gateway.ext.nacos.config.NacosExtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

/**
 * @author: finalcola
 * @date: 2023/3/12 12:43
 */
@Slf4j
public class NacosRouteDefinitionLocator implements RouteDefinitionLocator, ApplicationEventPublisherAware, Ordered {

    private final NacosRouteDefListener routeDefListener = new NacosRouteDefListener();
    @Autowired
    private NacosExtProperties properties;
    @Autowired
    private NacosConfigAccess configAccess;
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() throws Throwable {
        String configDataId = properties.getDynamicRouteDataId();
        String configGroup = properties.getGroup();
        Retry retry = Retry.of("nacos-route-def-listener", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofSeconds(10))
                .failAfterMaxAttempts(true)
                .build());
        Retry.decorateCheckedRunnable(retry, () -> {
            log.info("init nacos route definition locator，{}-{}", configDataId, configGroup);
            configAccess.subscribeConfig(configDataId, configGroup, routeDefListener);
            routeDefListener.addListener(this::refreshRouteCache);
        }).run();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefListener.getRouteDefinitions());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private void refreshRouteCache(List<RouteDefinition> routeDefinitions) {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
