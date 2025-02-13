<template>
  <div class="p-2">

    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'live:zs_encode_task:add'" @click="handleAdd"
          preIcon="ant-design:plus-outlined"> 新增</a-button>
      </template>
      <!--任务状态-->
      <template #status="{ record }">
        <div class="status-dot" :class="{ 'active': record.status }"></div>
      </template>

      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>

      <!-- <template v-slot:bodyCell="{ column, record, index, text }">
      </template> -->
    </BasicTable>
    <!-- 表单区域 -->
    <ZsEncodeTaskModal ref="registerModal" @success="handleSuccess"></ZsEncodeTaskModal>
    <ZsEncodeTaskLogDrawer @register="registerLogDrawer" />
  </div>
</template>

<script lang="ts" name="live-zsEncodeTask" setup>
import { ref, reactive } from 'vue';
import { BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { columns } from './ZsEncodeTask.data';
import { list, deleteOne, getImportUrl, getExportUrl, toggleTask, getTaskLogList } from './ZsEncodeTask.api';
import ZsEncodeTaskModal from './components/ZsEncodeTaskModal.vue';
import { useMessage } from '/@/hooks/web/useMessage';
import ZsEncodeTaskLogDrawer from './components/ZsEncodeTaskLogDrawer.vue';
import { useDrawer } from '/@/components/Drawer';
const { createMessage } = useMessage();
const queryParam = reactive<any>({});
const registerModal = ref();
//注册table数据
const { tableContext} = useListPage({
  tableProps: {
    title: '编码任务表',
    api: list,
    columns,
    canResize: false,
    useSearchForm: false,
    actionColumn: {
      width: 300,
      fixed: 'right',
    },
    beforeFetch: async (params) => {
      return Object.assign(params, queryParam);
    },
  },
  exportConfig: {
    name: "编码任务表",
    url: getExportUrl,
    params: queryParam,
  },
  importConfig: {
    url: getImportUrl,
    success: handleSuccess
  },
});
const [registerTable, { reload }, { selectedRowKeys }] = tableContext;

/**
 * 新增事件
 */
function handleAdd() {
  registerModal.value.disableSubmit = false;
  registerModal.value.add();
}

/**
 * 编辑事件
 */
function handleEdit(record: Recordable) {
  registerModal.value.disableSubmit = false;
  registerModal.value.edit(record);
}

/**
 * 详情
 */
function handleDetail(record: Recordable) {
  registerModal.value.disableSubmit = true;
  registerModal.value.edit(record);
}

/**
 * 删除事件
 */
async function handleDelete(record) {
  await deleteOne({ id: record.id }, handleSuccess);
}

/**
 * 成功回调
 */
function handleSuccess() {
  (selectedRowKeys.value = []) && reload();
}

/**
 * 操作栏
 */
const [registerLogDrawer, { openDrawer: openLogDrawer }] = useDrawer();

// 修改日志按钮点击事件
function getTableAction(record) {
  return [
    {
      label: record.status === 1 ? '停止' : '启动',
      type: record.status === 1 ? 'error' : 'success',
      onClick: ()=> {
        if (record.status === 1) {
          handleStatusChange(0, record)
          return;
        }
        handleStatusChange(1, record) 
      },
      auth: 'live:zs_encode_task:switch'
    },
    {
      label: '日志',
      onClick: () => {
        try {
          // 获取日志日期列表
          getTaskLogList({ id: record.id }).then((res) => {
            if (res.success && res.result?.length > 0) {
              openLogDrawer(true, { record, res });
            } else {
              createMessage.warning('无日志数据'); // 提示用户无日志数据
            }
          })
        } catch (error) {
          createMessage.error('获取日志日期列表失败:', error);
        }
      },
      auth: 'live:zs_encode_task:checkLog'
    },
    {
      label: record.status === 1 ? '查看' : '编辑',
      onClick: () => {
        if (record.status === 1) {
          handleDetail(record);
          return;
        }
        handleEdit(record);
      },
      auth: 'live:zs_encode_task:edit'
    },
    {
      label: '删除',
      onClick: () => {
        if (record.status === 1) {
          createMessage.warning('任务运行中，请先停止任务');
          return;
        }
        handleDelete(record);
      },
      popConfirm: record.status !== 1 && {
        title: '是否确认删除',
        confirm: handleDelete.bind(null, record),
        placement: 'topLeft',
      },
      auth: 'live:zs_encode_task:delete'
    }
  ];
}

/**
 * 任务启动停止 
 */
async function handleStatusChange(checked, record) {
  const params = {
    id: record.id,
    status: checked ? 1 : 0,
  };
  record.switchLoading = true;
  toggleTask(params).then((res) => {
    record.switchLoading = false;
    if(res.code === 200) {
      record.status = checked;
      createMessage.success('任务状态修改成功');
      reload();
    } else {
      record.switchLoading = false;
      createMessage.error(res.message);
      return;
    }
  }).catch(() => {
    record.switchLoading = false;
    createMessage.error('操作失败');
  });
}


</script>

<style lang="less" scoped>
.jeecg-basic-table-form-container {
  padding: 0;

  .table-page-search-submitButtons {
    display: block;
    margin-bottom: 24px;
    white-space: nowrap;
  }

  .query-group-cust {
    min-width: 100px !important;
  }

  .query-group-split-cust {
    width: 30px;
    display: inline-block;
    text-align: center
  }

  .ant-form-item:not(.ant-form-item-with-help) {
    margin-bottom: 16px;
    height: 32px;
  }

  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #909399;
  margin: 0 auto;
  transition: background-color 0.3s;
}

.status-dot.active {
  background-color: #67C23A;
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.5);
}
</style>
