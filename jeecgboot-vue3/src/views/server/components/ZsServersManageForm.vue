<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol"
          name="ZsServersManageForm">
          <a-row>
            <a-divider orientation="left" style="color: cadetblue; background-color: aliceblue;">连接服务器</a-divider>
            <a-col :span="12">
              <a-form-item label="服务器名称" v-bind="validateInfos.name" id="ZsServersManageForm-name" name="name">
                <a-input v-model:value="formData.name" placeholder="请输入服务器名称" ></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="用户名" v-bind="validateInfos.username" id="ZsServersManageForm-username"
                name="username">
                <a-input v-model:value="formData.username" placeholder="请输入用户名" :disabled="isKeyFieldDisabled"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="服务器地址" v-bind="validateInfos.host" id="ZsServersManageForm-host" name="host">
                <a-input v-model:value="formData.host" placeholder="请输入服务器地址" :disabled="isKeyFieldDisabled"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="passwordShow">
              <a-form-item label="密码" v-bind="validateInfos.password" id="ZsServersManageForm-password" name="password">
                <a-input-password v-model:value="formData.password" placeholder="请输入密码" :disabled="isKeyFieldDisabled"/>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="SSH端口" v-bind="validateInfos.port" id="ZsServersManageForm-port" name="port">
                <a-input-number v-model:value="formData.port" placeholder="请输入SSH端口" style="width: 100%" :disabled="isKeyFieldDisabled"/>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-button type="primary" @click="testConnect" preIcon="ant-design:api-outlined" style="margin-left: 21%;"
                v-show="showTestButton">测试连接</a-button>
              <a-button type="success" @click="saveConnect" preIcon="ant-design:cloud-download-outlined" style="margin-left: 20px;"
                v-show="isConnected">保存连接</a-button>
            </a-col>
            <a-divider orientation="left" style="color: cadetblue; background-color: aliceblue;">服务器信息</a-divider>
            <a-col :span="12">
              <a-form-item label="公网IP地址" v-bind="validateInfos.publicIp" id="ZsServersManageForm-publicIp"
                name="publicIp">
                <a-input v-model:value="formData.publicIp" placeholder="请输入公网IP地址"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="内网IP地址" v-bind="validateInfos.privateIp" id="ZsServersManageForm-privateIp"
                name="privateIp">
                <a-input v-model:value="formData.privateIp" placeholder="请输入内网IP地址"></a-input>
              </a-form-item>
            </a-col>
            <!-- <a-col :span="12">
              <a-form-item label="私钥内容" v-bind="validateInfos.privateKey" id="ZsServersManageForm-privateKey"
                name="privateKey">
                <a-input-password v-model:value="formData.privateKey" placeholder="请输入私钥内容"/>
              </a-form-item>
            </a-col> -->
            <!-- <a-col :span="12">
              <a-form-item label="私钥密码" v-bind="validateInfos.passphrase" id="ZsServersManageForm-passphrase"
                name="passphrase">
                <a-input-password v-model:value="formData.passphrase" placeholder="请输入私钥密码" allow-clear />
              </a-form-item>
            </a-col> -->
            <a-col :span="12">
              <a-form-item label="服务器型号" v-bind="validateInfos.serverModel" id="ZsServersManageForm-serverModel"
                name="serverModel">
                <a-input v-model:value="formData.serverModel" placeholder="请输入服务器型号"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="CPU型号" v-bind="validateInfos.cpuModel" id="ZsServersManageForm-cpuModel"
                name="cpuModel">
                <a-input v-model:value="formData.cpuModel" placeholder="请输入CPU型号"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="CPU核心数" v-bind="validateInfos.cpuCores" id="ZsServersManageForm-cpuCores"
                name="cpuCores">
                <a-input-number v-model:value="formData.cpuCores" placeholder="请输入CPU核心数" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="内存大小（GB）" v-bind="validateInfos.memoryGb" id="ZsServersManageForm-memoryGb"
                name="memoryGb">
                <a-input-number v-model:value="formData.memoryGb" placeholder="请输入内存大小（GB）" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="磁盘大小（GB）" v-bind="validateInfos.diskGb" id="ZsServersManageForm-diskGb" name="diskGb">
                <a-input-number v-model:value="formData.diskGb" placeholder="请输入磁盘大小（GB）" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="操作系统" v-bind="validateInfos.os" id="ZsServersManageForm-os" name="os">
                <a-input v-model:value="formData.os" placeholder="请输入操作系统"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="服务器状态" v-bind="validateInfos.status" id="ZsServersManageForm-status" name="status">
                <a-input-number v-model:value="formData.status" placeholder="请输入服务器状态" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="服务器描述" v-bind="validateInfos.description" id="ZsServersManageForm-description"
                name="description" style="width: 100%">
                <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入服务器描述" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="创建人" v-bind="validateInfos.createBy" id="ZsServersManageForm-createBy"
                name="createBy">
                <a-input v-model:value="formData.createBy" placeholder="请输入创建人" disabled></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="创建日期" v-bind="validateInfos.createTime" id="ZsServersManageForm-createTime"
                name="createTime">
                <a-date-picker placeholder="请选择创建日期" v-model:value="formData.createTime" showTime
                  value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="更新人" v-bind="validateInfos.updateBy" id="ZsServersManageForm-updateBy"
                name="updateBy">
                <a-input v-model:value="formData.updateBy" placeholder="请输入更新人" disabled></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="更新日期" v-bind="validateInfos.updateTime" id="ZsServersManageForm-updateTime"
                name="updateTime">
                <a-date-picker placeholder="请选择更新日期" v-model:value="formData.updateTime" showTime
                  value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { getValueType } from '/@/utils';
