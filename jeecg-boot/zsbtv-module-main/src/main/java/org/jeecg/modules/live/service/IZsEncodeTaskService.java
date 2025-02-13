package org.jeecg.modules.live.service;

import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.live.entity.ZsEncodeTask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.live.vo.TaskSwitchVO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 编码任务表
 * @Author: jeecg-boot
 * @Date:   2025-01-21
 * @Version: V1.0
 */
public interface IZsEncodeTaskService extends IService<ZsEncodeTask> {

    /**
     * 任务开关
     * @param zsEncodeTask
     * @return
     */
    Result<T> startSwitch(ZsEncodeTask zsEncodeTask);

    Result<T> stopSwitch(ZsEncodeTask zsEncodeTask);

    Result<List<String>> getTaskLogList(ZsEncodeTask zsEncodeTask);

    String getTaskLog(ZsEncodeTask zsEncodeTask, String taskDate);

    void checkTaskStatus(ZsEncodeTask zsEncodeTask);
}
