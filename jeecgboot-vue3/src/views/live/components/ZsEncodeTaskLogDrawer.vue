<template>
    <BasicDrawer v-bind="$attrs" @register="registerDrawer" title="任务日志" width="800px">
        <div class="log-header">
            <div class="date-list">
                <a-button v-for="date in logDates" :key="date" :type="currentDate === date ? 'primary' : 'default'"
                    @click="handleDateChange(date)" class="date-btn">
                    {{ date }}
                </a-button>
            </div>
        </div>
        <div class="log-content">
            <div v-for="(log, index) in historyLogs" :key="index" class="log-line">{{ log }}</div>
        </div>
    </BasicDrawer>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
import { getTaskLogList, getTaskLogs } from '../ZsEncodeTask.api';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();
const historyLogs = ref<string[]>([]);
const taskId = ref('');
const logDates = ref<string[]>([]);
const currentDate = ref<string>('');

const [registerDrawer, { setDrawerProps }] = useDrawerInner(async (data) => {
    taskId.value = data.record.id;
    logDates.value = data.res.result.sort((a, b) => new Date(b).getTime() - new Date(a).getTime());
    currentDate.value = data.res.result[0];
    setDrawerProps({ loading: false });
    await loadTaskLogs();
}, () => {
    historyLogs.value = [];
    logDates.value = [];
    currentDate.value = '';
    taskId.value = '';
});

/**
 * 获取指定日期的日志内容
 */
async function loadTaskLogs() {
    if (!currentDate.value) return;
    try {
        const res = await getTaskLogs({
            id: taskId.value,
            taskDate: currentDate.value
        });
        if (res.success) {
            historyLogs.value = res.result.split('\n');
        }
    } catch (error) {
        console.error('获取日志内容失败:', error);
    }
}

/**
 * 日期切换处理
 */
const handleDateChange = (date: string) => {
    currentDate.value = date;
    loadTaskLogs();
};

</script>

<style lang="less" scoped>
.log-header {
    padding: 0 12px 12px 12px;
    border-bottom: 1px solid #d9d9d9;

    .date-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .date-btn {
            font-size: 12px;
            padding: 0 12px;
            height: 28px;
            line-height: 26px;
            border-radius: 4px;
        }
    }
}

.log-content {
    height: calc(100vh - 180px);
    overflow-y: auto;
    padding: 12px;
    background: #1e1e1e;
    color: #fff;
    font-family: monospace;
    white-space: pre-wrap;
    word-wrap: break-word;

    .log-line {
        font-size: 12px;
        line-height: 1.5;
        margin: 2px 0;
    }
}
</style>