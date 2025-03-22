<script setup lang="ts">
import {ref,computed} from 'vue'
import {userLogin} from "@/api/user.ts";
import {ElMessage} from "element-plus";
import {router} from "@/route";
const username=ref('')
const password=ref('')
const hasUsernameInput = computed(() => username.value != '')
const hasPasswordInput = computed(() => password.value != '')
function handleLogin() {
  userLogin({
    username:username.value,
    password:password.value,
  }).then(res => {
  if(res.data.code === "000"){
    ElMessage({
      message:"登录成功！",
      type:"success",
      center:true,
    })
    router.push("/app")
  }
  else{
    ElMessage({
      message:"用户名或密码错误！",
      type:"error",
      center:true,
    })
    password.value=""
  }
  })
}
const loginDisabled = computed(() => {
  return !(hasUsernameInput.value && hasPasswordInput.value)
})
</script>

<template>
  <div class="w-screen h-screen flex items-center justify-center">
    <el-card>
      <div class="text-xl text-center">欢迎回来</div>
      <el-form class="mt-4">
        <el-form-item >
          <label>用户名</label>
          <el-input id="username" type="text" v-model="username" required placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <label>密码</label>
          <el-input id="password" type="password" v-model="password" required placeholder="请输入密码" />
        </el-form-item>
        <div class="flex justify-around">
          <el-button @click.prevent="handleLogin" :disabled="loginDisabled">登入</el-button>
          <router-link to="/register">
            <el-button>去注册</el-button>
          </router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>
