<template>
  <div>
    <div class="hide_box" v-if="showPay"></div>
    <div class="shang_box" v-if="showPay">
      <a class="shang_close" href="javascript:void(0)" @click="dashangToggle()" title="关闭">关闭</a>
      <div class="shang_tit">
        <p>感谢您的支持，我会继续努力的!</p>
      </div>
      <div class="shang_payimg">
        <img :src="payCode" alt="扫码支持" title="扫一扫">
      </div>
      <div class="pay_explain">扫码打赏，你说多少就多少</div>
      <div class="shang_payselect">
        <div
          class="pay_item"
          :class="[this.payMethod==1?'checked':'']"
          data-id="alipay"
          @click="choosePay('1')"
        >
          <span class="radiobox"></span>
          <span class="pay_logo">
            <img src="../../../static/images/alipay.jpg" alt="支付宝">
          </span>
        </div>
        <div
          class="pay_item"
          :class="[this.payMethod==2?'checked':'']"
          data-id="weipay"
          @click="choosePay('2')"
        >
          <span class="radiobox"></span>
          <span class="pay_logo">
            <img src="../../../static/images/wechat.jpg" alt="微信">
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getWebConfig } from "../../api/index";
export default {
  name: "PayCode",
  watch: {

  },
  data() {
    return {
      webConfigData: {},
      showPay: true, //是否显示支付
      payMethod: 1, // 1: 支付宝  2：微信
      payCode: "", //支付码图片
    }
  },
  created() {
    getWebConfig().then(response => {
      console.log("从接口中获取")
      if (response.code == this.$ECode.SUCCESS) {
        this.webConfigData = response.data;
        this.payCode = this.webConfigData.aliPayPhoto;
      }
    });
  },
  methods: {
    dashangToggle: function() {
      this.$emit("close", "")
      // this.showPay = !this.showPay;
    },
    // 支付方式
    choosePay: function(type) {
      this.payMethod = type;
      if (type == 1) {
        this.payCode = this.webConfigData.aliPayPhoto;
      } else {
        this.payCode = this.webConfigData.weixinPayPhoto;
      }
    },
  },
}
</script>

<style scoped>

</style>
