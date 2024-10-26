package com.explorex.zero.config;

import com.explorex.zero.util.ZookeeperUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.connect-string}")
    private String connectString;

    @Value("${zookeeper.session-timeout}")
    private int sessionTimeout;

    @Value("${zookeeper.connection-timeout}")
    private int connectionTimeout;

    @Value("${zookeeper.namespace}")
    private String namespace;

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                connectString,
                sessionTimeout,
                connectionTimeout,
                new ExponentialBackoffRetry(1000, 3)
        );
    }

    @Bean
    public ZookeeperUtils zookeeperUtils(CuratorFramework curatorFramework) {
        return new ZookeeperUtils(curatorFramework, namespace);
    }
}