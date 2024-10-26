package com.explorex.zero.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

//@Component
public class ZookeeperUtils {

    private final CuratorFramework curatorFramework;
    private final String namespace;

    public ZookeeperUtils(CuratorFramework curatorFramework, String namespace) {
        this.curatorFramework = curatorFramework;
        this.namespace = namespace;
    }

    /**
     * 创建节点
     *
     * @param path 节点路径
     * @param data 节点数据
     */
    public void createNode(String path, byte[] data) throws Exception {
        String fullPath = ZKPaths.makePath(namespace, path);
        if (curatorFramework.checkExists().forPath(fullPath) == null) {
            curatorFramework.create().creatingParentsIfNeeded().forPath(fullPath, data);
        } else {
            curatorFramework.setData().forPath(fullPath, data);
        }
    }

    /**
     * 读取节点数据
     *
     * @param path 节点路径
     * @return 节点数据
     */
    public byte[] readNodeData(String path) throws Exception {
        String fullPath = ZKPaths.makePath(namespace, path);
        Stat stat = new Stat();
        return curatorFramework.getData().storingStatIn(stat).forPath(fullPath);
    }

    /**
     * 删除节点
     *
     * @param path 节点路径
     */
    public void deleteNode(String path) throws Exception {
        String fullPath = ZKPaths.makePath(namespace, path);
        if (curatorFramework.checkExists().forPath(fullPath) != null) {
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(fullPath);
        }
    }

    /**
     * 判断节点是否存在
     *
     * @param path 节点路径
     * @return 是否存在
     */
    public boolean exists(String path) throws Exception {
        String fullPath = ZKPaths.makePath(namespace, path);
        return curatorFramework.checkExists().forPath(fullPath) != null;
    }
}