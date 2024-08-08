<template>
    <div>

      <div class="button-item" v-if=" componentShowMap.showUserPrise">
        <el-tooltip content="点赞" placement="top-end" effect="light">
          <el-badge :value="praiseCount" :hidden="praiseCount == 0" class="item" :type="isPraise?'primary':'info'">
            <el-button circle @click="cancelUserPraise" type="primary" v-if="isPraise">
              <span class="iconfont">&#xe618;</span>
            </el-button>

            <el-button circle @click="praise" v-else>
              <span class="iconfont">&#xe615;</span>
            </el-button>
          </el-badge>
        </el-tooltip>
      </div>

      <div class="button-item" v-if="componentShowMap.showUserTread">
        <el-tooltip content="点踩" placement="top-end" effect="light">
          <el-badge :value="treadCount" :hidden="treadCount == 0" class="item" :type="isTread?'danger':'info'">
            <el-button circle @click="cancelUserTread"  type="danger" v-if="isTread">
              <span class="iconfont">&#xe619;</span>
            </el-button>

            <el-button circle @click="tread" v-else>
              <span class="iconfont">&#xe613;</span>
            </el-button>
          </el-badge>
        </el-tooltip>
      </div>

      <div class="button-item" v-if="componentShowMap.showUserCollect">
        <el-tooltip content="收藏" placement="top-end" effect="light">
          <el-badge :value="collectCount" :hidden="collectCount == 0"  class="item" :type="isCollect?'warning':'info'">
            <el-button circle @click="cancelCollect"  type="warning" v-if="isCollect">
              <span class="iconfont">&#xe614;</span>
            </el-button>

            <el-button circle @click="collect" v-else>
              <span class="iconfont">&#xe616;</span>
            </el-button>
          </el-badge>
        </el-tooltip>
      </div>

      <div class="button-item" v-if="showSubject">
        <el-tooltip content="专栏" placement="top-end" effect="light">
          <el-button circle @click="subject" :type="isSubject?'primary':''">
            <span class="iconfont">&#xe609;</span>
          </el-button>
        </el-tooltip>
      </div>

      <div class="button-item">
        <el-tooltip :content="showSteepRead?'开启沉浸阅读':'关闭沉浸阅读'" placement="top-end" effect="light">
          <el-button circle @click="openSteepRead" :type="!showSteepRead?'primary':''">
            <span class="iconfont">&#xe61d;</span>
          </el-button>
        </el-tooltip>
      </div>

      <div class="button-item" >
        <el-tooltip content="打赏" placement="top-end" effect="light">
          <el-button circle @click="openPayCallback">
            <span class="iconfont">&#xe67d;</span>
          </el-button>
        </el-tooltip>
      </div>

      <div class="button-item" v-if="componentShowMap.showUserComment">
        <el-tooltip content="评论" placement="top-end" effect="light">
          <el-button circle @click="comment">
            <span class="iconfont">&#xe61b;</span>
          </el-button>
        </el-tooltip>
      </div>


    </div>
</template>

