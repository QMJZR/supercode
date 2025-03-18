<script setup lang="ts"></script>

<template>
  <div class="w-screen h-screen flex items-center justify-center">
    <el-card>
      <div class="text-xl text-center">欢迎回来</div>
      <el-form class="mt-4">
        <el-form-item >
          <label>用户名</label>
          <el-input placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <label>密码</label>
          <el-input placeholder="请输入密码" />
        </el-form-item>
        <div class="flex justify-around">
          <el-button>登入</el-button>
          <router-link to="/register">
            <el-button>去注册</el-button>
          </router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>
