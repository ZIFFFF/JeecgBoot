import { defHttp } from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/live/zsEncodeTask/list',
  save='/live/zsEncodeTask/add',
  edit='/live/zsEncodeTask/edit',
  deleteOne = '/live/zsEncodeTask/delete',
  deleteBatch = '/live/zsEncodeTask/deleteBatch',
  importExcel = '/live/zsEncodeTask/importExcel',
  exportXls = '/live/zsEncodeTask/exportXls',
  taskSwitch = '/live/zsEncodeTask/taskSwitch',
  getTaskLogList = '/live/zsEncodeTask/logDates',
  taskLog = '/live/zsEncodeTask/taskLog',
}

/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 * @param params
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 * @param params
 * @param handleSuccess
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({url: Api.deleteOne, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 * @param handleSuccess
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({url: Api.deleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 * @param isUpdate
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params }, { isTransformResponse: false });
}

/**
 * 开启/停止任务
 * @param params
 */
export const toggleTask = async (params) => {
  return defHttp.post({ url: Api.taskSwitch, params }, { isTransformResponse: false });
}

/**
 * 获取任务日志日期
 */
export const getTaskLogList = (params) => {
  return defHttp.post({ url: Api.getTaskLogList, params }, {joinParamsToUrl: true});
};

/**
 * 按日期查看日志
 */
export const getTaskLogs = (params) => {
  return defHttp.post({ url: Api.taskLog, params }, { isTransformResponse: false });
};