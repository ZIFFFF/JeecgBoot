<template>
    <Card title="值班日历">
        <template #extra>
            <a-button type="link" size="large">前往排班</a-button>
        </template>
        <a-calendar v-model:value="value">
            <template #headerRender="{ value, type, onChange }">
                <div style="display: flex; justify-content: space-between;">
                    <div style="text-align: right; margin-bottom: 10px; font-size: 15px">
                        值班人员：
                        <div v-for="(user, index) in dutyUser" :key="index"
                            style="display: inline-flex; align-items: center; margin-bottom: 5px; margin-right: 10px">
                            <div
                                :style="{ width: '10px', height: '10px', backgroundColor: user.color, borderRadius: '50%' }">
                            </div>
                            <div style="margin-left: 5px;">{{ user.name }}</div>
                        </div>
                    </div>
                    <div style="text-align: right; margin-bottom: 10px;">
                        <a-select :value="value.year()" @change="(year) => onChange(value.year(year))">
                            <a-select-option v-for="year in yearRange" :key="year">
                                {{ year }}年
                            </a-select-option>
                        </a-select>
                        <a-select :value="value.month() + 1" @change="(month) => onChange(value.month(month - 1))">
                            <a-select-option v-for="month in 12" :key="month">
                                {{ month }}月
                            </a-select-option>
                        </a-select>
                    </div>
                </div>
            </template>
            <template #dateCellRender="{ current }">
                <div class="duty-label" v-if="isInKeyDuty(current)">{{ dutyName }}</div>
                <ul class="events">
                    <li v-for="item in getListData(current)" :key="item.content">
                        <a-badge :status="item.type" :text="item.content" :style="item.style" />
                    </li>
                </ul>
            </template>
        </a-calendar>
    </Card>
</template>
<script lang="ts" setup>
import { Card } from 'ant-design-vue';
import { ref, computed } from 'vue';
import { Dayjs } from 'dayjs';
const value = ref<Dayjs>();

interface Duty {
    name: string;
    date: string; // 格式为 'YYYY-MM-DD'
    type: string;
    remarks: string;
    color: string;
}

const dutyUser = [{ name: '范健科', color: '#5cd15c' },
{ name: '孙汉明', color: '#ff5d5d' },
{ name: '唐凯峰', color: '#339fff' },
{ name: '王小灿', color: '#f3be79' },
{ name: '郑梓枫', color: '#d867eb' },]

