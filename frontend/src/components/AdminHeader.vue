<script setup lang="ts">
import { router } from "@/route";

const exitAdmin = () => {
  router.push("/app");
};
</script>

<template>
  <el-menu mode="horizontal" :ellipsis="false">
    <el-sub-menu>
      <template #title>我的</template>
      <el-menu-item @click="exitAdmin"> 退出管理员界面 </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>
