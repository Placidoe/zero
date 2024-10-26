package com.explorex.zero.api;

import com.explorex.zero.util.ZookeeperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZookeeperController {

    @Autowired
    private ZookeeperUtils zookeeperUtils;

    @GetMapping("/create-node")
    public String createNode() {
        try {
            zookeeperUtils.createNode("/test-node/a/b/c", "Hello, Zookeeper!".getBytes());
            return "Node created successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create node: " + e.getMessage();
        }
    }

    @GetMapping("/read-node")
    public String readNode() {
        try {
            byte[] data = zookeeperUtils.readNodeData("/test-node/a/b/c");
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to read node: " + e.getMessage();
        }
    }

    @GetMapping("/delete-node")
    public String deleteNode() {
        try {
            zookeeperUtils.deleteNode("/test-node/a/b/c");
            return "Node deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete node: " + e.getMessage();
        }
    }
}