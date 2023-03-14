package org.finalcola.gateway.ext.nacos.access;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author: finalcola
 * @date: 2023/3/13 22:43
 */
@Slf4j
@AllArgsConstructor
public class NacosConfigAccess {

    private NacosConfigManager nacosConfigManager;

    public String getConfig(@NotNull String dataId, @Nullable String group) throws NacosException {
        return nacosConfigManager.getConfigService().getConfig(dataId, group, 5000L);
    }

    public String subscribeConfig(@NotNull String dataId, @Nullable String group, Listener listener) throws NacosException {
        ConfigService configService = nacosConfigManager.getConfigService();
        String content = configService.getConfigAndSignListener(dataId, group, 5000L, listener);
        // 手动触发一次
        listener.receiveConfigInfo(content);
        return content;
    }
}
