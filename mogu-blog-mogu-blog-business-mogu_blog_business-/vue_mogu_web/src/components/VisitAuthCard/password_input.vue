<template>
  <!-- 加载校验框 -->
  <el-dialog
    title="输入访问密码"
    :visible.sync="dialogVisible"
    :width="dialogSize"
  >

    <div style="text-align: center">
      <div style="margin-top: 10px">
        <el-input placeholder="请输入访问密码" style="width: 200px" type="password" v-model="validCode"
                  @keyup.enter.native="submitCode"></el-input>
      </div>
      <div style="margin-top: 10px">
        <el-button type="primary" style=" width: 200px" @click="submitCode">提交</el-button>
      </div>
    </div>

  </el-dialog>
</template>

<script>
import {checkResourceVisitAuth} from "@/api/common";
import {getCookie, setCookie} from "@/utils/cookieUtils";
export default {
  name: "password_input",
  props: ['entity', "resourceType"],
  data() {
    return {
      validCode: "",
      dialogSize: "35%",
      dialogVisible: true,
    }
  },
  watch: {

  },
  created() {
    this.resizeWin()
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
      if (validCode.length === 0) {
        this.$message.error("密码长度不能为空")
        return
      }

      // 校验一下密码是否正确

      let resourceType = this.resourceType
      let entity = this.entity

      // 如果是在文章详情页进行访问控制
      // 判断文章是否携带了专栏，如果归属某个专栏，那么就需要校验对应专栏的访问权限
      if (resourceType === "BLOG" && entity.subject) {
        resourceType = "SUBJECT"
        entity = entity.subject
      }

      let params = {}
      params.password = validCode
      params.resourceType = resourceType
      params.resourceUid = entity.uid
      params.visitAuth = 10
      checkResourceVisitAuth(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          let checkSuccess = response.data
          if (checkSuccess) {
            this.$message.success("校验成功")
            this.$emit("passwordCallback", 1)
          } else {
            this.$message.error("校验失败")
          }
        } else {
          this.$message.error(response.message)
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
