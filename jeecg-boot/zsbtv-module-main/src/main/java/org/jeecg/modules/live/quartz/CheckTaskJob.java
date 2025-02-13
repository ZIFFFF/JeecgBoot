package org.jeecg.modules.live.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.live.entity.ZsEncodeTask;
import org.jeecg.modules.live.service.IZsEncodeTaskService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ：ZZF
 * @date ：Created in 2025/02/09
 * @description：定时检查ffmpeg任务状态
 * @modified By：
 * @version: 1.0
 */
@Slf4j
public class CheckTaskJob implements Job {

    @Autowired
    private IZsEncodeTaskService zsEncodeTaskService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        QueryWrapper<ZsEncodeTask> queryWrapper = new QueryWrapper<ZsEncodeTask>();
        queryWrapper.eq("status", 1);
        List<ZsEncodeTask> zsEncodeTasks = zsEncodeTaskService.list(queryWrapper);

        // 创建一个单线程的线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 用于存储任务的 Future 对象
        List<Future<?>> futures = new ArrayList<>();

        for (ZsEncodeTask zsEncodeTask : zsEncodeTasks) {
            // 提交任务到线程池
            Future<?> future = executorService.submit(() -> {
                zsEncodeTaskService.checkTaskStatus(zsEncodeTask);
                log.info("定时检查ffmpeg任务状态: {}", zsEncodeTask.getId());
            });
            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get(); // 阻塞直到任务完成
            } catch (InterruptedException | ExecutionException e) {
                log.error("任务执行出错", e);
            }
        }

        // 关闭线程池
        executorService.shutdown();
    }
}
