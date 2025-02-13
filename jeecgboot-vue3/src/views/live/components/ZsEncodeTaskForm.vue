<template>
    <a-spin :spinning="confirmLoading">
        <JFormContainer :disabled="disabled">
            <template #detail>
                <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol"
                    name="ZsEncodeTaskForm">
                    <a-row>
                        <a-col :span="24">
                            <a-form-item label="任务名称" v-bind="validateInfos.taskName" id="ZsEncodeTaskForm-taskName"
                                name="taskName">
                                <a-input v-model:value="formData.taskName" placeholder="请输入任务名称" allow-clear></a-input>
                                <div class="form-tip-text">请填写标题文字10-20字左右。</div>
                            </a-form-item>
                        </a-col>
						<a-col :span="24">
							<a-form-item label="运行服务器" v-bind="validateInfos.server" id="ZsEncodeTaskForm-server" name="server">
								<j-dict-select-tag v-model:value="formData.server" dictCode="zs_servers_manage,name,id" placeholder="请选择运行服务器"  allow-clear />
							</a-form-item>
						</a-col>
                        <a-divider orientation="left"
                            style="color: cadetblue; background-color: aliceblue;">输入配置
                            <a style="font-size: 12px;"  @click="openInputRulesModal"> Url规则</a>
                        </a-divider>
                        <a-col :span="24">
                            <a-form-item label="源地址" v-bind="validateInfos.sourceUrl" id="ZsEncodeTaskForm-sourceUrl"
                                name="sourceUrl">
                                <a-input v-model:value="formData.sourceUrl" placeholder="请输入源地址" allow-clear></a-input>
                                <div class="form-tip-text">支持HTTP/UDP单播/UDP多播/RTMP/RTSP/MMS/HLS等协议，支持jpg、png图片作为视频输入源。
                                </div>
                            </a-form-item>
                        </a-col>
                        <a-col :span="24">
                            <a-form-item label="PID" v-bind="validateInfos.pid" id="ZsEncodeTaskForm-pid" name="pid"
                                v-if="isUdpSource">
                                <a-input v-model:value="formData.pid" placeholder="请输入PID" allow-clear></a-input>
                                <div class="form-tip-text">UDP 协议需要配置 PID，范围：1-65535</div>
                            </a-form-item>
                        </a-col>
                        <a-divider orientation="left"
                            style="color: cadetblue; background-color: aliceblue;">输出配置</a-divider>
                        <a-col :span="24">
                            <a-form-item label="输出地址" v-bind="validateInfos.pushUrl" id="ZsEncodeTaskForm-pullUrl"
                                name="pullUrl">
                                <a-input v-model:value="formData.pushUrl" placeholder="请输入输出地址" allow-clear></a-input>
                                <div class="form-tip-text">支持RTMP/UDP协议输出</div>
                            </a-form-item>
                        </a-col>
                        <!-- <a-col :span="24">
							<a-form-item label="任务状态" v-bind="validateInfos.status" id="ZsEncodeTaskForm-status" name="status">
								<a-input-number v-model:value="formData.status" placeholder="请输入任务状态" style="width: 100%" />
							</a-form-item>
						</a-col> -->
                        <!-- <a-col :span="24">
							<a-form-item label="启动时间" v-bind="validateInfos.startTime" id="ZsEncodeTaskForm-startTime" name="startTime">
								<a-date-picker placeholder="请选择启动时间"  v-model:value="formData.startTime" showTime value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"  allow-clear />
							</a-form-item>
						</a-col> -->
                        <a-divider orientation="left"
                            style="color: cadetblue; background-color: aliceblue;">任务配置</a-divider>
                        <a-col :span="24">
                            <a-form-item label="是否自动恢复" v-bind="validateInfos.enableRecovery"
                                id="ZsEncodeTaskForm-enableRecovery" name="enableRecovery">
                                <j-switch v-model:value="formData.enableRecovery"></j-switch>
                            </a-form-item>
                        </a-col>
                        <a-col :span="24">
                            <a-form-item label="是否自动重启" v-bind="validateInfos.enableReboot"
                                id="ZsEncodeTaskForm-enableReboot" name="enableReboot">
                                <j-switch v-model:value="formData.enableReboot"></j-switch>
                            </a-form-item>
                        </a-col>
                        <a-col :span="24">
                            <a-form-item label="自动重启时间" v-bind="validateInfos.rebootTime"
                                id="ZsEncodeTaskForm-rebootTime" name="rebootTime">
                                <time-picker placeholder="请选择自动重启时间" value-format="HH:mm:ss"
                                    v-model:value="formData.rebootTime" style="width: 100%" allow-clear />
                            </a-form-item>
                        </a-col>
                        <a-divider orientation="left"
                            style="color: cadetblue; background-color: aliceblue;">运行状态</a-divider>
						<a-col :span="24">
							<a-form-item label="运行进程号" v-bind="validateInfos.processId" id="ZsEncodeTaskForm-processId" name="processId">
								<a-input-number v-model:value="formData.processId" style="width: 100%" disabled/>
							</a-form-item>
						</a-col>
                        <a-col :span="24">
                            <a-form-item label="运行命令" v-bind="validateInfos.command" id="ZsEncodeTaskForm-command"
                                name="command">
                                <a-textarea v-model:value="formData.command" :rows="4" disabled />
                            </a-form-item>
                        </a-col>
                    </a-row>
                </a-form>
            </template>
        </JFormContainer>

        <InputRulesModal v-model:visible="showRules" />
    </a-spin>