const dutyList: Duty[] = [
    { name: '孙汉明', date: '2025-01-26', type: '*', remarks: '', color: '#ff5d5d' },
    { name: '范健科', date: '2025-01-26', type: '', remarks: '', color: '#5cd15c' },
    { name: '唐凯峰', date: '2025-01-26', type: '', remarks: '', color: '#339fff' },
    { name: '孙汉明', date: '2025-01-27', type: '*', remarks: '', color: '#ff5d5d' },
    { name: '范健科', date: '2025-01-27', type: '', remarks: '', color: '#5cd15c' },
    { name: '唐凯峰', date: '2025-01-27', type: '', remarks: '', color: '#339fff' },
    { name: '范健科', date: '2025-01-28', type: '', remarks: '(0:00-12:00)', color: '#5cd15c' },
    { name: '孙汉明', date: '2025-01-28', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-01-28', type: '', remarks: '(19:00-24:00)', color: '#339fff' },
    { name: '唐凯峰', date: '2025-01-29', type: '', remarks: '(00:00-08:30)', color: '#339fff' },
    { name: '范健科', date: '2025-01-29', type: '', remarks: '', color: '#5cd15c' },
    { name: '王小灿', date: '2025-01-29', type: '', remarks: '(14:30-24:00)', color: '#f3be79' },
    { name: '王小灿', date: '2025-01-30', type: '', remarks: '(14:30-24:00)', color: '#f3be79' },
    { name: '孙汉明', date: '2025-01-30', type: '', remarks: '(14:30-22:30)', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-01-31', type: '', remarks: '(08:30-22:30)', color: '#339fff' },
    { name: '唐凯峰', date: '2025-02-01', type: '', remarks: '(08:30-22:30)', color: '#339fff' },
    { name: '孙汉明', date: '2025-02-02', type: '', remarks: '(08:30-22:30)', color: '#ff5d5d' },
    { name: '范健科', date: '2025-02-03', type: '', remarks: '', color: '#5cd15c' },
    { name: '郑梓枫', date: '2025-02-03', type: '', remarks: '(14:30-22:30)', color: '#d867eb' },
    { name: '郑梓枫', date: '2025-02-04', type: '', remarks: '(08:30-22:30)', color: '#d867eb' },
    { name: '王小灿', date: '2025-02-05', type: '', remarks: '*', color: '#f3be79' },
    { name: '范健科', date: '2025-02-05', type: '', remarks: '', color: '#5cd15c' },
    { name: '郑梓枫', date: '2025-02-05', type: '', remarks: '', color: '#d867eb' },
    { name: '唐凯峰', date: '2025-02-06', type: '', remarks: '*', color: '#339fff' },
    { name: '王小灿', date: '2025-02-06', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-06', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-07', type: '', remarks: '*', color: '#ff5d5d' },
    { name: '郑梓枫', date: '2025-02-07', type: '', remarks: '', color: '#d867eb' },
    { name: '王小灿', date: '2025-02-07', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-08', type: '', remarks: '*', color: '#d867eb' },
    { name: '唐凯峰', date: '2025-02-08', type: '', remarks: '*', color: '#339fff' },
    { name: '王小灿', date: '2025-02-08', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-09', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-10', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-02-11', type: '', remarks: '', color: '#339fff' },
    { name: '王小灿', date: '2025-02-12', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-13', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-14', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-02-15', type: '', remarks: '', color: '#339fff' },
    { name: '王小灿', date: '2025-02-16', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-17', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-18', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-02-19', type: '', remarks: '', color: '#339fff' },
    { name: '王小灿', date: '2025-02-20', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-21', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-22', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-02-23', type: '', remarks: '', color: '#339fff' },
    { name: '王小灿', date: '2025-02-24', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-02-25', type: '', remarks: '', color: '#d867eb' },
    { name: '孙汉明', date: '2025-02-26', type: '', remarks: '', color: '#ff5d5d' },
    { name: '唐凯峰', date: '2025-02-27', type: '', remarks: '', color: '#339fff' },
    { name: '王小灿', date: '2025-02-28', type: '', remarks: '', color: '#f3be79' },
    { name: '郑梓枫', date: '2025-03-01', type: '', remarks: '', color: '#d867eb' },

];

const keyDutys = [{ name: '元旦值班', start: '2024-12-31', end: '2025-01-02' },
{ start: '2025-01-26', end: '2025-02-08', name: '春节值班' }];
let dutyName = '';
const isInKeyDuty = (current: Dayjs) => {
    for (let keyDuty of keyDutys) {
        const currentDate = current.format('YYYY-MM-DD');
        if (currentDate >= keyDuty.start && currentDate <= keyDuty.end) {
            dutyName = keyDuty.name;
            return true;
        }
    }
    return false;
};

const getListData = (current: Dayjs) => {
    const currentDate = current.format('YYYY-MM-DD');
    const duties = dutyList.filter(duty => duty.date === currentDate);
    return duties.map(duty => ({
        type: duty.type,
        content: `${duty.name} ${duty.remarks}`,
        style: { backgroundColor: duty.color }
    }));
};

// 动态生成年份范围
const yearRange = computed(() => {
    const currentYear = new Date().getFullYear();
    const startYear = currentYear - 3; // 从当前年份往前推
    const endYear = currentYear + 3; // 从当前年份往后推
    const years = [];
    for (let year = startYear; year <= endYear; year++) {
        years.push(year);
    }
    return years;
});

</script>
<style scoped>
.events {
    list-style: none;
    margin: 0;
    padding: 0;
}

.events .ant-badge-status {
    overflow: hidden;
    white-space: nowrap;
    width: 100%;
    text-overflow: ellipsis;
    font-size: 12px;
    border-radius: 10px;
}

.duty-label {
    position: absolute;
    top: 4px;
    left: 4px;
    font-size: 12px;
    font-weight: bold;
    color: #ff0000;
    /* 红色字体 */
}

.notes-month {
    text-align: center;
    font-size: 28px;
}

.notes-month section {
    font-size: 28px;
}
</style>