import { saveOrUpdate, checkServerConnect, getServerInfo } from '../ZsServersManage.api';
import { Form } from 'ant-design-vue';
import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
import { duplicateValidate } from '/@/utils/helper/validator'

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
  name: '',
  host: '',
  publicIp: '',
  privateIp: '',
  port: undefined,
  username: '',
  password: '',
  privateKey: '',
  passphrase: '',
  serverModel: '',
  cpuModel: '',
  cpuCores: undefined,
  memoryGb: undefined,
  diskGb: undefined,
  os: '',
  status: undefined,
  description: '',
  createBy: '',
  createTime: '',
  updateBy: '',
  updateTime: '',
});
const { createMessage } = useMessage();
const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
const confirmLoading = ref<boolean>(false);
//表单验证
const validatorRules = reactive({
  name: [{ required: true, message: '请输入服务器名称!' }, { validator: nameDuplicatevalidate }],
  host: [{ required: true, message: '请输入服务器地址!' }, { validator: hostDuplicatevalidate }, {validator: hostFormatValidate}],
  username: [{ required: true, message: '请输入用户名!' }],
  password: [{ required: true, message: '请输入密码!' }],
  port: [{ required: true, message: '请输入端口号!' }],
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

const showTestButton = computed(() => {
  if (!!formData.id) {
    return false;
  } else {
    return formData.host && formData.username && formData.password && formData.port;
  }
});
const isConnected = ref(false);
const testLoading = ref(false);
const infoLoading = ref(false);

// 判断是否为编辑模式
const isEditMode = computed(() => {
  return !!formData.id;
});

// 计算关键字段是否禁用
const isKeyFieldDisabled = computed(() => {
  return isEditMode.value || disabled.value;
});

const passwordShow = computed(() => {
  if (formData.id) {
    return false;
  } else {
    return true;
  }
})


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
    //重置连接状态
    isConnected.value = false;
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
  return duplicateValidate('zs_servers_manage', 'name', value, formData.id || '')
}
async function hostDuplicatevalidate(_r, value) {
  return duplicateValidate('zs_servers_manage', 'host', value, formData.id || '')
}
function hostFormatValidate(_r, value) {
  if (value) {
    if (!/^((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}$/.test(value)) {
      return Promise.reject('请输入正确的ip地址');
    }
  }
  return Promise.resolve();
}

async function testConnect() {
  if (testLoading.value) return;
  testLoading.value = true;
  const param = {
    host: formData.host,
    username: formData.username,
    password: formData.password,
    port: formData.port
  }

  try {
    const res = await checkServerConnect(param);
    if (res.success) {
      if (res.message == 'true') {
        createMessage.success("连接成功");
        isConnected.value = true;
      } else {
        createMessage.error("连接失败");
        isConnected.value = false;
      }
    } else {
      createMessage.warning(res.message);
      isConnected.value = false;
    }
  } catch (error) {
    createMessage.error("连接请求失败");
    isConnected.value = false;
  } finally {
    testLoading.value = false;
  }
}

function saveConnect() {
  submitForm();
}

async function serverInfo() {
  if (infoLoading.value) return;
  infoLoading.value = true;
  const param = {
    host: formData.host,
    username: formData.username,
    password: formData.password,
  }
  try {
    createMessage.info("正在获取服务器信息...");
    // TODO: 实现获取服务器信息的逻辑
    const res = await getServerInfo(param);
    if (res.success) {
      console.log(res);
      createMessage.success("获取服务器信息成功");
      Object.assign(formData, res.data);
    } else {
      createMessage.error("获取服务器信息失败");
    }
  } finally {
    infoLoading.value = false;
  }
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
</style>