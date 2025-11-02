<template>
  <el-menu router class="portal-menu" :default-active="$route.path">
    <el-sub-menu index="res">
      <template #title>
        <el-icon><Menu /></el-icon>
        <span>课程资源分享</span>
      </template>
      <el-menu-item index="/p">
        <span>资源首页</span>
      </el-menu-item>
      <el-menu-item index="/p/my">
        <span>我上传的</span>
      </el-menu-item>
      <el-menu-item index="/p/top">
        <span>排行榜</span>
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="lib">
      <template #title>
        <el-icon><Collection /></el-icon>
        <span>数字图书馆</span>
      </template>
      <el-menu-item disabled>即将上线</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { Menu, Collection } from '@element-plus/icons-vue'
</script>

<style scoped>
.portal-menu { border-right: none; }
</style>