<script>
import { addPraise, cancelPraise, addTread, cancelTread, getPraiseCount} from "@/api/praise"
import {getCollectCount, addCollect} from "@/api/collect"
import {mapMutations} from "vuex";
import {deleteCollect} from "../../api/collect";
export default {
  name: "LeftNavigation",
  props: ["bizUid", "resourceType", "isPraiseValue", "isTreadValue", "praiseCountValue", "treadCountValue", "collectCountValue", "isCollectValue", "isSubjectValue", "showSubjectValue", "showSteepRead"],
  data() {
    return {
      isPraise: false,
      isTread: false,
      isCollect: false,
      praiseCount: 0,
      treadCount: 0,
      collectCount: 0,
      isSubject: false, // 是否显示专题
      showSubject: false, // 是否选中专题
      random: 0,
      componentShowMap: {},
    }
  },
  watch: {
    bizUid: function (newFlag, oldFlag) {
      this.getUserPraiseCount()
      this.checkCollectStatus(false)
    },
    // 判断是否点赞成功
    '$store.state.app.praiseMessage': function (newFlag, oldFlag) {
      console.log("监听变化")
      if (this.random == newFlag) {
        return
      }
      this.getUserPraiseCount(true)
    },
    '$store.state.app.webConfigData': function (event, oldFlag) {
      this.getComponentShowMap()
    },
  },
  components: {

  },
  mounted() {
    this.getComponentShowMap()
  },
  created() {
    this.getUserPraiseCount(false)
    this.checkCollectStatus()
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage', 'setPraiseMessage', "setDomainEventMessage"]),
    getComponentShowMap() {
      let webConfigData = this.$store.state.app.webConfigData
      this.componentShowMap = webConfigData.componentShowMap
    },
    getUserPraiseCount(refresh) {
      if (!this.bizUid) {
        return
      }
      // 判断传递了专题
      if (this.isSubjectValue != undefined) {
         this.isSubject = this.isSubjectValue
      }
      if (this.showSubjectValue != undefined) {
        this.showSubject = this.showSubjectValue
      }
      // 有值直接返回
      if (this.praiseCountValue != undefined && this.treadCountValue != undefined && !refresh) {
        this.isPraise = this.isPraiseValue
        this.isTread = this.isTreadValue
        this.praiseCount = this.praiseCountValue
        this.treadCount = this.treadCountValue
        return
      }

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
    openSteepRead() {
      // 沉浸阅读
      this.subject()
      this.$emit("steepReadCallBack", "")
    },
    praise() {
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
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      addPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = true
          this.random = Math.random()
          this.setPraiseMessage(this.random)
          this.praiseCount = this.praiseCount + 1

          // 发送点赞的领域事件
          let event = {
            "type": "action",
            "action": this.$EAction.Praise,
            "resourceUid": this.bizUid,
            "resourceType": this.resourceType,
            "time": new Date(),
          }
          this.setDomainEventMessage(event)

        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelUserPraise() {
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
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      cancelPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = false
          this.random = Math.random()
          this.setPraiseMessage(this.random)
          this.praiseCount = this.praiseCount - 1
        } else {
          this.$message.error(response.message)
        }
      })
    },
    tread() {
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
      params.resourceUid = this.bizUid
      params.resourceType = this.resourceType
      addTread(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isTread = true
          this.treadCount = this.treadCount + 1

          // 发送点赞的领域事件
          let event = {
            "type": "action",
            "action": this.$EAction.Tread,
            "resourceUid": this.bizUid,
            "resourceType": this.resourceType,
            "time": new Date(),
          }
          this.setDomainEventMessage(event)

        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelUserTread() {
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
    // 检查是否收藏
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
      params.collectType = this.resourceType
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
    // 收藏
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
      params.collectType = this.resourceType
      addCollect(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isCollect = true
          this.collectCount = this.collectCount + 1

          // 发送收藏的领域事件
          let event = {
            "type": "action",
            "action": this.$EAction.Collect,
            "resourceUid": this.bizUid,
            "resourceType": this.resourceType,
            "time": new Date(),
          }
          this.setDomainEventMessage(event)

        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 取消收藏
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
      params.collectType = this.resourceType
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
    // 评论回调
    comment: function () {
      this.$emit("commentCallback", "")
    },
    // 举报
    report: function () {

    },
    subject: function () {
      console.log("点击专栏")
      this.isSubject = !this.isSubject
      this.$emit("subjectCallback", "")
    },
    openPayCallback: function () {
      this.$router.push("/sponsor")
      // this.$emit("openPayCallback", "")
    },
  }
}
</script>

<style scoped>
  .button-item {
    margin-bottom: 15px;

  }
  /deep/ .el-button {
    width: 48px;
    height: 48px;
  }

  .iconfont {
    font-size: 18px;
    margin-right: 0;
  }
</style>