</template>

<script lang="ts" setup>
import { ref, reactive, defineExpose, nextTick, defineProps, computed, watch } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import JSwitch from '/@/components/Form/src/jeecg/components/JSwitch.vue';
import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
import { TimePicker } from 'ant-design-vue';
import { getValueType } from '/@/utils';
import { saveOrUpdate } from '../ZsEncodeTask.api';
import { Form } from 'ant-design-vue';
import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
import InputRulesModal from './InputRulesModal.vue'
import { duplicateValidate } from '/@/utils/helper/validator'
// 添加抽屉控制变量和方法
const showRules = ref(false);
const openInputRulesModal = () => {
    showRules.value = true;
};
const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true }
});
const formRef = ref();
const useForm = Form.useForm;
const emit = defineEmits(['register', 'ok']);
const formData = reactive<Record<string, any>>({
    id: '',
    taskName: '',
    sourceUrl: '',
    pid: '',
    pushUrl: '',
    status: 0,
    server: '',   
    processId: '',
    startTime: '',
    enableReboot: '',
    rebootTime: '',
    enableRecovery: '',
    command: '',
});
const { createMessage } = useMessage();
const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
const confirmLoading = ref<boolean>(false);
//表单验证
const validatorRules = reactive({
    taskName: [{ required: true, message: '请输入任务名称!' }, { validator: nameDuplicatevalidate }],
    sourceUrl: [{ required: true, message: '请输入源地址!' },],
    pid: [{ required: false },],
    pullUrl: [{ required: true, message: '请输入输出地址!' },],
    status: [{ required: true, message: '请输入任务状态!' },],
    enableReboot: [{ required: true, message: '请输入是否自动重启!' },],
    enableRecovery: [{ required: true, message: '请输入是否自动恢复!' },],
});
const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

// 表单禁用
const disabled = computed(() => {
    if (props.formBpm === true) {
        if (props.formData.disabled === false) {
            return false;
        } else {
            return true;
        }
    }
    return props.formDisabled;
});

// 判断输入地址是否是udp协议
const isUdpSource = computed(() => {
    return formData.sourceUrl.toLowerCase().startsWith('udp://')
});

/**
 * 命令生成
 */ 
const commandTemplate = 'ffmpeg -i ${sourceUrl} -c:v libx264 -preset veryfast -b:v 2000k -c:a aac -b:a 128k -f flv ${pushUrl}';

// 监听源地址和输出地址变化，自动生成命令
watch([() => formData.sourceUrl, () => formData.pushUrl, () => formData.pid], () => {
    generateCommand();
}, { deep: true });

// 生成命令
function generateCommand() {
    if (!formData.sourceUrl || !formData.pushUrl) {
        formData.command = '';
        return;
    }
    let command = commandTemplate;
    if (isUdpSource.value && formData.pid) {
        const inputPattern = /-i\s+\${sourceUrl}/;
        command = command.replace(inputPattern, `-i ${formData.sourceUrl} -map 0:p:${formData.pid}`);
        command = command.replace('${sourceUrl}', '');
    } else {
        command = command.replace('${sourceUrl}', formData.sourceUrl);
    }

    command = command.replace('${pushUrl}', formData.pushUrl);
    formData.command = command;
}

/**
 * 新增
 */
function add() {
    edit({});
}

/**
 * 编辑
 */
function edit(record) {
    
    nextTick(() => {
        resetFields();
        const tmpData = {};
        Object.keys(formData).forEach((key) => {
            if (record.hasOwnProperty(key)) {
                tmpData[key] = record[key]
            }
        })
        //赋值
        Object.assign(formData, tmpData);
    });
}

/**
 * 提交数据
 */
async function submitForm() {
    try {
        // 触发表单验证
        await validate();
    } catch ({ errorFields }) {
        if (errorFields) {
            const firstField = errorFields[0];
            if (firstField) {
                formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
            }
        }
        return Promise.reject(errorFields);
    }
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
        isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
        //如果该数据是数组并且是字符串类型
        if (model[data] instanceof Array) {
            let valueType = getValueType(formRef.value.getProps, data);
            //如果是字符串类型的需要变成以逗号分割的字符串
            if (valueType === 'string') {
                model[data] = model[data].join(',');
            }
        }
    }
    await saveOrUpdate(model, isUpdate.value)
        .then((res) => {
            if (res.success) {
                createMessage.success(res.message);
                emit('ok');
            } else {
                createMessage.warning(res.message);
            }
        })
        .finally(() => {
            confirmLoading.value = false;
        });
}
async function nameDuplicatevalidate(_r, value) {
    return duplicateValidate('zs_encode_task', 'task_name', value, formData.id || '')
  }

defineExpose({
    add,
    edit,
    submitForm,
});
</script>

<style lang="less" scoped>
.antd-modal-form {
    padding: 14px;
}

.form-tip-text {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    line-height: 1.4;
    padding-left: 4px;
    position: relative;
}

.form-tip-text::before {
    content: '提示：';
    font-weight: 500;
    color: #f5222d;
}
</style>
