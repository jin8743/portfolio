<script setup lang="ts">

import {ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const boardName = ref("");

const router = useRouter();

const create = function () {
  axios
      .post("/api/admin/board", {
        boardName: boardName.value
      },
          {
            headers: {
              Authorization:  axios.defaults.headers.common['Authorization']
            },
          });
};

</script>

<template>
  <div class="container">
    <h2 class="mb-3">게시판 생성</h2>
    <div class="input">
      <label for="boardName">게시판 이름</label>
      <input
          v-model="boardName"
          type="text"
      />
    </div>

    <button type="submit" @click="create()" class="mt-4 btn-pers">
      생성
    </button>
  </div>
</template>

<style>

</style>