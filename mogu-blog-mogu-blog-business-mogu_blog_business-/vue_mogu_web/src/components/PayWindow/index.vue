<template>
  <el-dialog
    title="支付确认"
    :modal-append-to-body="false"
    :visible.sync="dialogVisible"
    :before-close="beforeClose"
    :width="dialogWidth">
    <el-row>
      <!--      <el-card>-->
      <el-col :span="8" style="padding: 10px">
        <div  v-if="product.images" style="height: 160px;">
          <el-image
            style="height: 100%; width: 100%; border-top-left-radius: 5px; border-top-right-radius: 5px;"
            :src="product.images"
            fit="cover"></el-image>
        </div>
      </el-col>

      <el-col :span="16" style="padding: 10px">
        <div
          style="height: 80px; background: #FFFFFF; padding: 5px; border-bottom-left-radius: 5px; border-bottom-right-radius: 5px;">
          <div style="height: 30px;">
            <el-tag effect="dark" size="mini" v-if="product.chargeType == 1" type="primary">免费</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="product.chargeType == 3" type="danger">限时优惠</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="product.chargeType == 4" type="warning">会员免费</el-tag>
            <span style="font-weight: 400; font-size: 16px; color: #2D2D2D;">{{ product.title }}</span>

            <el-tag effect="dark" size="mini" v-if="resourceType == 'BLOG'" type="success">文章</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="resourceType == 'SUBJECT'" type="success">专栏</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="resourceType == 'RESOURCE'" type="success">资源</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="resourceType == 'VIP'" type="success">会员</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="resourceType == 'PRODUCT'" type="success">商品</el-tag>


          </div>
          <div style="height: 60px">
            <div>
              {{ product.summary }}
            </div>
            <div style="margin-top: 8px; float: right" v-if="product.discountStartTime && product.discountEndTime">
              <TimeCountDown :startTime="product.discountStartTime" :endTime="product.discountEndTime"
                             endText="优惠活动已结束 "/>
            </div>
          </div>
          <div>
            <span style="font-size: 14px; color: #999999; font-weight: 400;" v-if="product.payOrderCount>0">{{ product.payOrderCount }}人购买</span>
            <span style="float: right; margin-right: 10px; font-size: 18px; color: #076FE2; font-weight: 500;">

                  <span class="price l blue bold" v-if="!product.price">免费</span>
                  <span class="price l blue bold" v-else-if="product.price == product.discountPrice">
                    <!--如果是会员免费-->
                    <span v-if="product.chargeType == 4 && userTag > 0">
                      <span v-if="product.payType == 1">
                          <span>0 积分</span>
                          <span
                            style="text-decoration: line-through; color: #999999; font-size: 14px;">{{ product.price }} 积分</span>
                      </span>
                      <span v-else>
                          <span>￥0 </span>
                          <span style="text-decoration: line-through; color: #999999; font-size: 14px;">￥{{
                              product.price / 100
                            }}</span>
                      </span>
                    </span>
                    <span v-else>
                      <span v-if="product.payType == 1">
                        {{ product.price }} 积分
                      </span>
                      <span v-else>
                        ￥{{ product.price / 100 }}
                      </span>
                    </span>
                  </span>
                  <span class="price l blue bold" v-else-if="product.price != product.discountPrice">
                    <span v-if="product.payType == 1">
                        <!--判断是否处于折扣-->
                        <span v-if="product.betweenDiscount">
                          <span>{{ product.discountPrice }} 积分</span>
                          <span
                            style="text-decoration: line-through; color: #999999; font-size: 14px;">{{ product.price }} 积分</span>
                        </span>
                        <span v-else>
                          <span>{{ product.price }} 积分</span>
                        </span>
                    </span>
                    <span v-else>
                        <!--判断是否处于折扣-->
                        <span v-if="product.betweenDiscount">
                          <span>￥{{ product.discountPrice / 100 }} </span>
                          <span style="text-decoration: line-through; color: #999999; font-size: 14px;">￥{{product.price / 100}}</span>
                        </span>
                        <span v-else>
                          <span>￥{{ product.price/100 }} </span>
                        </span>
                    </span>
                  </span>
                </span>
          </div>
          <div>
            <el-row style=" margin-top: 5px;">
              <el-col :span="12">
                <el-button type="success" style="width: 160px;" @click="goPay()" :disabled="qrcode != null">
                  <span v-if="qrcode">扫码支付</span>
                  <span v-else>下单购买</span>
                </el-button>
              </el-col>
              <el-col :span="12">
                <span v-if="isLogin && product.payType == 1" style="margin-left: 10px; text-align: center; line-height: 40px;">
                  用户剩余积分:
                  <span style="color: #0E9A00; font-size: 14px;">{{ credits }} 积分</span>
                </span>
              </el-col>
            </el-row>

          </div>
        </div>
      </el-col>
      <!--      </el-card>-->
    </el-row>

    <div v-if="qrcode">
      <div class="shang_tit">
        <p>打开
          <span v-if="this.payMethod== 'ALI_PAY' || this.payMethod== 'YUN_GOU_OS_ALI_PAY'">支付宝</span>
          <span v-else-if="this.payMethod== 'WECHAT_PAY' || this.payMethod== 'YUN_GOU_OS_WECHAT_PAY'">微信</span>
          ，扫码完成支付
        </p>
      </div>
      <div class="shang_payimg">
        <Qrcode v-if="qrcode" :text="qrcode" :size="200"/>
      </div>

      <div class="pay_explain">
        如遇支付问题，请联系站长解决
      </div>

      <div class="shang_payselect">
        <div
          v-if="cashPayMethod.aliPay"
          class="pay_item"
          :class="[this.payMethod== 'ALI_PAY'?'checked':'']"
          data-id="alipay"
          @click="submitBuy('ALI_PAY')"
        >
          <span class="radiobox"></span>
          <span class="pay_logo">
            <img src="../../../static/images/alipay.jpg" alt="支付宝">
          </span>
        </div>

        <div
          v-if="cashPayMethod.wxPay"
          class="pay_item"
          :class="[this.payMethod == 'WECHAT_PAY' ?'checked':'']"
          data-id="weipay"
          @click="submitBuy('WECHAT_PAY')"
        >
          <span class="radiobox"></span>
          <span class="pay_logo">
            <img src="../../../static/images/wechat.jpg" alt="微信">
          </span>
        </div>

        <div
          v-if="cashPayMethod.yunGouOSWxPay"
          class="pay_item"
          :class="[this.payMethod == 'YUN_GOU_OS_WECHAT_PAY' ?'checked':'']"
          data-id="weipay"
          @click="submitBuy('YUN_GOU_OS_WECHAT_PAY')"
        >
          <span class="radiobox"></span>
          <span class="pay_logo">
            <img src="../../../static/images/wechat.jpg" alt="微信">
          </span>
        </div>

      </div>
    </div>

  </el-dialog>
