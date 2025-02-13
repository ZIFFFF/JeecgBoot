package org.jeecg.modules.live.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.live.entity.ZsEncodeTask;
import org.jeecg.modules.live.service.IZsEncodeTaskService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.live.vo.TaskSwitchVO;
import org.jeecg.modules.live.vo.TaskLogVO;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 编码任务表
 * @Author: jeecg-boot
 * @Date:   2025-01-21
 * @Version: V1.0
 */
@Api(tags="编码任务表")
@RestController
@RequestMapping("/live/zsEncodeTask")
@Slf4j
public class ZsEncodeTaskController extends JeecgController<ZsEncodeTask, IZsEncodeTaskService> {
	@Autowired
	private IZsEncodeTaskService zsEncodeTaskService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zsEncodeTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "编码任务表-分页列表查询")
	@ApiOperation(value="编码任务表-分页列表查询", notes="编码任务表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ZsEncodeTask>> queryPageList(ZsEncodeTask zsEncodeTask,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ZsEncodeTask> queryWrapper = QueryGenerator.initQueryWrapper(zsEncodeTask, req.getParameterMap());
		Page<ZsEncodeTask> page = new Page<ZsEncodeTask>(pageNo, pageSize);
		IPage<ZsEncodeTask> pageList = zsEncodeTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zsEncodeTask
	 * @return
	 */
	@AutoLog(value = "编码任务表-添加")
	@ApiOperation(value="编码任务表-添加", notes="编码任务表-添加")
	@RequiresPermissions("live:zs_encode_task:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ZsEncodeTask zsEncodeTask) {
		zsEncodeTaskService.save(zsEncodeTask);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zsEncodeTask
	 * @return
	 */
	@AutoLog(value = "编码任务表-编辑")
	@ApiOperation(value="编码任务表-编辑", notes="编码任务表-编辑")
	@RequiresPermissions("live:zs_encode_task:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ZsEncodeTask zsEncodeTask) {
		zsEncodeTaskService.updateById(zsEncodeTask);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "编码任务表-通过id删除")
	@ApiOperation(value="编码任务表-通过id删除", notes="编码任务表-通过id删除")
	@RequiresPermissions("live:zs_encode_task:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		zsEncodeTaskService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "编码任务表-批量删除")
	@ApiOperation(value="编码任务表-批量删除", notes="编码任务表-批量删除")
	@RequiresPermissions("live:zs_encode_task:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zsEncodeTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "编码任务表-通过id查询")
	@ApiOperation(value="编码任务表-通过id查询", notes="编码任务表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ZsEncodeTask> queryById(@RequestParam(name="id",required=true) String id) {
		ZsEncodeTask zsEncodeTask = zsEncodeTaskService.getById(id);
		if(zsEncodeTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(zsEncodeTask);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zsEncodeTask
    */
    @RequiresPermissions("live:zs_encode_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZsEncodeTask zsEncodeTask) {
        return super.exportXls(request, zsEncodeTask, ZsEncodeTask.class, "编码任务表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("live:zs_encode_task:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZsEncodeTask.class);
    }


	/**
	 * 任务启动
	 *  */
	@AutoLog(value = "任务启停")
	@ApiOperation(value="任务启停", notes="任务启停")
	@RequiresPermissions("live:zs_encode_task:switch")
	@RequestMapping(value = "/taskSwitch", method = {RequestMethod.POST})
	public Result<T> taskSwitch(@RequestBody TaskSwitchVO taskSwitchVO) {
		ZsEncodeTask zsEncodeTask = zsEncodeTaskService.getById(taskSwitchVO.getId());
		if(taskSwitchVO.getStatus()==1) {
			Result<T> result = zsEncodeTaskService.startSwitch(zsEncodeTask);
			return new Result<>(result.getCode(), result.getMessage());
		} else {
			Result<T> result = zsEncodeTaskService.stopSwitch(zsEncodeTask);
			return new Result<>(result.getCode(), result.getMessage());
		}
	}


	 /**
	  * 任务日志日期查询
	  *  */
	 @AutoLog(value = "任务日志日期查询")
	 @ApiOperation(value="任务日志日期查询", notes="任务日志日期查询")
	 @RequiresPermissions("live:zs_encode_task:checkLog")
	 @RequestMapping(value = "/logDates", method = {RequestMethod.POST})
	 public Result<Object> getTaskLogList(@RequestParam(name="id",required=true) String id) {
		 ZsEncodeTask zsEncodeTask = zsEncodeTaskService.getById(id);
		 return Result.OK(zsEncodeTaskService.getTaskLogList(zsEncodeTask));
	 }

	 /**
	  * 任务日志查询
	  *  */
	 @AutoLog(value = "任务日志")
	 @ApiOperation(value="任务日志", notes="任务日志")
	 @RequiresPermissions("live:zs_encode_task:checkLog")
	 @RequestMapping(value = "/taskLog", method = {RequestMethod.POST})
	 public Result<Object> getTaskLog(@RequestBody TaskLogVO taskLogVo) {
		 ZsEncodeTask zsEncodeTask = zsEncodeTaskService.getById(taskLogVo.getId());
		 return Result.OK(zsEncodeTaskService.getTaskLog(zsEncodeTask, taskLogVo.getTaskDate()));
	 }


}
