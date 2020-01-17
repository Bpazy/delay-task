package com.github.bpazy.delaytask;

import com.github.bpazy.delaytask.jedis.JedisManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author ziyuan
 * created on 2020/1/14
 */
@Slf4j
public class DefaultDelayTaskSubmitter implements DelayTaskSubmitter {
    private JedisManager jedisManager;
    private ExecutorService delayTaskExecutor;
    private DelayTaskProperty delayTaskProperty;
    private DelayTaskManager delayTaskManager;

    public DefaultDelayTaskSubmitter(JedisManager jedisManager, ExecutorService delayTaskExecutor,
                                     DelayTaskProperty delayTaskProperty, DelayTaskManager delayTaskManager) {
        this.jedisManager = jedisManager;
        this.delayTaskExecutor = delayTaskExecutor;
        this.delayTaskProperty = delayTaskProperty;
        this.delayTaskManager = delayTaskManager;
    }

    @Override
    public void submit(AbstractDelayTaskRunnable delayTaskRunnable) {
        if (!delayTaskRunnable.getRunAt().after(new Date())) {
            delayTaskExecutor.submit(() -> delayTaskRunnable.run(delayTaskRunnable.getArgument()));
        }

        long score = delayTaskRunnable.getRunAt().getTime() / 1000;
        jedisManager.getJedis().zadd(delayTaskProperty.getKey(), score, delayTaskRunnable.getTaskId());
        delayTaskManager.addDelayTask(getDelayTask(delayTaskRunnable));
    }

    private DelayTask getDelayTask(AbstractDelayTaskRunnable delayTaskRunnable) {
        DelayTask delayTask = new DelayTask();
        delayTask.setTaskId(delayTaskRunnable.getTaskId());
        delayTask.setArgument(delayTaskRunnable.getArgument());
        delayTask.setTargetClass(delayTaskRunnable.getClass().getName());
        delayTask.setTaskStatus("未执行");
        return delayTask;
    }
}
