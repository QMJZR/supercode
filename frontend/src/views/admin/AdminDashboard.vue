<script setup lang="ts">
import { userInfo, userInfoUpdate, type UpdateInfo } from "@/api/user";
import AdminHeader from "@/components/AdminHeader.vue";
import { router } from "@/route";
import { computed, onMounted, ref } from "vue";

const username = ref<string>("");
const name = ref<string>("");
const avatar = ref<null | string>("");
const telephone = ref<null | string>("");
const email = ref<null | string>("");
const location = ref<null | string>("");

onMounted(() => {
  const un = sessionStorage.getItem("username");
  if (un == null) {
    router.push("/login");
    return;
  }
  userInfo(un!).then((res) => {
    const user: UpdateInfo = res.data;
    username.value = user.username;
    name.value = user.name;
    avatar.value = user.avatar;
    telephone.value = user.telephone;
    email.value = user.email;
    location.value = user.location;
  });
});

const handleUpdate = () => {
  const userInfo: UpdateInfo = {
    username: username.value,
    name: name.value,
    avatar: avatar.value,
    telephone: telephone.value,
    email: email.value,
    location: location.value,
  };
  console.log(userInfo);
  userInfoUpdate(userInfo).then((res) => {
    console.log(res);
  });
};

const nameEmpty = computed(() => {
  return name.value === "";
});
</script>

<template>
  <AdminHeader></AdminHeader>
  <div class="w-screen h-screen flex items-center justify-center">
    <div class="flex flex-col gap-2">
      <div>
        <label>用户名</label>
        <el-input v-model="username" :disabled="true"></el-input>
      </div>
      <div>
        <label>真实姓名(必填)</label>
        <el-input v-model="name"></el-input>
      </div>
      <div>
        <label>头像</label>
        <el-input v-model="avatar"></el-input>
      </div>
      <div>
        <label>手机号</label>
        <el-input v-model="telephone"></el-input>
      </div>
      <div>
        <label>邮箱</label>
        <el-input v-model="email"></el-input>
      </div>
      <div>
        <label>地址</label>
        <el-input v-model="location"></el-input>
      </div>
      <el-button @click="handleUpdate" :disabled="nameEmpty">修改</el-button>
    </div>
  </div>
</template>
