package com.explorex.zero;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ZeroApplication.class)
class MyBatisPlusTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelect() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");
        List<User> users = userMapper.selectList(wrapper);
        assertEquals(1, users.size());
        assertEquals("Tom", users.get(0).getName());
    }

    @Test
    void testInsert() {
        User user = new User();
        user.setName("Jerry");
        user.setAge(20);
        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertTrue(user.getId() > 0);
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setId(1L);
        user.setName("Spike");
        user.setAge(30);
        int result = userMapper.updateById(user);
        assertEquals(1, result);
        User updatedUser = userMapper.selectById(1L);
        assertEquals("Spike", updatedUser.getName());
        assertEquals(30, updatedUser.getAge());
    }

    @Test
    void testDelete() {
        int result = userMapper.deleteById(1L);
        assertEquals(1, result);
        User deletedUser = userMapper.selectById(1L);
        assertNull(deletedUser);
    }
}
