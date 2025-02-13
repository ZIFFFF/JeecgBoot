package org.jeecg.modules.live.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskLogVO implements Serializable {

    private static final long serialVersionUID = 3238429500037511283L;

    String id;

    String taskDate;

}
