<template>
  <el-menu router class="portal-menu" :default-active="$route.path">
    <el-sub-menu index="res">
      <template #title>
        <el-icon><Menu /></el-icon>
        <span>课程资源分享</span>
      </template>
      <el-menu-item index="/portal/course-resource">
        <span>资源首页</span>
      </el-menu-item>
      <el-menu-item index="/portal/course-resource/my">
        <span>我上传的</span>
      </el-menu-item>
      <el-menu-item index="/portal/course-resource/top">
        <span>排行榜</span>
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="notice">
      <template #title>
        <el-icon><Bell /></el-icon>
        <span>通知公告</span>
      </template>
      <el-menu-item index="/portal/notice">
        <span>公告列表</span>
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="lib">
      <template #title>
        <el-icon><Collection /></el-icon>
        <span>数字图书馆</span>
      </template>
      <el-menu-item index="/portal/library/list">
        <span>全部图书</span>
      </el-menu-item>
      <el-menu-item index="/portal/library/top">
        <span>下载/贡献榜</span>
      </el-menu-item>
      <el-menu-item index="/portal/library/upload">
        <span>上传图书</span>
      </el-menu-item>
      <el-menu-item index="/portal/library/contributions">
        <span>我的上传</span>
      </el-menu-item>
      <el-menu-item index="/portal/library/fav">
        <span>我的收藏</span>
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="facility">
      <template #title>
        <el-icon><OfficeBuilding /></el-icon>
        <span>功能房预约</span>
      </template>
      <el-menu-item index="/portal/facility">
        <span>功能房列表</span>
      </el-menu-item>
      <el-menu-item index="/portal/facility/my">
        <span>我的预约</span>
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { Menu, Collection, Bell, OfficeBuilding } from '@element-plus/icons-vue'
</script>

<style scoped>
.portal-menu { border-right: none; }
</style>
