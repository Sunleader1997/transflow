package org.sunyaxing.transflow.transflowapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicLong;

class TransflowAppApplicationTests {

    @Test
    void contextLoads() {
        AtomicLong atomicLong = new AtomicLong(Long.MAX_VALUE);
        System.out.printf(""+atomicLong.updateAndGet(x -> x + 1));
    }

}
