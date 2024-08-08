<template>
  <div style="margin-top: 150px">

    <el-empty :image="require('../assets/img/error.png')">
      <span style="font-size: 24px; color: #000000; font-weight: 500" slot="description">
        {{message}}
      </span>
      <el-button style="background: #F06B60; color: #ffffff;" round @click="goHome">返回首页</el-button>
    </el-empty>

  </div>
</template>

<script>
export default {
  name: "error",
  data() {
    return {
      message: "操作失败",
    }
  },
  created() {
    let message = this.$route.query.msg;
    if(message) {
      this.message = message
    }
  },
  methods: {
    goHome() {
      this.$router.push("/")
    }
  }
}
</script>

<style scoped>

</style>
