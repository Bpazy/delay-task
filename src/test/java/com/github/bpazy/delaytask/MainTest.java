package com.github.bpazy.delaytask;

import com.github.bpazy.delaytask.jedis.JedisManager;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public class MainTest {
    private JedisManager jedisManager;
    private DelayTaskProperty delayTaskProperty;
    private ExecutorService delayTaskExecutor;
    private DelayTaskManager delayTaskManager;

    @Before
    public void init() {
        System.out.println(1);
        jedisManager = new JedisManager(new JedisPool("127.0.0.1"));
        delayTaskProperty = DelayTaskProperty.builder().applicationName("delay-task-unit-test").build();
        delayTaskExecutor = Executors.newFixedThreadPool(5);
        delayTaskManager = new TestDelayTaskManager();
    }

    @Test
    public void mainTest() {
        startSchedule();
        submitTask();
    }

    private void startSchedule() {
        DelayTaskSchedule schedule = new DelayTaskSchedule(jedisManager, delayTaskProperty, delayTaskExecutor, getDelayTaskLoader(), delayTaskManager);
        schedule.start();

        shutdownScheduleAfter(schedule, 10 * 1000);
    }

    private void shutdownScheduleAfter(DelayTaskSchedule schedule, long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            schedule.stop();
        }).start();
    }

    private DelayTaskLoader getDelayTaskLoader() {
        return className -> {
            try {
                return (AbstractDelayTaskRunnable) Class.forName(className).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void submitTask() {
        DelayTaskSubmitter delayTaskSubmitter = new DefaultDelayTaskSubmitter(jedisManager, delayTaskExecutor, delayTaskProperty, delayTaskManager);
        delayTaskSubmitter.submit(new TestDelayTaskRunnable(UUID.randomUUID().toString(), DateTime.now().plusSeconds(5).toDate(), "我是参数"));
    }
}
