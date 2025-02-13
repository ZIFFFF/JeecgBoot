package org.jeecg.modules.live.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 编码任务表
 * @Author: jeecg-boot
 * @Date:   2025-01-21
 * @Version: V1.0
 */
@Data
@TableName("zs_encode_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zs_encode_task对象", description="编码任务表")
public class ZsEncodeTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
	/**源地址*/
	@Excel(name = "源地址", width = 15)
    @ApiModelProperty(value = "源地址")
    private String sourceUrl;
	/**PID*/
	@Excel(name = "PID", width = 15)
    @ApiModelProperty(value = "PID")
    private String pid;
	/**输出地址*/
	@Excel(name = "输出地址", width = 15)
    @ApiModelProperty(value = "输出地址")
    private String pushUrl;
	/**任务状态*/
	@Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态")
    private Integer status;
    /**运行服务器*/
    @Excel(name = "运行服务器", width = 15, dictTable = "zs_servers_manage", dicText = "name", dicCode = "id")
    @Dict(dictTable = "zs_servers_manage", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "运行服务器")
    private String server;
    /**运行进程号*/
    @Excel(name = "运行进程号", width = 15)
    @ApiModelProperty(value = "运行进程号")
    private Integer processId;
	/**启动时间*/
	@Excel(name = "启动时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "启动时间")
    private java.util.Date startTime;
	/**是否自动重启*/
    @Excel(name = "是否自动重启", width = 15,replace = {"是_Y","否_N"} )
    @ApiModelProperty(value = "是否自动重启")
    private String enableReboot;
	/**自动重启时间*/
	@Excel(name = "自动重启时间", width = 15)
    @ApiModelProperty(value = "自动重启时间")
    private String rebootTime;
	/**是否自动恢复*/
    @Excel(name = "是否自动恢复", width = 15,replace = {"是_Y","否_N"} )
    @ApiModelProperty(value = "是否自动恢复")
    private String enableRecovery;
	/**运行命令*/
	@Excel(name = "运行命令", width = 15)
    @ApiModelProperty(value = "运行命令")
    private String command;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
}
