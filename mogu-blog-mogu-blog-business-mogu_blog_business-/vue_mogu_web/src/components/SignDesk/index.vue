<template>
  <div class="sidebarDiv guanzhu" id="follow-us" ref="follow">

    <el-row style="line-height: 40px;text-align: center">
      <el-col :span="4">
        <el-tooltip class="item" effect="light" content="通过发布文章、问答和评论可获取积分" placement="left-end">
          <i class="el-icon-coin"></i>
        </el-tooltip>
      </el-col>
      <el-col :span="8">
        <span style="font-weight: 600;">{{credits}}</span> 积分
        <el-tooltip class="item" effect="light" content="每隔10分钟自动刷新" placement="left-end">
          <i class="el-icon-refresh" @click="flushData" style="cursor: pointer;"></i>
        </el-tooltip>
      </el-col>
      <el-col :span="10">
        <el-button v-if="!isSignIn" type="primary" icon="el-icon-star-off" size="mini" round @click="clickSignIn">立即签到</el-button>
        <el-button v-else type="primary" icon="el-icon-star-off" disabled size="mini" round>今日已签到</el-button>
      </el-col>
      <el-col :span="2">
        <span v-if="!show" @click="showMore" style="font-size: 14px">展开</span>
        <span v-else @click="showMore" style="font-size: 14px; color: #00A7EB">收回</span>
      </el-col>
    </el-row>
    <el-row v-if="isSignIn" style="margin-top: 10px; text-align: center; background-color: #ecf8ff; line-height: 30px;">
      <div> <i class="el-icon-ice-tea"></i> 你已经连续签到<span style="color: dodgerblue; font-weight: 600;"> {{ continuousDays }} </span>天啦！</div>
    </el-row>

    <el-row v-if="show">
      <SignCalendar></SignCalendar>
    </el-row>

  </div>
</template>

<script>
import {userSignIn, getCurrentUserCredits, getSignDataByUserUid} from "../../api/about";
import SignCalendar from "@/components/SignCalendar";
import {mapMutations} from "vuex";
export default {
  name: "SignDesk",
  props: {

  },
  components: {
    SignCalendar
  },
  data() {
    return {
      continuousDays: 0, // 连续签到天数
      signInObject: {},
      isSignIn: false, // 是否完成签到
      credits: 0, // 总积分
      show: false,
    };
  },
  created() {
    this.getUserCredits(true);
    this.getSignDate(true)
  },
  mounted() {
    let userInfo = this.$store.state.user.userInfo
    if (userInfo) {
      let signInObject = userInfo.signIn
      if (signInObject) {
        this.isSignIn = true
        this.signInObject = signInObject
      } else {
        this.isSignIn = false
      }
    }
  },
  watch: {
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      let userInfo = this.$store.state.user.userInfo
      if (userInfo) {
        let signInObject = userInfo.signIn
        if (signInObject) {
          this.isSignIn = true
          this.getUserCredits(false)
        } else {
          this.isSignIn = false
        }
      }
    },
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.app.signInMessage': function (newFlag, oldFlag) {
      this.flushData()
    },
  },
  methods: {
    ...mapMutations(['setLoginMessage']),
    flushData() {
      this.getUserCredits(true);
      this.getSignDate(true);
    },
    showMore() {
      this.show = !this.show
    },
    getUserCredits (refresh) {
      let params = {}
      params.refresh = refresh
      getCurrentUserCredits(params).then(response => {
          if(response.code == this.$ECode.SUCCESS) {
            this.credits = response.data
          }
      })
    },
    getSignDate(refresh){
      let params = {}
      params.refresh = refresh
      getSignDataByUserUid(params).then(response => {
        console.log("获取用户签到", response)
        if(response.code == this.$ECode.SUCCESS) {
          this.isSignIn= response.data.todayIsSignIn;
          this.continuousDays= response.data.continuousDays;
        } else {
          this.$commonUtil.message.error(response.message)
        }
      })
    },
    clickSignIn() {
      let isLogin = this.$store.state.user.isLogin
      // 判断是否登录
      if (!isLogin) {
        this.isSignIn = false
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      console.log("点击签到")
      userSignIn().then(response => {
        console.log(response)
        if(response.code == this.$ECode.SUCCESS) {
          this.$commonUtil.message.success("签到成功")
          this.isSignIn = true;
          this.signInObject = response.data
          this.getUserCredits(true)
        } else {
          this.$commonUtil.message.error(response.message)
        }
      })
    }
  },
};
</script>

<style>
</style>
