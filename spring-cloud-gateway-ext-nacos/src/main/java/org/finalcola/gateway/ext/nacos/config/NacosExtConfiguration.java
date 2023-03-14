package org.finalcola.gateway.ext.nacos.config;

import com.alibaba.cloud.nacos.NacosConfigBootstrapConfiguration;
import com.alibaba.cloud.nacos.NacosConfigManager;
import org.finalcola.gateway.ext.nacos.access.NacosConfigAccess;
import org.finalcola.gateway.ext.nacos.def.NacosRouteDefinitionLocator;
import org.finalcola.gateway.ext.nacos.filter.RateLimitFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: finalcola
 * @date: 2023/3/12 12:43
 */
@Configuration
@AutoConfigureAfter(NacosConfigBootstrapConfiguration.class)
public class NacosExtConfiguration {

    @Bean
    public NacosExtProperties nacosExtProperties() {
        return new NacosExtProperties();
    }

    @Bean
    public NacosConfigAccess nacosConfigAccess(NacosConfigManager nacosConfigManager) {
        return new NacosConfigAccess(nacosConfigManager);
    }

    @Bean
    public NacosRouteDefinitionLocator nacosRouteDefinitionLocator() {
        return new NacosRouteDefinitionLocator();
    }

    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }
}
