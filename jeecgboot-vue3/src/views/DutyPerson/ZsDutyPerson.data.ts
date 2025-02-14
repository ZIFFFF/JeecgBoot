import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '组别',
    align: "center",
    dataIndex: 'name'
  },
  {
    title: '人员信息',
    align: "center",
    dataIndex: 'userList'
  },
  {
    title: '创建人',
    align: "center",
    dataIndex: 'createBy'
  },
  {
    title: '创建日期',
    align: "center",
    dataIndex: 'createTime'
  },
];

// 高级查询数据
export const superQuerySchema = {
  name: {title: '组别',order: 0,view: 'text', type: 'string',},
  userList: {title: '人员信息',order: 1,view: 'textarea', type: 'string',},
  createBy: {title: '创建人',order: 2,view: 'text', type: 'string',},
  createTime: {title: '创建日期',order: 3,view: 'datetime', type: 'string',},
};
