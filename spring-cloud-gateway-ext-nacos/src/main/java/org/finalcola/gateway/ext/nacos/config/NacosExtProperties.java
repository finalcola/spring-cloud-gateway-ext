package org.finalcola.gateway.ext.nacos.config;

import org.finalcola.gateway.ext.nacos.constants.NacosExtConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: finalcola
 * @date: 2023/3/13 22:34
 */
@ConfigurationProperties(NacosExtProperties.PERFIX)
public class NacosExtProperties {

    public static final String PERFIX = "finalcola.nacos.config";

    private String group = NacosExtConstants.DEFAULT_GROUP;
    private String dynamicRouteDataId = NacosExtConstants.DEFAULT_DYNAMIC_ROUTE_DATA_ID;
    private String localRateLimitDataId = NacosExtConstants.DEFAULT_RATE_DATA_ID;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDynamicRouteDataId() {
        return dynamicRouteDataId;
    }

    public void setDynamicRouteDataId(String dynamicRouteDataId) {
        this.dynamicRouteDataId = dynamicRouteDataId;
    }

    public String getLocalRateLimitDataId() {
        return localRateLimitDataId;
    }

    public void setLocalRateLimitDataId(String localRateLimitDataId) {
        this.localRateLimitDataId = localRateLimitDataId;
    }
}
