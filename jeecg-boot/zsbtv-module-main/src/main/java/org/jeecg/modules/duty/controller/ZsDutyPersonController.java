package org.jeecg.modules.duty.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.duty.entity.ZsDutyPerson;
import org.jeecg.modules.duty.service.IZsDutyPersonService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 值班人员表
 * @Author: jeecg-boot
 * @Date:   2025-02-14
 * @Version: V1.0
 */
@Api(tags="值班人员表")
@RestController
@RequestMapping("/duty/zsDutyPerson")
@Slf4j
public class ZsDutyPersonController extends JeecgController<ZsDutyPerson, IZsDutyPersonService> {
	@Autowired
	private IZsDutyPersonService zsDutyPersonService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zsDutyPerson
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "值班人员表-分页列表查询")
	@ApiOperation(value="值班人员表-分页列表查询", notes="值班人员表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ZsDutyPerson>> queryPageList(ZsDutyPerson zsDutyPerson,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ZsDutyPerson> queryWrapper = QueryGenerator.initQueryWrapper(zsDutyPerson, req.getParameterMap());
		Page<ZsDutyPerson> page = new Page<ZsDutyPerson>(pageNo, pageSize);
		IPage<ZsDutyPerson> pageList = zsDutyPersonService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zsDutyPerson
	 * @return
	 */
	@AutoLog(value = "值班人员表-添加")
	@ApiOperation(value="值班人员表-添加", notes="值班人员表-添加")
	@RequiresPermissions("duty:zs_duty_person:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ZsDutyPerson zsDutyPerson) {
		zsDutyPersonService.save(zsDutyPerson);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zsDutyPerson
	 * @return
	 */
	@AutoLog(value = "值班人员表-编辑")
	@ApiOperation(value="值班人员表-编辑", notes="值班人员表-编辑")
	@RequiresPermissions("duty:zs_duty_person:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ZsDutyPerson zsDutyPerson) {
		zsDutyPersonService.updateById(zsDutyPerson);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "值班人员表-通过id删除")
	@ApiOperation(value="值班人员表-通过id删除", notes="值班人员表-通过id删除")
	@RequiresPermissions("duty:zs_duty_person:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		zsDutyPersonService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "值班人员表-批量删除")
	@ApiOperation(value="值班人员表-批量删除", notes="值班人员表-批量删除")
	@RequiresPermissions("duty:zs_duty_person:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zsDutyPersonService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "值班人员表-通过id查询")
	@ApiOperation(value="值班人员表-通过id查询", notes="值班人员表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ZsDutyPerson> queryById(@RequestParam(name="id",required=true) String id) {
		ZsDutyPerson zsDutyPerson = zsDutyPersonService.getById(id);
		if(zsDutyPerson==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(zsDutyPerson);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zsDutyPerson
    */
    @RequiresPermissions("duty:zs_duty_person:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZsDutyPerson zsDutyPerson) {
        return super.exportXls(request, zsDutyPerson, ZsDutyPerson.class, "值班人员表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("duty:zs_duty_person:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZsDutyPerson.class);
    }

}
