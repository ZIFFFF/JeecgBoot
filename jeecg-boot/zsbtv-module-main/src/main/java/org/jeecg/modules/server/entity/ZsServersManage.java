package org.jeecg.modules.server.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 服务器管理表
 * @Author: jeecg-boot
 * @Date:   2025-01-22
 * @Version: V1.0
 */
@Data
@TableName("zs_servers_manage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zs_servers_manage对象", description="服务器管理表")
public class ZsServersManage implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**服务器名称*/
	@Excel(name = "服务器名称", width = 15)
    @ApiModelProperty(value = "服务器名称")
    private String name;
	/**服务器地址*/
	@Excel(name = "服务器地址", width = 15)
    @ApiModelProperty(value = "服务器地址")
    private String host;
	/**公网IP地址*/
	@Excel(name = "公网IP地址", width = 15)
    @ApiModelProperty(value = "公网IP地址")
    private String publicIp;
	/**内网IP地址*/
	@Excel(name = "内网IP地址", width = 15)
    @ApiModelProperty(value = "内网IP地址")
    private String privateIp;
	/**SSH端口*/
	@Excel(name = "SSH端口", width = 15)
    @ApiModelProperty(value = "SSH端口")
    private Integer port;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String username;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private String password;
	/**私钥内容*/
	@Excel(name = "私钥内容", width = 15)
    @ApiModelProperty(value = "私钥内容")
    private String privateKey;
	/**私钥密码*/
	@Excel(name = "私钥密码", width = 15)
    @ApiModelProperty(value = "私钥密码")
    private String passphrase;
	/**服务器型号*/
	@Excel(name = "服务器型号", width = 15)
    @ApiModelProperty(value = "服务器型号")
    private String serverModel;
	/**CPU型号*/
	@Excel(name = "CPU型号", width = 15)
    @ApiModelProperty(value = "CPU型号")
    private String cpuModel;
	/**CPU核心数*/
	@Excel(name = "CPU核心数", width = 15)
    @ApiModelProperty(value = "CPU核心数")
    private Integer cpuCores;
	/**内存大小（GB）*/
	@Excel(name = "内存大小（GB）", width = 15)
    @ApiModelProperty(value = "内存大小（GB）")
    private String memoryGb;
	/**磁盘大小（GB）*/
	@Excel(name = "磁盘大小（GB）", width = 15)
    @ApiModelProperty(value = "磁盘大小（GB）")
    private String diskGb;
	/**操作系统*/
	@Excel(name = "操作系统", width = 15)
    @ApiModelProperty(value = "操作系统")
    private String os;
	/**服务器状态*/
	@Excel(name = "服务器状态", width = 15)
    @ApiModelProperty(value = "服务器状态")
    private Integer status;
	/**服务器描述*/
	@Excel(name = "服务器描述", width = 15)
    @ApiModelProperty(value = "服务器描述")
    private String description;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
}
