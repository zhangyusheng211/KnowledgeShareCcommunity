<template>
  <div>
    <span v-if="componentShowMap.showUserPrise">
      <span v-if="!isPraise||praiseCount==0" @click="praise" class="pointer">
        <span class="iconfont">&#xec7f;</span>赞<span v-if="praiseCount>0">{{ praiseCount }}</span>
      </span>
      <span v-else @click="cancelUserPraise" class="pointer" style="color:#409EFF">
        <span class="iconfont">&#xec8c;</span>赞{{ praiseCount }}
      </span>
    </span>

    <span style="margin-left: 10px" v-if="componentShowMap.showUserTread">
      <span v-if="!isTread||treadCount==0" @click="tread" class="pointer">
        <span class="iconfont">&#xe690;</span>踩<span v-if="treadCount>0">{{ treadCount }}</span>
      </span>

      <span v-else @click="cancelUserTread" class="pointer" style="color: palevioletred">
        <span class="iconfont">&#xe68f;</span>踩{{ treadCount }}
      </span>
    </span>
  </div>
</template>

<script>
import {addPraise, addTread, cancelPraise, cancelTread, getPraiseCount} from "@/api/praise"
import {mapMutations} from "vuex";

export default {
  name: "Praise",
  props: ["bizUid", "resourceType", "isPraiseValue", "isTreadValue", "praiseCountValue", "treadCountValue"],
  data() {
    return {
      isPraise: false,
      isTread: false,
      praiseCount: 0,
      treadCount: 0,
      componentShowMap: {},
    }
  },
  watch: {
    bizUid: function (newFlag, oldFlag) {
      this.getUserPraiseCount()
    },
    '$store.state.app.webConfigData': function (event, oldFlag) {
      this.getComponentShowMap()
    },
  },
  mounted() {
    this.getComponentShowMap()
  },
  created() {
    this.getUserPraiseCount()
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage']),
    getComponentShowMap() {
      let webConfigData = this.$store.state.app.webConfigData
      this.componentShowMap = webConfigData.componentShowMap
    },
    getUserPraiseCount() {
      if (!this.bizUid) {
        return
      }

      // 有值直接返回
      if (this.praiseCountValue != undefined && this.treadCountValue != undefined) {
        this.isPraise = this.isPraiseValue
        this.isTread = this.isTreadValue
        this.praiseCount = this.praiseCountValue
        this.treadCount = this.treadCountValue
        return
      }
      console.log("获取到的点赞", this.praiseCountValue, this.treadCountValue, this.praiseCountValue != undefined && this.treadCountValue != undefined)

      let params = {}
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      getPraiseCount(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let praiseMap = response.data
          this.isPraise = praiseMap.isPraise
          this.isTread = praiseMap.isTread
          this.praiseCount = praiseMap.praiseCount
          this.treadCount = praiseMap.treadCount
        }
      })
    },
    praise() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      addPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = true
          this.praiseCount = this.praiseCount + 1
          // 点赞回调
          this.$emit("praiseCallback", this.bizUid)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelUserPraise() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      cancelPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = false
          this.praiseCount = this.praiseCount - 1
          // 取消点赞回调
          this.$emit("cancelPraiseCallback", this.bizUid)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    tread() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      addTread(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isTread = true
          this.treadCount = this.treadCount + 1
        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelUserTread() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      cancelTread(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.isTread = false
          this.$message.success(response.message)
          this.treadCount = this.treadCount - 1
        } else {
          this.$message.error(response.message)
        }
      })
    },
  }
}
</script>

<style scoped>
.pointer {
  font-size: 14px
}
</style>
