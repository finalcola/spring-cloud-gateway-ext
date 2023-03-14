package org.finalcola.gateway.ext.nacos.def;

import com.alibaba.nacos.api.config.listener.Listener;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author: finalcola
 * @date: 2023/3/12 22:08
 */
@Slf4j
@Getter
public class NacosRouteDefListener implements Listener {

    private static final Yaml YAML = new Yaml();
    private final List<Consumer<List<RouteDefinition>>> routeChangeListeners = new ArrayList<>();
    private volatile List<RouteDefinition> routeDefinitions = Collections.emptyList();

    @Override
    public Executor getExecutor() {
        return MoreExecutors.directExecutor();
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("configInfo:{}", configInfo);
        List<RouteDefinition> definitions = parse(configInfo);
        if (definitions == null) {
            log.info("route definition config is null,ignore.config:{}", configInfo);
            return;
        }
        if (ListUtils.isEqualList(definitions, this.routeDefinitions)) {
            log.info("route definition config is same with current,ignore");
            return;
        }
        this.routeDefinitions = ListUtils.unmodifiableList(definitions);
        routeChangeListeners.forEach(consumer -> consumer.accept(definitions));
    }

    public void addListener(@NotNull Consumer<List<RouteDefinition>> consumer) {
        routeChangeListeners.add(consumer);
    }

    @Nullable
    private List<RouteDefinition> parse(String config) {
        if (StringUtils.isBlank(config)) {
            return null;
        }
        try {
            GatewayProperties properties = YAML.loadAs(config, GatewayProperties.class);
            return properties.getRoutes();
        } catch (Exception e) {
            log.info("parse route definition yaml error.json:{}", config, e);
            return null;
        }
    }
}
