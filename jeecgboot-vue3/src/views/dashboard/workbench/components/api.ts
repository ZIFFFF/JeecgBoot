import {defHttp} from '/@/utils/http/axios';

enum Api {
    keyDutyList = '/duty/zsKeyDuty/list',
}

/**
 * 重点值班列表接口
 * 
 */
export const keyDutyList = () =>
    defHttp.get({url: Api.keyDutyList});
  