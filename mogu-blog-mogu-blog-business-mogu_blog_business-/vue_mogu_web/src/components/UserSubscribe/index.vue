<template>
  <div>
    <el-tooltip effect="light" :content=" isUserSubscribe ? '取消订阅后，将无法收到专栏文章更新的站内信':'订阅专栏后，将收到专栏文章更新的站内信'" placement="top" >
      <el-button v-if="!isUserSubscribe" type="success" size="small" @click="addUserSubscribeMethod">订 阅</el-button>
      <el-button v-else size="small" type="danger"  @click="deleteUserSubscribeMethod">取消订阅</el-button>
    </el-tooltip>

  </div>
</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData";
import {addUserSubscribe, deleteUserSubscribe, checkUserSubscribe} from "@/api/userSubscribe";
import {mapMutations} from "vuex";
export default {
  name: "index",
  props: ['resourceUid', "resourceType"],
  data() {
    return {
      // 是否订阅
      isUserSubscribe: false,
    }
  },
  watch: {
    // 判断是否登录
    '$store.state.user.isLogin': function (newFlag, oldFlag) {
      this.checkUserSubscribeMethod()
    },
  },
  created() {
    this.checkUserSubscribeMethod()
  },
  mounted() {

  },
  components: {

  },
  methods: {
    ...mapMutations(['setLoginMessage']),
    addUserSubscribeMethod() {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以订阅',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      addUserSubscribe(params).then(resp => {
        if (resp.code === this.$ECode.SUCCESS) {
          this.$message.success(resp.message)
          this.isUserSubscribe = true
        } else {
          this.$message.error(resp.message)
          this.checkUserSubscribeMethod()
        }
      })
    },
    checkUserSubscribeMethod() {
      console.log("checkUserSubscribeMethod校验中", this.resourceUid, this.resourceType)
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
        console.log("没有登录")
        return
      }
      console.log("登录了")
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      checkUserSubscribe(params).then(resp => {
        if (resp.code === this.$ECode.SUCCESS) {
          console.log("获取到响应数据", resp)
          this.isUserSubscribe = resp.data
        }
      })
    },
    deleteUserSubscribeMethod() {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以取消订阅',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      deleteUserSubscribe(params).then(resp => {
        if (resp.code === this.$ECode.SUCCESS) {
          this.$message.success(resp.message)
          this.isUserSubscribe = false
        } else {
          this.$message.error(resp.message)
          this.checkUserSubscribeMethod()
        }
      })
    },
  },
}
</script>

<style scoped>

</style>
