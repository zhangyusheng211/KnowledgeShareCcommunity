<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">赞助</a>
      </h1>

      <div class="reward-header">
        <div class="div-1190px">
          <img src="../assets/img/sponsor_logo2.png"
               class="reward-header-icon2">
          <h1><span>赞</span><span>助</span><span>排</span><span>行</span><span>榜</span></h1>
          <h3>感谢大家对社区的支持，每一笔赞助都将投放到网站的体验升级中。</h3>
          <h4>说明：为了保留赞助信息，赞助前请先完成登录，每赞助1元将获取10积分</h4>
          <div class="under-line"></div>
          <el-button class="reward-btn" v-if="hiddenSponsor" id="reward-btn" @click="showSponsor">
            网站很有帮助，赞助一下
          </el-button>
          <div class="reward-coded-div" v-if="!hiddenSponsor" id="reward-code-box">
            <div class="reward-code-img-div">
              <Qrcode v-if="qrcode" :text="qrcode" :size="150"/>
              <img v-else :src="webConfigData.logoPhoto" id="reward-code-img">

              <p style="line-height: 25px; font-size: 12px; color: gray; text-align: center;">微信扫码, 赞助<b
                id="reward-amount-count" style="color: #ffc36d">{{ price }}</b>元
              </p>
            </div>
            <div class="reward-code-amount-div">
              <button class="reward-amount-btn" v-for="(item, index) in sponsorList" :key="item"
                      :class="selectSponsor == index ?'selectSponsor':''" @click="cashSponsor(index, item)">{{ item }}元
              </button>
              <input class="reward-amount-input" placeholder="自定义" style="text-indent: 10px"
                     @click="selectSponsor = 7" v-model="priceInput" @blur="cashSponsor(7, priceInput)"
                     @keyup.enter="cashSponsor(7, priceInput)">
            </div>
          </div>
        </div>
      </div>

      <div class="home_top">
        <button class="reward-order-type-btn order-btn1"
                :style="type == 1? 'height: 70px;background: linear-gradient(to right, rgb(194, 21, 0), rgb(255, 197, 0))':''"
                @click="changeType(1)">最新榜
        </button>
        <button class="reward-order-type-btn order-btn1"
                :style="type == 2? 'height: 70px;background: linear-gradient(to right, rgb(194, 21, 0), rgb(255, 197, 0))':''"
                @click="changeType(2)">金额榜
        </button>
        <button class="reward-order-type-btn order-btn1"
                :style="type == 3? 'height: 70px;background: linear-gradient(to right, rgb(194, 21, 0), rgb(255, 197, 0))':''"
                @click="changeType(3)">累积榜
        </button>
      </div>

      <div class="reward-order-list-div">
        <div class="top-reward-module">
          <div class="top2-reward-module" v-if="sponsorOrderList.length >= 2">
            <img src="../assets/img/sponsor_top2.png"
                 class="reward-top-rank-img">
            <img :src="sponsorOrderList[1].user.photoUrl" class="reward-top-user-avatar img-hover"
                 id="reward-top2-user-avatar" @click="goUserCenter(sponsorOrderList[1].user)">
            <span class="reward-top-name"
                  id="reward-top3-amount-detail"> {{ sponsorOrderList[1].user.nickName }} </span>
            <span class="reward-top-amount-detail"
                  id="reward-top2-amount-detail"> {{ sponsorOrderList[1].price / 100 }}元 </span>
            <span class="reward-top-time" id="reward-top1-time">{{ formatDate(sponsorOrderList[1].createTime) }}</span>
          </div>

          <div class="top3-reward-module" v-if="sponsorOrderList.length >= 3">
            <img src="../assets/img/sponsor_top3.png"
                 class="reward-top-rank-img">
            <img :src="sponsorOrderList[2].user.photoUrl" @click="goUserCenter(sponsorOrderList[2].user)"
                 class="reward-top-user-avatar img-hover" data-user-id="CU_7f562e6d20f14572a79d38641bc0065b"
                 id="reward-top3-user-avatar">
            <span class="reward-top-name"
                  id="reward-top3-amount-detail"> {{ sponsorOrderList[2].user.nickName }} </span>
            <span class="reward-top-amount-detail"
                  id="reward-top3-amount-detail"> {{ sponsorOrderList[2].price / 100 }}元 </span>
            <span class="reward-top-time" id="reward-top1-time">{{ formatDate(sponsorOrderList[2].createTime) }}</span>
          </div>
          <div class="top1-reward-module" v-if="sponsorOrderList.length >= 1">
            <img src="../assets/img/sponsor_top1.png"
                 class="reward-top-rank-img">
            <img :src="sponsorOrderList[0].user.photoUrl" class="reward-top-user-avatar img-hover"
                 id="reward-top1-user-avatar" @click="goUserCenter(sponsorOrderList[0].user)">
            <span class="reward-top-name"
                  id="reward-top3-amount-detail"> {{ sponsorOrderList[0].user.nickName }} </span>
            <span class="reward-top-amount-detail"
                  id="reward-top1-amount-detail"> {{ sponsorOrderList[0].price / 100 }}元 </span>
            <span class="reward-top-time" id="reward-top1-time">{{ formatDate(sponsorOrderList[0].createTime) }}</span>
          </div>
        </div>


        <div id="reward-order-list-module" v-if="sponsorOrderList.length >= 4">
          <div class="reward-common-list-module" v-for="(item, index) in sponsorOrderList" :key="item.uid"
               v-if="index > 2">
            <button class="reward-rank">{{ index + 1 }}</button>
            <div class="reward-detail">
              <img :src="item.user.photoUrl" class="reward-user-avatar img-hover" @click="goUserCenter(item.user)">
              <span class="reward-amount-nickName">{{ item.user.nickName }}</span>
              <span class="reward-amount-detail">赞助了{{ item.price / 100 }}元</span>
              <span class="reward-time">{{ formatDate(item.createTime) }}</span>
            </div>
          </div>

        </div>
        <p id="load-more-btn" @click="loadMore" v-if="total > pageSize * (currentPage)">加载更多</p>
        <p id="load-more-end-btn" v-else style="margin-top: 80px; color: #d1d1d1">已经加载全部</p>
        <div style="clear: both"></div>
      </div>


      <div
        style="width: 100%; margin-top: 100px; height: auto; min-height: 100px; margin-bottom: 100px; text-align: center">
        <div class="div-1190px">
          <p style="margin-bottom: 30px;"><span class="company-reward-title-en">CORPORATE SPONSORS</span><span
            class="company-reward-title">企业赞助商</span></p>
          <el-row type="flex" class="row-bg" justify="center">
            <el-col :span="8">
              <a href="https://www.qiniu.com/" target="_blank">
                <img class="company-reward-logo active-box" title="七牛云"
                     src="https://picture.moguit.cn//blog/admin/jpg/2023/5/25/1685022898333.jpg">
              </a>
            </el-col>
            <el-col :span="8">
              <a href="https://www.upyun.com/?utm_source=lianmeng&utm_medium=referral" target="_blank">
                <img class="company-reward-logo active-box" title="又拍云"
                     src="https://picture.moguit.cn/blog/admin/png/2022/5/14/1652526898809.png">
              </a>
            </el-col>
          </el-row>
        </div>
      </div>

    </div>
  </div>