</template>

<script>
import {buyNow, checkWhetherPay} from "../../api/pay";
import TimeCountDown from "../TimeCountDown/index.vue";
import Qrcode from 'vue-qrcode-component'
import {getCurrentUserCredits} from "../../api/about";
import {mapMutations} from "vuex";

export default {
  name: "PayWindow",
  props: ["product", "resourceType"],
  data() {
    return {
      dialogVisible: true,
      qrcode: null,
      payOrderUid: null, // 支付单号
      payMethod: "ALI_PAY", // ALI_PAY: 支付宝  WECHAT_PAY：微信
      interval: null, // 支付检查定时器
      userTag: 0,
      credits: 0,
      isLogin: false,
      dialogWidth: "30%",
      cashPayMethod: {
        aliPay: false,
        wxPay: false,
        yunGouOSAliPay: false,
        yunGouOSWxPay: false,
      },
    };
  },
  components: {
    Qrcode,
    TimeCountDown,
  },
  created() {
    this.setCashPayMethodList()
    this.getUserCredits()
    console.log("获取详情", this.product)
    let userInfo = this.$store.state.user.userInfo
    this.isLogin = this.$store.state.user.isLogin;
    if (userInfo) {
      this.userTag = userInfo.userTag
    }
    this.initWidth()
  },
  mounted() {
    this.initWidth()
  },
  methods: {
    ...mapMutations(['setLoginMessage']),
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
    initWidth() {
      const width = document.body.clientWidth;
      if (width < 600) {
        this.dialogWidth = "90%";
      } else if(width < 1200) {
        this.dialogWidth = "60%";
      }  else if(width < 1400) {
        this.dialogWidth = "50%";
      }  else if(width < 1600) {
        this.dialogWidth = "40%";
      } else {
        this.dialogWidth = "30%";
      }
    },
    setCashPayMethodList: function() {
      let webConfigData = this.$store.state.app.webConfigData
      console.log("获取配置信息", webConfigData)
      if(webConfigData && webConfigData.cashPayMethodList) {
        let cashPayMethodList = JSON.parse(webConfigData.cashPayMethodList)
        for(let a=0; a<cashPayMethodList.length; a++) {
          switch (cashPayMethodList[a]) {
            case "1": {
              this.cashPayMethod.aliPay = true
            } break;
            case "2": {
              this.cashPayMethod.wxPay = true
            } break;
            case "3": {
              this.cashPayMethod.yunGouOSAliPay = true
            } break;
            case "4": {
              this.cashPayMethod.yunGouOSWxPay = true
            } break;
            default: {
              console.log("现金支付方式设置有误！！")
            }
          }
        }
      }
      console.log("转化后的信息=", this.cashPayMethod)
    },
    goPay: function () {
      console.log("点击去支付", this.product)
      let productVO = this.product
      if (productVO == null) {
        return
      }
      if (productVO.payType == 1) {
        this.submitBuy('CREDITS_PAY')
      } else {
        // 如果是现金支付
        if (this.cashPayMethod.aliPay) {
          this.submitBuy('ALI_PAY')
        } else if (this.cashPayMethod.wxPay) {
          this.submitBuy('WECHAT_PAY')
        } else if (this.cashPayMethod.yunGouOSAliPay) {
          this.submitBuy('YUN_GOU_OS_ALI_PAY')
        } else if (this.cashPayMethod.yunGouOSWxPay) {
          this.submitBuy('YUN_GOU_OS_WECHAT_PAY')
        } else {
          this.$message.success("暂未设置现金支付方式")
        }

      }
    },
    submitBuy: function (type) {
      // 需要校验是否登录
      let isLogin = this.$store.state.user.isLogin;
      if (!isLogin) {
        this.$notify.error({
          title: "警告",
          message: "登录后才可以进行支付",
          offset: 100,
        });
        return
      }
      this.payMethod = type
      // 立即购买
      let params = {}
      params.client = "PC"
      params.resourceUid = this.product.resourceUid
      params.resourceType = this.product.resourceType
      params.payMethod = type
      buyNow(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          if (response.data) {
            this.qrcode = response.data.url
            this.payOrderUid = response.data.payOrderUid
          } else {
            this.$message.success(response.message)
          }
          this.checkPay()
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 校验是否购买
    checkPay() {
      let count = 0
      this.interval = setInterval(() => {
        count++
        // 当检查30次将关闭
        if (count > 30) {
          clearInterval(this.interval)
        }
        let params = new URLSearchParams()
        params.append("productUid", this.product.resourceUid)
        if (this.payOrderUid) {
          params.append("orderUid", this.payOrderUid)
        }
        checkWhetherPay(params).then(response => {
          let isPay = response.data
          if (isPay) {
            clearInterval(this.interval)
            this.$message.success("支付完成")
            this.$emit("payCallback", true)
          }
        })
      }, 2000);
    },
    // 关闭时的回调
    beforeClose(done) {
      done();
      this.$emit("payCallback", false)
    },
  },
}
</script>

<style scoped>

</style>
