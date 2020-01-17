package com.github.bpazy.delaytask;

import com.github.bpazy.delaytask.jedis.JedisManager;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public class DelayTaskSchedule {
    private ExecutorService scheduleExecutor;
    private DelayTaskRunner task;

    public DelayTaskSchedule(JedisManager jedisManager, DelayTaskProperty delayTaskProperty,
                             ExecutorService delayTaskExecutor, DelayTaskLoader delayTaskLoader,
                             DelayTaskManager delayTaskManager) {
        this.scheduleExecutor = Executors.newSingleThreadExecutor();
        this.task = new DelayTaskRunner(jedisManager, delayTaskProperty, delayTaskExecutor, delayTaskLoader, delayTaskManager);
    }

    public void start() {
        scheduleExecutor.submit(task);
    }

    public void stop() {
        task.stop();
        scheduleExecutor.shutdownNow();
    }

    @Slf4j
    private static class DelayTaskRunner implements Runnable {
        private JedisManager jedisManager;
        private DelayTaskProperty delayTaskProperty;
        private ExecutorService delayTaskExecutor;
        private DelayTaskLoader delayTaskLoader;
        private DelayTaskManager delayTaskManager;
        private boolean working = true;

        public DelayTaskRunner(JedisManager jedisManager, DelayTaskProperty delayTaskProperty,
                               ExecutorService delayTaskExecutor, DelayTaskLoader delayTaskLoader,
                               DelayTaskManager delayTaskManager) {
            this.jedisManager = jedisManager;
            this.delayTaskProperty = delayTaskProperty;
            this.delayTaskExecutor = delayTaskExecutor;
            this.delayTaskLoader = delayTaskLoader;
            this.delayTaskManager = delayTaskManager;
        }

        @Override
        public void run() {
            while (working) {
                scanTaskAndRun();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        public void stop() {
            working = false;
        }

        private void scanTaskAndRun() {
            Jedis jedis = jedisManager.getJedis();
            long maxScore = DateTime.now().getMillis() / 1000;
            Set<String> taskIds = jedis.zrangeByScore(delayTaskProperty.getKey(), 0, maxScore);

            if (CollectionUtils.isEmpty(taskIds)) {
                return;
            }

            long deletedNum = jedis.zremrangeByScore(delayTaskProperty.getKey(), 0, maxScore);
            if (deletedNum == 0) {
                return;
            }

            List<DelayTask> delayTasks = delayTaskManager.queryDelayTasks(new ArrayList<>(taskIds));
            delayTasks.forEach(dt -> delayTaskExecutor.submit(() -> {
                try {
                    delayTaskLoader.getInstance(dt.getTargetClass()).run(dt.getArgument());
                } catch (Exception e) {
                    log.error("延迟运行任务失败", e);
                    dt.setFailReason(Throwables.getRootCause(e).getMessage());
                    delayTaskManager.updateDelayTask(dt);
                    return;
                }

                dt.setTaskStatus("执行完毕");
                delayTaskManager.updateDelayTask(dt);
            }));
        }
    }
}
