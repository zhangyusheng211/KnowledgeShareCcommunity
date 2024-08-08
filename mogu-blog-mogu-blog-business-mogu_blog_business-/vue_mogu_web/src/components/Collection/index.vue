<template>
  <div v-if="componentShowMap.showUserCollect">
    <span v-if="!isCollect || collectCount == 0" @click="collect" class="pointer">
      <span class="iconfont">&#xe666;</span>收藏<span v-if="collectCount > 0">{{ collectCount }}</span>
    </span>

    <span v-else @click="cancelCollect" class="pointer" style="color:orange">
      <span class="iconfont">&#xe8c2;</span>收藏<span v-if="collectCount>0">{{collectCount}}</span>
    </span>
  </div>
</template>

<script>
import {getCollectCount, addCollect} from "@/api/collect"
import {mapMutations} from "vuex";
import {deleteCollect} from "../../api/collect";
export default {
  name: "Collection",
  props: ["bizUid", "collectType", "collectCountValue", "isCollectValue",],
  data() {
    return {
      collectCount: 0,
      isCollect: false,
      componentShowMap: {},
    }
  },
  watch: {
    bizUid: function (newFlag, oldFlag) {
      this.checkCollectStatus()
    },
    '$store.state.app.webConfigData': function (event, oldFlag) {
      this.getComponentShowMap()
    },
  },
  mounted() {
    this.getComponentShowMap()
  },
  created() {
    this.checkCollectStatus()
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage']),
    getComponentShowMap() {
      let webConfigData = this.$store.state.app.webConfigData
      this.componentShowMap = webConfigData.componentShowMap
    },
    checkCollectStatus() {
      if (!this.bizUid) {
        return
      }
      // 有值直接返回
      if (this.isCollectValue != undefined && this.collectCountValue != undefined) {
        this.isCollect = this.isCollectValue
        this.collectCount = this.collectCountValue
        return
      }

      let params = {}
      params.bizUid = this.bizUid
      params.collectType = this.collectType
      getCollectCount(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let collectData = response.data
          this.isCollect = collectData.isCollect
          this.collectCount = collectData.collectCount
        } else {
          this.isCollect = false
        }
      })
    },
    collect() {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
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
      params.bizUid = this.bizUid
      params.collectType = this.collectType
      addCollect(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isCollect = true
          this.collectCount = this.collectCount + 1
        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelCollect() {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
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
      params.bizUid = this.bizUid
      params.collectType = this.collectType
      deleteCollect(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isCollect = false
          this.collectCount = this.collectCount - 1
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
