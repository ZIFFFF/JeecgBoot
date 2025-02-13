package org.jeecg.modules.server.controller;

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
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.server.entity.ZsServersManage;
import org.jeecg.modules.server.service.IZsServersManageService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.server.utils.ServerUtils;
import org.jeecg.modules.server.vo.serverConnectVO;
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
 * @Description: 服务器管理表
 * @Author: jeecg-boot
 * @Date:   2025-01-22
 * @Version: V1.0
 */
@Api(tags="服务器管理表")
@RestController
@RequestMapping("/sever/zsServersManage")
@Slf4j
public class ZsServersManageController extends JeecgController<ZsServersManage, IZsServersManageService> {
	@Autowired
	private IZsServersManageService zsServersManageService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zsServersManage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "服务器管理表-分页列表查询")
	@ApiOperation(value="服务器管理表-分页列表查询", notes="服务器管理表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ZsServersManage>> queryPageList(ZsServersManage zsServersManage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<ZsServersManage> queryWrapper = QueryGenerator.initQueryWrapper(zsServersManage, req.getParameterMap());
		Page<ZsServersManage> page = new Page<ZsServersManage>(pageNo, pageSize);
		IPage<ZsServersManage> pageList = zsServersManageService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zsServersManage
	 * @return
	 */
	@AutoLog(value = "服务器管理表-添加")
	@ApiOperation(value="服务器管理表-添加", notes="服务器管理表-添加")
	@RequiresPermissions("sever:zs_servers_manage:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ZsServersManage zsServersManage) {
		String salt = oConvertUtils.randomGen(8);
		String passwordEncode = PasswordUtil.encrypt(zsServersManage.getPassword(), zsServersManage.getHost(), salt);
		zsServersManage.setPrivateKey(salt);
		zsServersManage.setPassword(passwordEncode);
		zsServersManageService.save(zsServersManage);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zsServersManage
	 * @return
	 */
	@AutoLog(value = "服务器管理表-编辑")
	@ApiOperation(value="服务器管理表-编辑", notes="服务器管理表-编辑")
	@RequiresPermissions("sever:zs_servers_manage:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ZsServersManage zsServersManage) {
		zsServersManageService.updateById(zsServersManage);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "服务器管理表-通过id删除")
	@ApiOperation(value="服务器管理表-通过id删除", notes="服务器管理表-通过id删除")
	@RequiresPermissions("sever:zs_servers_manage:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		zsServersManageService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "服务器管理表-批量删除")
	@ApiOperation(value="服务器管理表-批量删除", notes="服务器管理表-批量删除")
	@RequiresPermissions("sever:zs_servers_manage:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zsServersManageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "服务器管理表-通过id查询")
	@ApiOperation(value="服务器管理表-通过id查询", notes="服务器管理表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ZsServersManage> queryById(@RequestParam(name="id",required=true) String id) {
		ZsServersManage zsServersManage = zsServersManageService.getById(id);
		if(zsServersManage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(zsServersManage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zsServersManage
    */
    @RequiresPermissions("sever:zs_servers_manage:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZsServersManage zsServersManage) {
        return super.exportXls(request, zsServersManage, ZsServersManage.class, "服务器管理表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("sever:zs_servers_manage:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZsServersManage.class);
    }

	 /**
	  *  测试连接
	  *
	  * @param serverConnectVO
	  * @return
	  */
//	 @AutoLog(value = "服务器管理表-测试连接")
	 @ApiOperation(value="服务器管理表-测试连接", notes="服务器管理表-测试连接")
	 @RequestMapping(value = "/checkServerConnect", method = {RequestMethod.POST})
	 public Result<String> checkServerConnect(@RequestBody serverConnectVO serverConnectVO) {
		 boolean result = ServerUtils.checkSSHConnectivity(serverConnectVO.getHost(), serverConnectVO.getUsername(), serverConnectVO.getPassword());
		 return Result.OK(String.valueOf(result));
	 }

	 /**
	  *  获取服务器信息
	  *
	  * @param serverConnectVO
	  * @return
	  */
//	 @AutoLog(value = "服务器管理表-获取服务器信息")
	 @ApiOperation(value="服务器管理表-获取服务器信息", notes="服务器管理表-获取服务器信息")
	 @RequestMapping(value = "/getServerInfo", method = {RequestMethod.POST})
	 public Result<?> getServerInfo(@RequestBody serverConnectVO serverConnectVO) {
		 ZsServersManage zsServersManage = ServerUtils.getServerInfo(serverConnectVO.getHost(), serverConnectVO.getUsername(), serverConnectVO.getPassword());
		 if (zsServersManage == null) {
			 return Result.error("获取服务器信息失败");
		 }
		 return Result.OK(zsServersManage);
	 }

}
