<template>
  <!-- 加载校验框 -->
  <el-dialog
    title="加载校验"
    close-on-click-modal
    @close="beforeClose"
    :visible.sync="dialogVisible"
    :width="dialogSize"
  >

    <div style="text-align: center">
      <div v-html="webConfig.loadingValidText"></div>
      <div style="margin-top: 10px">
        <el-image style="width: 300px" :src="webConfig.loadingValidPhoto"></el-image>
      </div>
      <div style="margin-top: 10px">
        <el-input placeholder="请输入验证码(6位)" style="width: 200px" v-model="validCode"
                  @keyup.enter.native="submitCode"></el-input>
      </div>
      <div style="margin-top: 10px">
        <el-button type="primary" style=" width: 200px" @click="submitCode">提交</el-button>
      </div>
    </div>

  </el-dialog>
</template>

<script>
import {checkValidCode} from "@/api/user";
import {getCookie, setCookie} from "@/utils/cookieUtils";
import {done} from "nightwatch/lib/core/queue";
export default {
  name: "index",
  data() {
    return {
      validCode: "",
      webConfig: {},
      dialogSize: "35%",
      dialogVisible: true,
    }
  },
  watch: {
    // 判断配置
    '$store.state.app.webConfigData': function (newFlag, oldFlag) {
      this.webConfig = this.$store.state.app.webConfigData
    },
  },
  created() {
    this.resizeWin()
    this.webConfig = this.$store.state.app.webConfigData
  },
  mounted() {
    this.resizeWin()
  },
  methods: {
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.dialogSize = "35%"
      } else if (centerWidth > 1000) {
        this.dialogSize = "50%"
      } else if (centerWidth > 600) {
        this.dialogSize = "60%"
      } else {
        this.dialogSize = "95%"
      }
    },
    submitCode: function () {
      let validCode = this.validCode
      if (validCode.length != 6) {
        this.$message.error("验证码长度必须为6位")
        return
      }
      let params = new URLSearchParams()
      params.append("validCode", validCode)
      checkValidCode(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success("校验成功")
          setCookie("validCode", response.data, 0)
          this.$emit("loadingValidCallback", 1)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    beforeClose() {
      console.log("关闭前")
      this.$emit("loadingValidCallback", 0)
      done()
    },
  }
}
</script>

<style scoped>

</style>
