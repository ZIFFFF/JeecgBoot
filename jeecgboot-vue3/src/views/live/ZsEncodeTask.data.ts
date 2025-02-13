import {BasicColumn} from '/@/components/Table';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '',
    align: "center",
    dataIndex: 'status',
    width: 50,
    slots: { customRender: 'status' }
  },
  {
    title: '任务名称',
    align: "center",
    dataIndex: 'taskName',
    width: 250
  },
  {
    title: '源地址',
    align: "center",
    dataIndex: 'sourceUrl'
  },
  {
    title: '输出地址',
    align: "center",
    dataIndex: 'pushUrl'
  },
  {
    title: '服务器',
    align: "center",
    dataIndex: 'server_dictText',
    width: 200
  },
  {
    title: '启动时间',
    align: "center",
    dataIndex: 'startTime',
    width: 200
  },
];