</template>
<script>
import {getSponsorOrderList, sponsor} from "../api/pay";
import Qrcode from 'vue-qrcode-component'
import {mapMutations} from "vuex";

export default {
  name: "sponsor",
  data() {
    return {
      price: null,
      hiddenSponsor: true,
      selectSponsor: 0,
      qrcode: null,
      sponsorList: [1, 2, 5, 10, 20, 50, 100],
      sponsorOrderList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      priceInput: null,
      webConfigData: {},
      orderByDescColumn: "createTime",
      type: 1,
    }
  },
  watch: {
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.app.webConfigData': function (newFlag, oldFlag) {
      this.webConfigData = this.$store.state.app.webConfigData
    },
    // 判断是否支付成功
    '$store.state.app.paySuccessMessage': function (newFlag, oldFlag) {
      console.log("支付成功")
      this.$message.success("支付成功")
      this.currentPage = 1
      this.sponsorOrderList = []
      this.getSponsorOrderListMethod()
    },
  },
  components: {
    Qrcode,
  },
  created() {
    this.sponsorOrderList = []
    this.getSponsorOrderListMethod()
    this.webConfigData = this.$store.state.app.webConfigData
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage']),
    changeType(type) {
      console.log("切换type", type)
      this.type = type
      if (type == 1) {
        this.orderByDescColumn = "createTime"
      } else if(type == 2) {
        this.orderByDescColumn = "price"
      } else if(type == 3) {
        this.orderByDescColumn = "sum"
      }
      this.currentPage = 1
      this.sponsorOrderList = []
      this.getSponsorOrderListMethod()
    },
    showSponsor() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        // 弹出登录框
        this.$notify.error({
          title: '警告',
          message: '赞助前请先完成登录~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return
      }
      this.hiddenSponsor = false
      this.cashSponsor(0, 1)
    },
    loadMore() {
      this.currentPage = this.currentPage + 1
      this.getSponsorOrderListMethod()
    },
    goUserCenter(user) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: user.uid}
      });
      window.open(routeData.href, '_blank');
    },
    getSponsorOrderListMethod() {
      let params = {}
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      params.orderByDescColumn = this.orderByDescColumn
      getSponsorOrderList(params).then(res => {
        if (res.code == this.$ECode.SUCCESS) {
          let sponsorOrderList = res.data.records
          this.total = res.data.total
          if (sponsorOrderList.length > 0) {
            this.sponsorOrderList = this.sponsorOrderList.concat(sponsorOrderList)
          }
        }
        console.log("获取赞助订单列表", res)
      })
    },
    formatDate(dateString) {
      let datePattern = /^(\d{4})-(\d{2})-(\d{2})/;
      let matches = datePattern.exec(dateString);
      if (matches) {
        return matches[1] + "-" + matches[2] + "-" + matches[3];
      }
      return "";
    },
    cashSponsor(index, price) {
      if (!price) {
        return
      }
      if (price <= 0) {
        this.$message.error("输入的金额有误")
        return
      }
      let reg = /^[0-9]+.?[0-9]*$/; //判断字符串是否为数字 ，判断正整数用/^[1-9]+[0-9]*]*$/
      if (!reg.test(price)) {
        this.$message.error("请输入正确的数字")
        return
      }
      this.selectSponsor = index
      let params = {}
      this.price = price
      params.price = price * 100
      sponsor(params).then(res => {
        if (res.code == this.$ECode.SUCCESS) {
          this.$message.success("支付二维码生成成功")
          this.qrcode = res.data
        } else {
          this.$message.error(res.message)
        }
        console.log("获取赞助码", res)
      })
    }
  }
}
</script>

