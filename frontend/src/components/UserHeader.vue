<script setup lang="ts">
import { router } from "@/route";

const enterAdmin = () => {
  router.push("/admin");
};
</script>

<template>
  <el-menu mode="horizontal" :ellipsis="false">
    <el-menu-item>学习</el-menu-item>
    <el-menu-item>题库</el-menu-item>
    <el-sub-menu>
      <template #title>我的</template>
      <el-menu-item @click="enterAdmin"> 管理员界面 </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>
