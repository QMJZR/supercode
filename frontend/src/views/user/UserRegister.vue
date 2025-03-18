<template>
  <div class="w-screen h-screen flex items-center justify-center">
    <el-card>
      <div class="text-xl text-center">创建新账户</div>
      <el-form class="mt-4">
        <el-form-item>
          <label>用户名</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>密码</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>真实姓名</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>头像</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>手机号</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>邮箱</label>
          <el-input />
        </el-form-item>
        <el-form-item>
          <label>位置</label>
          <el-input />
        </el-form-item>
        <div class="flex justify-center">
          <el-button> 创建 </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts"></script>