<style scoped>
.reward-header {
  position: relative;
  width: 100%;
  height: 410px;
  padding-top: 50px;
  background-color: #1c7ce5;
}

.home_top {
  position: relative;
  width: 100%;
  height: 50px;
  text-align: center;
  line-height: 60px;
}

.home_top:after {
  position: relative;
  width: 180%;
  height: 200px;
  position: absolute;
  left: -20%;
  top: 0;
  z-index: -1;
  content: '';
  border-radius: 0 0 0 180%;
  background-color: #1c7ce5;
}

.reward-header-icon1 {
  position: absolute;
  left: 0px;
  top: 50px;
  height: 400px;
}

.reward-header-icon2 {
  position: absolute;
  right: -30px;
  bottom: 0px;
  height: 370px;
}

.reward-header h1 {
  position: absolute;
  left: 70px;
  top: 80px;
  font-size: 55px;
  font-weight: 700;
  letter-spacing: 6px;
}

.reward-header h3 {
  position: absolute;
  left: 70px;
  top: 170px;
  font-size: 17px;
  letter-spacing: 3px;
  color: white;
  font-weight: 500;
}

.reward-header h4 {
  position: absolute;
  left: 70px;
  top: 210px;
  font-size: 13px;
  letter-spacing: 1px;
  color: #FFFFFF;
  font-weight: 500;
}

