import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '服务器名称',
    align: "center",
    dataIndex: 'name'
  },
  {
    title: '服务器地址',
    align: "center",
    dataIndex: 'host'
  },
  {
    title: '公网IP地址',
    align: "center",
    dataIndex: 'publicIp'
  },
  {
    title: '内网IP地址',
    align: "center",
    dataIndex: 'privateIp'
  },
  {
    title: '服务器型号',
    align: "center",
    dataIndex: 'serverModel'
  },
  {
    title: '操作系统',
    align: "center",
    dataIndex: 'os'
  },
  {
    title: '服务器状态',
    align: "center",
    dataIndex: 'status'
  },
];

// 高级查询数据
export const superQuerySchema = {
  name: {title: '服务器名称',order: 0,view: 'text', type: 'string',},
  host: {title: '服务器地址',order: 1,view: 'text', type: 'string',},
  publicIp: {title: '公网IP地址',order: 2,view: 'text', type: 'string',},
  privateIp: {title: '内网IP地址',order: 3,view: 'text', type: 'string',},
  serverModel: {title: '服务器型号',order: 9,view: 'text', type: 'string',},
  os: {title: '操作系统',order: 14,view: 'text', type: 'string',},
  status: {title: '服务器状态',order: 15,view: 'number', type: 'number',},
};
