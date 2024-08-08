<template>
  <!-- 补签 -->
  <div class="repair-sign-dialog" style="margin-bottom: 10px; margin-top: 5px;">
    <signCalendar
      @handlePrev="handleChange"
      @handleNext="handleChange"
      :showOpenBtn="false"
      :signedDates="calendar.signedDates"
      :maxChoose="calendar.maxChoose"
      :chooseCallBack="chooseHandle"
      :chooseDates="chooseDates"
      :calendarList="this.calendarList"
    ></signCalendar>
    <div class="fbar">
      <span v-if="chooseDates.length > 0">{{chooseDatesText}}</span>
      <el-tooltip v-if="chooseDates.length > 0"  class="item" effect="dark" content="补签选中日期" placement="top">
        <el-button @click="repairSignHandle" type="warning" size="mini" icon="el-icon-star-off" round>补签</el-button>
      </el-tooltip>
      <span v-if="chooseDates.length > 0">可补签{{calendar.maxChoose}}次</span>

      <span v-if="chooseDates.length == 0">
        <el-tooltip class="item" effect="light"  placement="top">
          <div slot="content">通过完成签到、任务、发布文章、问答、评论、动态<br/>可获取积分，积分可用于参与抽奖和资源兑换</div>
          <i class="el-icon-coin"></i>
        </el-tooltip>
        <span style="font-weight: 600;color: #FFFFFF; cursor: pointer;" @click="showCreditsDetail"> {{credits}}</span> 积分

        <el-tooltip class="item" effect="light" content="每隔10分钟自动刷新" placement="left-end">
          <i class="el-icon-refresh pointer" @click="refresh()" ></i>
        </el-tooltip>

      </span>

      <span v-if="chooseDates.length == 0">
        <el-button v-if="!isSignIn" type="primary" size="mini" icon="el-icon-star-off" round @click="clickSignIn">立即签到</el-button>
      </span>

      <div  v-if="chooseDates.length == 0 && isSignIn">
        <i class="el-icon-ice-tea"></i>你已经连续签到<span style="color: #FFFFFF; font-weight: 600;"> {{ continuousDays }} </span>天啦！
      </div>

    </div>

  </div>
</template>

<script>
import signCalendar from "./signCalendar.vue";
import {userSignIn, getCurrentUserCredits, retroactive, getSignDataByUserUid} from "../../api/about";
import {mapMutations} from "vuex";
export default {
  name: "app",
  data() {
    return {
      calendar: {
        open: false,
        unClick: true,
        showTop: false,
        maxChoose: 0, //补签次数
        signedDates: []
      },
      repairSignDialog: true,
      chooseDatesText: "选择补签日期",
      chooseDates: [],
      continuousDays:0,
      signInObject: {},
      isSignIn: false, // 是否完成签到
      credits: 0, // 总积分
      dateStr: "",
      calendarList: [],
    };
  },
  mounted() {
  },
  watch: {
  },
  created() {
    this.getSignDate(false);
    this.getUserCredits(false);
  },
  methods: {
    ...mapMutations(['setLoginMessage', 'setSignInMessage', 'setDomainEventMessage']),
    showCreditsDetail() {
      // 上传成功后，打开我的文章
      // 发送个人中心领域事件
      let event = {
        "type": "personCenter",
        "action": "CreditsDetail",
        "time": new Date(),
      }
      this.setDomainEventMessage(event)
    },
    refresh() {
      this.getSignDate(true)
      this.getUserCredits(true)
    },
    getUserCredits (refresh) {
      if (refresh) {
        let isLogin = this.$store.state.user.isLogin
        // 判断是否登录
        if (!isLogin) {
          this.isSignIn = false
          // 未登录，自动弹出登录框
          this.setLoginMessage(Math.random())
          return;
        }
      }
      let params = {}
      params.refresh = refresh
      getCurrentUserCredits(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.credits = response.data
        }
      })
    },
    handleChange(dateStr) {
      this.dateStr = dateStr
      this.chooseDates = []
      this.getSignDate(true)
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
      userSignIn().then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.isSignIn = true;
          this.signInObject = response.data
          this.setSignInMessage(Math.random())
          this.getSignDate(true);
          this.getUserCredits(true)
          this.$commonUtil.message.success("签到成功")
        } else {
          this.$commonUtil.message.error(response.message)
        }
      })
    },
    //选择补签日期回调
    chooseHandle(data) {
      if (data.length > 0) {
        this.chooseDates = data;
        let str = "";
        data.forEach(v => {
          str = str + this.dateFormat(v, "d") + "、";
        });
        this.chooseDatesText = `选中${str.substring(0, str.length - 1)}号补签`;
      } else {
        this.chooseDatesText = "选择补签日期";
      }
    },
    //确认补签
    repairSignHandle() {
      if (this.chooseDates.length > 0 && this.chooseDates.length>this.calendar.maxChoose){
        this.$commonUtil.message.error("补签次数不足");
        return;
      }
      if (this.chooseDates.length > 0) {
        let params ={};
        params.retroactiveDate =this.chooseDates;
        retroactive(params).then(response => {
          if(response.code == this.$ECode.SUCCESS) {
            this.$commonUtil.message.success("补签成功")
            this.setSignInMessage(Math.random())
            this.getSignDate(true);
            this.getUserCredits(true);
          }
        })
      } else {
        alert("请先选择补签日期");
      }
    },
    dateFormat(d, fmt) {
      let D = new Date(d);
      let o = {
        "M+": D.getMonth() + 1, //月份
        "d+": D.getDate(), //日
        "h+": D.getHours(), //小时
        "m+": D.getMinutes(), //分
        "s+": D.getSeconds(), //秒
        "q+": Math.floor((D.getMonth() + 3) / 3), //季度
        S: D.getMilliseconds() //毫秒
      };
      if (/(y+)/.test(fmt))
        fmt = fmt.replace(
          RegExp.$1,
          (D.getFullYear() + "").substr(4 - RegExp.$1.length)
        );
      for (let k in o)
        if (new RegExp("(" + k + ")").test(fmt))
          fmt = fmt.replace(
            RegExp.$1,
            RegExp.$1.length == 1
              ? o[k]
              : ("00" + o[k]).substr(("" + o[k]).length)
          );
      return fmt;
    },
    getSignDate(refresh){
      this.chooseDates=[];
      this.chooseDatesText="选择补签日期";
      let params = {}
      params.refresh = refresh
      params.dateStr = this.dateStr
      getSignDataByUserUid(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.calendar.maxChoose = response.data.retroactiveCard;
          this.calendar.signedDates= response.data.signedDataList;
          this.isSignIn= response.data.todayIsSignIn;
          this.continuousDays= response.data.continuousDays;
          this.calendarList = response.data.calendarList;
        } else {
          this.$commonUtil.message.error(response.message)
        }
      })
    }
  },
  components: {
    signCalendar
  }
};
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
}
.txt-c {
  text-align: center;
}
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.btn {
  margin: 0 auto;
  padding: 0.1rem;
}
.repair-sign-dialog {
  border-radius: 5px;
  overflow: hidden;
  .fbar {
    border-top: 1px solid rgba(255, 255, 255, 0.3);
    background-color: #1c7ce5;
    justify-content: space-between;
    font-size: 0.26rem;
    color: #c6e1ff;
    height: 0.8rem;
    line-height: 0.8rem;
    padding: 0 0.34rem;
    display: flex;
    align-items: center;
  }
  .btn {
    margin: 5px;
  }
  .signCalendar {
    padding-bottom: 0 !important;
    .week {
      margin-bottom: 0 !important;
    }
  }
}
</style>