.reward-header h1 span {
  background-image: -webkit-gradient(linear, left 0, right 0, from(white), to(#FFFFFF));
  -webkit-background-clip: text; /*必需加前缀 -webkit- 才支持这个text值 */
  -webkit-text-fill-color: transparent; /*text-fill-color会覆盖color所定义的字体颜色： */
}

.under-line {
  position: absolute;
  left: 70px;
  top: 235px;
  width: 150px;
  height: 1px;
  border-bottom: 2px solid #FFFFFF;
}

.reward-btn {
  position: absolute;
  left: 70px;
  top: 300px;
  padding: 10px 30px;
  color: #1c7ce5;
  background-color: white;
  border: 0px;
  border-radius: 50px;
}

.reward-coded-div {
  position: absolute;
  left: 70px;
  top: 250px;
  width: 400px;
  height: 200px;
  background-color: transparent;
}

.reward-code-img-div {
  position: absolute;
  left: 0px;
  top: 0px;
  width: 150px;
  height: 170px;
  padding: 10px;
  background-color: white;
}

#reward-code-img {
  width: 150px;
  height: 150px;
}

.reward-code-amount-div {
  position: absolute;
  left: 170px;
  top: 0px;
  width: 200px;
  height: 150px;
}

.reward-amount-btn, .reward-amount-input {
  cursor: pointer;
  position: relative;
  float: left;
  margin-left: 15px;
  width: 80px;
  height: 35px;
  background-color: white;
  border: 0px;
  border-radius: 3px;
  color: #1c7ce5;
  margin-bottom: 15px;
}

.reward-amount-btn:hover, .reward-amount-input:hover {
  background-color: #ffc36d;
  color: white;
}

.reward-order-list-div {
  position: relative;
  width: 1030px;
  padding: 50px;
  min-height: 500px;
  height: auto;
  background-color: white;
  box-shadow: 0px 5px 5px #cecece;
  margin-top: 0px;
  border-radius: 10px;
  margin-bottom: 50px;
  margin: 0 auto;
}

.reward-order-type-btn {
  cursor: pointer;
  position: relative;
  height: 50px;
  border: 0px;
  width: 150px;
  border-radius: 10px 10px 0px 0px;
  margin: -80px 20px 0px 20px;
  color: white;
  font-size: 17px;
  font-weight: 700;
}

.order-btn1 {
  background: #525252; /* fallback for old browsers */
  background: -webkit-linear-gradient(to right, rgb(82, 82, 82), rgb(61, 114, 180)); /* Chrome 10-25, Safari 5.1-6 */
  background: linear-gradient(to right, rgb(82, 82, 82), rgb(61, 114, 180)); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
  box-shadow: 0px -2px 5px #3734ff;
  height: 50px;
}

.order-btn2 {
  background: #c21500; /* fallback for old browsers */
  background: -webkit-linear-gradient(to right, rgb(194, 21, 0), rgb(255, 197, 0)); /* Chrome 10-25, Safari 5.1-6 */
  background: linear-gradient(to right, rgb(194, 21, 0), rgb(255, 197, 0)); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}

.company-reward-title-en {
  position: relative;
  font-size: 20px;
  color: #dbdbdb;
}

.company-reward-title {
  position: relative;
  margin-left: -190px;
  margin-top: -20px;
  font-size: 25px;
  font-weight: 700;
  color: #0d3d86;
  letter-spacing: 4px;
}

.company-reward-logo {
  position: relative;
  height: 50px;
  margin: 0px 20px 20px 20px;
  padding: 5px;
  border-radius: 3px;
  border: 1px solid #efefef;
}

.reward-top-user-avatar {
  position: absolute;
  top: 50px;
  left: 75px;
  width: 50px;
  height: 50px;
  border-radius: 100px;
  box-shadow: 0px 0px 5px white;
}

.reward-top-user-avatar:hover {
  cursor: pointer;
}


.reward-top-name {
  position: absolute;
  top: 110px;
  width: 200px;
  text-align: center;
  left: 0px;
  font-size: 16px;
  font-weight: 600;
  color: black;
  font-family: Courier;
  letter-spacing: 2px;
}

.reward-top-amount-detail {
  position: absolute;
  top: 135px;
  width: 200px;
  text-align: center;
  left: 0px;
  font-size: 14px;
  font-weight: 600;
  color: black;
  font-family: Courier;
  letter-spacing: 2px;
}

.reward-top-time {
  position: absolute;
  top: 160px;
  width: 200px;
  text-align: center;
  left: 0px;
  font-size: 12px;
  color: black;
  letter-spacing: 2px;
}


.reward-top-rank-img {
  position: absolute;
  left: 75px;
  top: -25px;
  width: 50px;
}


.top-reward-module {
  position: relative;
  width: 700px;
  height: 250px;
  margin-bottom: 60px;
  margin: 30px auto;
}

.top1-reward-module {
  position: absolute;
  left: 250px;
  top: 0px;
  width: 200px;
  height: 250px;
  background: linear-gradient(to bottom, #7372ff, white);
  opacity: 0.8;
  border-radius: 5px 5px 0px 0px;
}

.top2-reward-module {
  position: absolute;
  left: 90px;
  top: 30px;
  width: 200px;
  height: 220px;
  background: linear-gradient(to bottom, #ffa3f7, white);
  opacity: 0.8;
  border-radius: 5px 5px 0px 0px;
}

.top3-reward-module {
  position: absolute;
  right: 90px;
  top: 60px;
  width: 200px;
  height: 190px;
  background: linear-gradient(to bottom, #92f5d9, white);
  opacity: 0.8;
  border-radius: 5px 5px 0px 0px;
}

.reward-common-list-module {
  position: relative;
  width: 700px;
  height: 45px;
  margin: 30px auto;
}

.reward-detail {
  position: absolute;
  left: 55px;
  top: 0px;
  height: 45px;
  width: 645px;
  border: 0px;
  border-radius: 50px;
  background-color: #1c7ce5;
  color: white;
}

.reward-rank {
  position: absolute;
  left: 0px;
  top: 5px;
  height: 35px;
  width: 35px;
  border: 0px;
  background-color: #1c7ce5;
  color: white;
  font-size: 17px;
  font-family: Courier;
  font-weight: 700;
}

.reward-user-avatar {
  position: absolute;
  left: 0px;
  top: 0px;
  width: 45px;
  height: 45px;
  border-radius: 50px;
  box-shadow: 0px 0px 5px #e7e6fc;
}

.reward-user-avatar:hover {
  cursor: pointer;
}

.reward-amount-detail, .reward-amount-nickName {
  position: absolute;
  left: 250px;
  top: 0px;
  height: 45px;
  line-height: 45px;
  width: 400px;
  font-size: 15px;
  color: white;
  text-align: left;
  letter-spacing: 5px;
  font-weight: 700;
}

.reward-amount-nickName {
  left: 57px;
  font-weight: 500;
  font-size: 14px;
  letter-spacing: 1px;
}

.reward-time {
  position: absolute;
  right: 20px;
  top: 0px;
  height: 45px;
  line-height: 45px;
  width: 200px;
  font-size: 12px;
  color: white;
  text-align: right;
}

#load-more-btn {
  position: relative;
  width: 23%;
  height: 45px;
  line-height: 45px;
  background-color: #f2f2f2;
  border-radius: 5px;
  margin: 60px auto;
  text-align: center;
}

#load-more-end-btn {
  position: relative;
  width: 23%;
  height: 45px;
  line-height: 45px;
  background-color: #f2f2f2;
  border-radius: 5px;
  margin: 60px auto;
  text-align: center;
}

#load-more-btn:hover {
  cursor: pointer;
  background-color: #1c7ce5;
  color: white;
}

.selectSponsor {
  background-color: #ffc36d;
  color: white
}
</style>
