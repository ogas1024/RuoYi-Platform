<template>
  <div class="app-container">
    <el-card>
      <el-form :model="form" label-width="160px" style="max-width:560px">
        <el-form-item label="是否需要审核">
          <el-switch v-model="audit"/>
        </el-form-item>
        <el-form-item label="最长时长（分钟）">
          <el-input-number v-model="form.maxDurationMinutes" :min="30" :step="30"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" v-hasPermi="['manage:facility:setting:edit']">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {getSetting, saveSetting} from '@/api/manage/facility'
import {ElMessage} from 'element-plus'

const form = ref({maxDurationMinutes: 4320})
const audit = ref(true)

async function load() {
  const {data} = await getSetting();
  audit.value = !!data.auditRequired;
  form.value.maxDurationMinutes = data.maxDurationMinutes
}

async function save() {
  await saveSetting({auditRequired: audit.value, maxDurationMinutes: form.value.maxDurationMinutes});
  ElMessage.success('已保存')
}

onMounted(load)
</script>

