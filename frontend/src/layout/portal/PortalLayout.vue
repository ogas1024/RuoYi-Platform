<template>
  <el-container class="portal-layout">
    <el-header class="portal-header">
      <div class="left" @click="goHome">
        <div class="logo">学院生活平台</div>
      </div>
      <div class="crumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/portal/course-resource">课程资源</el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.name==='PortalCourse' || route.name==='PortalResourceList'">
            {{ route.query.majorName || '专业' }}
          </el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.name==='PortalResourceList'">{{
              route.query.courseName || '课程'
            }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="actions" v-if="showAdmin">
        <el-button type="primary" size="small" @click="goAdmin">管理后台</el-button>
      </div>
      <div class="right">
        <el-dropdown>
          <span class="el-dropdown-link">
            <el-avatar :size="28" :src="userStore.avatar"/>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goProfile">个人信息</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="portal-aside">
        <PortalSidebar/>
      </el-aside>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import useUserStore from '@/store/modules/user'
import PortalSidebar from './PortalSidebar.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function goHome() {
  router.push('/portal/course-resource')
}

function goProfile() {
  router.push({name: 'Profile'})
}

function goAdmin() {
  // 使用整页跳转，避免在门户路由上下文下的路由守卫/动态路由注入时机造成的 404
  location.href = '/index'
}

async function logout() {
  try {
    await userStore.logOut()
  } finally {
    location.href = '/index'
  }
}

const showAdmin = computed(() => {
  const roles = (userStore.roles || []).map(r => String(r).toLowerCase())
  return roles.includes('admin') || roles.includes('super_admin') || roles.includes('major_lead') || roles.includes('librarian') || roles.includes('staff')
})
</script>

<style scoped>
.portal-layout {
  height: 100vh;
}

.portal-header {
  display: flex;
  align-items: center;
  gap: 16px;
  height: 56px;
  border-bottom: 1px solid #f0f0f0;
}

.portal-header .left {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.portal-header .logo {
  font-weight: 700;
  font-size: 18px;
  color: #409EFF;
}

.portal-header .crumb {
  flex: 1;
  margin-left: 20px;
}

.portal-header .actions {
  white-space: nowrap;
}

.portal-header .right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.portal-aside {
  border-right: 1px solid #f0f0f0;
}
</style>
