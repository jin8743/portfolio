<script setup lang="ts">

import {ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const username = ref("");
const password = ref("");

const router = useRouter();

const login = function () {
  axios
      .post("/api/login", {
        username: username.value,
        password: password.value,
      }).then(resonse => {
    const accessToken = resonse.headers.authorization;
    axios.defaults.headers.common['Authorization'] = accessToken;
  })
};

const moveToRegister = function () {
  router.push("/join");
}
</script>

<template>
  <div class="container">
    <h2 class="mb-3">로그인</h2>
    <div class="input">
      <label for="username">아이디</label>
      <input
          v-model="username"
          type="text"
      />
    </div>
    <div class="input">
      <label for="password">비밀번호</label>
      <input
          v-model="password"
          type="password"
      />
    </div>
    <div class="alternative-option mt-4">
      계정이 없으신가요? <span @click="moveToRegister">회원가입</span>
    </div>

    <button type="submit" @click="login()" class="mt-4 btn-pers">
      로그인
    </button>
  </div>
</template>

<style>

</style>