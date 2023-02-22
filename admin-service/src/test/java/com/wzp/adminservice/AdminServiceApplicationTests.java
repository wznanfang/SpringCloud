package com.wzp.adminservice;

import com.wzp.adminservice.dao.LoginLog;
import com.wzp.adminservice.repository.LoginLogRepository;
import com.wzp.adminservice.service.LoginLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest
class AdminServiceApplicationTests {


    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private LoginLogService loginLogService;


    @Test
    void contextLoads() {
    }


    @Test
    void findAll() {
//        List<LoginLog> list = loginLogRepository.findAllByUsernameLike("nf", PageRequest.of(0, 10));
//        System.out.println(JSONObject.toJSON(list));
        Instant start = Instant.parse("2022-02-20T00:00:00.000Z");
        Instant end = Instant.now().plusMillis(TimeUnit.SECONDS.toMillis(8));
        System.out.println(between(start, end));
    }


    public static Instant between(Instant start, Instant end) {
        long startSeconds = start.getEpochSecond();
        long endSeconds = end.getEpochSecond();
        long random = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(random);
    }


    @Test
    void test2() {
        StopWatch sw = new StopWatch("任务的耗时");
        sw.start();
        Instant start = Instant.parse("2022-02-20T00:00:00.000Z");
        Instant end = Instant.now().plusMillis(TimeUnit.SECONDS.toMillis(8));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 16, 3, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 3334; i++) {
            List<LoginLog> list = new ArrayList<>();
            for (int j = 0; j < 3000; j++) {
                LoginLog loginLog = new LoginLog();
                loginLog.setAdminId(9);
                loginLog.setUsername("nflj1234");
                loginLog.setLoginTime(Date.from(between(start, end)));
                list.add(loginLog);
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loginLogService.saveBatch(list);
                    System.out.println(Thread.currentThread().getName());
                }
            };
            executor.execute(runnable);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
    }







}
