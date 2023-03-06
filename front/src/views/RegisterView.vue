<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const username = ref("");
const password = ref("");
const passwordConfirm = ref("");

const router = useRouter();

const register = function () {
  axios
      .post("/api/join", {
        username: username.value,
        password: password.value,
        passwordConfirm: passwordConfirm.value,
      })
      .then(() => {
        router.replace({ name: "home" });
      });
};

const moveToLogin = function () {
  router.push("/login")
};

</script>

<template>
  <div class="container">
      <h2 class="mb-3">회원가입</h2>
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
      <div class="input">
        <label for="password-confirm">비밀번호 재확인</label>
        <input
            v-model="passwordConfirm"
            type="password"
        />
      </div>

      <div class="alternative-option mt-4">
        이미 계정이 있으신가요? <span @click="moveToLogin">로그인</span>
      </div>

      <button type="submit" @click="register()" class="mt-4 btn-pers">
        가입하기
      </button>
  </div>
</template>