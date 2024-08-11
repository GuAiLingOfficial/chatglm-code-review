package com.rsl.middleware.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ description:测试
 * @ author: rsl
 * @ create: 2024-08-06 17:06
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        System.out.println(Integer.parseInt("abcd1234"));
    }
}
