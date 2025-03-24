<template>
  <div class="w-screen h-screen flex items-center justify-center">
    <el-card>
      <div class="text-xl text-center">创建新账户</div>
      <el-form class="mt-4">
        <el-form-item>
          <label>用户名（必填）</label>
          <el-input id="username" type="text" v-model="username" required />
        </el-form-item>
        <el-form-item>
          <label>密码（必填）</label>
          <el-input
            show-password="true"
            id="password"
            type="password"
            v-model="password"
            required
          />
        </el-form-item>
        <el-form-item>
          <label>真实姓名（必填）</label>
          <el-input id="name" type="text" v-model="name" required />
        </el-form-item>
        <el-form-item>
          <label>头像</label>
          <el-input id="avatar" type="text" v-model="avatar" />
        </el-form-item>
        <el-form-item>
          <label>手机号</label>
          <el-input maxlength="11" id="telephone" type="text" v-model="telephone" />
        </el-form-item>
        <el-form-item>
          <label>邮箱</label>
          <el-input id="email" type="text" v-model="email" />
        </el-form-item>
        <el-form-item>
          <label>位置</label>
          <el-input id="location" type="text" v-model="location" />
        </el-form-item>
        <div class="flex justify-center">
          <el-button @click.prevent="handleRegister" :disabled="registerDisabled"> 创建 </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { userRegister } from "@/api/user.ts";
import { ElMessage } from "element-plus";
import { router } from "../../route/index";
const username = ref("");
const password = ref("");
const name = ref("");
const avatar = ref("");
const telephone = ref("");
const email = ref("");
const location = ref("");
const hasUsernameInput = computed(() => username.value != "");
const hasPasswordInput = computed(() => password.value != "");
const hasNameInput = computed(() => name.value != "");
const chinaMobileRegex = /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/;
const telLegal = computed(() => telephone.value == "" || chinaMobileRegex.test(telephone.value));
const emailRegex = /\w+@[A-Za-z]+(\.[A-Za-z0-9]+){1,2}/;
const emailLegal = computed(() => email.value == "" || emailRegex.test(email.value));
const registerDisabled = computed(() => {
  return !(
    hasUsernameInput.value &&
    hasPasswordInput.value &&
    hasNameInput.value &&
    telLegal.value &&
    emailLegal.value
  );
});
function handleRegister() {
  console.log(username.value);
  userRegister({
    username: username.value,
    password: password.value,
    name: name.value,
    avatar: avatar.value == "" ? null : avatar.value,
    telephone: telephone.value == "" ? null : telephone.value,
    email: email.value == "" ? null : email.value,
    location: location.value == "" ? null : location.value,
  }).then((res) => {
    if (res.data.code == "200") {
      ElMessage({
        message: "注册成功！",
        type: "success",
        center: true,
      });
      router.push("/login");
    } else {
      ElMessage({
        message: "用户名无效或冲突！",
        type: "error",
        center: true,
      });
      username.value = "";
    }
  });
}
</script>
