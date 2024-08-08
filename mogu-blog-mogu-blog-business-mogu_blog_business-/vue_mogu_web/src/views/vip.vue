<template>

  <div style=" -webkit-user-drag: none; user-select: none;" :style="{'min-height': minHeight + 'px'}" class="vipDiv">
    <div style="margin: 0 auto; max-width: 1400px">
      <div>
        <div style="text-align: center; margin-bottom: 20px; font-size: 40px; font-weight: 700; color: #000; line-height: 100px;">
              <span>
                开通会员
              </span>
          <span>
                获取海量优质内容，提升学习效率
              </span>
        </div>
      </div>

      <el-row   type="flex" justify="space-around">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="vipConfig in vipConfigList" :key="vipConfig.uid" v-if="vipConfig.vipExtraConfig">
          <div>
            <div class="vipCard"   :style="{backgroundColor: vipConfig.vipExtraConfig.backgroundColor, color: vipConfig.vipExtraConfig.textColor}" >
              <div class="img-gradient" :style="{backgroundImage: vipConfig.vipExtraConfig.backgroundImage, borderColor: vipConfig.vipExtraConfig.borderColor}">
                <div class="price">
                  <div class="name"  style="font-size: 20px; font-weight: 800;" :style="{color:vipConfig.vipExtraConfig.textColor}">{{vipConfig.name}}</div>
                  <div class="" style="font-size: 22px; text-align: center; font-weight: 700;" :style="{color:vipConfig.vipExtraConfig.textColor}">
                    <span class=" real-price" v-if="vipConfig.chargeType === 3 && checkDiscount(vipConfig)">
                      <span v-if="vipConfig.payType === 2">
                          ¥{{vipConfig.discountPrice/100}}
                      </span>
                      <span v-if="vipConfig.payType === 1">
                          {{vipConfig.discountPrice}}积分
                      </span>
                    </span>
                    <span class=" real-price" v-else>
                      <span v-if="vipConfig.payType === 2">
                          ¥{{vipConfig.price/100}}
                      </span>
                      <span v-if="vipConfig.payType === 1">
                          {{vipConfig.price}}积分
                      </span>
                    </span>


                  </div>
                  <span class="origin-price" style="text-align: center;" :style="{color:vipConfig.vipExtraConfig.textColor}" v-if="vipConfig.chargeType === 3 && checkDiscount(vipConfig)">

                    <del v-if="vipConfig.payType === 2">
                          ¥{{vipConfig.price/100}}
                      </del>
                      <del v-if="vipConfig.payType === 1">
                          {{vipConfig.price}}积分
                      </del>

                  </span>

                  <el-button  class="btn" style="margin-top: 10px" :disabled="userInfo.userTag === 15"
                              :style="{backgroundColor: vipConfig.vipExtraConfig.buttonBackgroundColor, color:vipConfig.vipExtraConfig.buttonTextColor, borderColor: vipConfig.vipExtraConfig.buttonBackgroundColor}" @click="buyVip(vipConfig)">
                    <span v-if="userInfo.userTag === 15">已成为终身会员</span>
                    <span v-else>升级会员</span>
                  </el-button>

                </div>
              </div>

              <div class="gain-list" style="margin-top: 10px;">
                <div style="width: 100%; justify-content: space-between; line-height: 28px; font-size: 12px; gap: 8px;" v-for="equityItem in vipConfig.vipExtraConfig.equityList">
                  <el-row type="flex" justify="space-between">
                    <el-col :span="12">
                      <div :style="{color: vipConfig.vipExtraConfig.equityTextColor}">{{equityItem.name}}</div>
                    </el-col>
                    <el-col :span="12">
                      <!--对-->
                      <div role="img" aria-label="check-circle" class="anticon anticon-check-circle"
                           style="font-size: 14px; color: rgb(97, 203, 96); float: right" v-if="equityItem.value === 'true'">
                        <svg viewBox="64 64 896 896" focusable="false" data-icon="check-circle" width="1em" height="1em"
                             fill="currentColor" aria-hidden="true">
                          <path
                            d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm193.5 301.7l-210.6 292a31.8 31.8 0 01-51.7 0L318.5 484.9c-3.8-5.3 0-12.7 6.5-12.7h46.9c10.2 0 19.9 4.9 25.9 13.3l71.2 98.8 157.2-218c6-8.3 15.6-13.3 25.9-13.3H699c6.5 0 10.3 7.4 6.5 12.7z"></path>
                        </svg>
                      </div>

                      <!--错-->
                      <div role="img" aria-label="check-circle" class="anticon anticon-check-circle"
                           style="font-size: 14px; color: rgb(231, 54, 19); float: right" v-else-if="equityItem.value === 'false'">
                        <svg viewBox="64 64 896 896" focusable="false" data-icon="check-circle" width="1em" height="1em"
                             fill="currentColor" aria-hidden="true">
                          <path
                            d="M512 64c247.4 0 448 200.6 448 448S759.4 960 512 960 64 759.4 64 512 264.6 64 512 64zm127.98 274.82h-.04l-.08.06L512 466.75 384.14 338.88c-.04-.05-.06-.06-.08-.06a.12.12 0 00-.07 0c-.03 0-.05.01-.09.05l-45.02 45.02a.2.2 0 00-.05.09.12.12 0 000 .07v.02a.27.27 0 00.06.06L466.75 512 338.88 639.86c-.05.04-.06.06-.06.08a.12.12 0 000 .07c0 .03.01.05.05.09l45.02 45.02a.2.2 0 00.09.05.12.12 0 00.07 0c.02 0 .04-.01.08-.05L512 557.25l127.86 127.87c.04.04.06.05.08.05a.12.12 0 00.07 0c.03 0 .05-.01.09-.05l45.02-45.02a.2.2 0 00.05-.09.12.12 0 000-.07v-.02a.27.27 0 00-.05-.06L557.25 512l127.87-127.86c.04-.04.05-.06.05-.08a.12.12 0 000-.07c0-.03-.01-.05-.05-.09l-45.02-45.02a.2.2 0 00-.09-.05.12.12 0 00-.07 0z"></path>
                        </svg>
                      </div>

                      <!--其它-->
                      <div style="font-weight: 600; float: right;" :style="{color: vipConfig.vipExtraConfig.equityTextColor}" v-else >{{equityItem.value}}</div>

                    </el-col>
                  </el-row>
                </div>

              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div style="text-align: center; margin-top: 15px; color: #8F8F8F; font-size: 14px;">购买遇到问题 ？
        <a href="/moment">去反馈</a>
      </div>
    </div>


    <!--支付窗口组件-->
    <PayWindow v-if="showPayWindow" :product="productVO" resourceType="12" @payCallback="payCallback"></PayWindow>
  </div>

</template>

<script>
import {getVipList} from "../api/vip";
import PayWindow from "../components/PayWindow"

export default {
  data() {
    return {
      vipConfigList: [], // 会员配置列表
      showPayWindow: false,
      productVO: {},
      userInfo: {}, // 用户信息
      minHeight: window.screen.height - 273,
    }
  },
  watch: {
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.userInfo = this.$store.state.user.userInfo
    },
  },
  created() {
    this.userInfo = this.$store.state.user.userInfo
    this.getVipListMethod()
  },
  components: {
    PayWindow
  },
  methods: {
    checkDiscount(entity) {
      return this.$commonUtil.checkDiscount(entity)
    },
    buyVip(vipConfig) {
      this.productVO = this.$commonUtil.convertVip(vipConfig)
      this.showPayWindow = true
    },
    payCallback(paySuccess) {
      console.log("支付回调", paySuccess)
      this.showPayWindow = false
      // 支付成功后，打开页面刷新
      if (paySuccess) {
        setTimeout(function () {
          location.reload();
        }, 1000);
      }
    },
    getVipListMethod() {
      getVipList().then(response => {
        console.log(response)
        if (response.code == this.$ECode.SUCCESS) {
          this.vipConfigList = response.data.records
        }
      })
    },
  },
}
</script>

<style scoped>

.vipCard .price {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px 14px;


}

.vipCard {
  position: relative;
  width: 278px;
  padding: 16px 14px;
  border-radius: 16px;
  box-shadow: 0 0 2px 0 rgba(0, 0, 0, .3);
}

.vipCard .price .origin-price {
  font-size: 12px;
}

.vipCard .price .btn {
  height: 44px;
  width: 180px;
  font-weight: 700;
  font-size: 18px;
  border-radius: 25px;
  transition: transform 0.3s ease; /* 平滑过渡效果 */
}

.vipCard .price .btn:hover {
  transform: scale(1.1); /* 鼠标悬停时放大到1.5倍 */
}


.img-gradient {
  /* 设置元素的尺寸 */
  width: 280px;
  height: auto;

  /* 创建从黑色到透明的线性渐变背景 */
  /* 渐变方向是从上到下 */
  background-image: linear-gradient(to bottom, rgba(87, 81, 79, 0.5), rgba(85, 77, 73, 0.5), transparent);

  /* 其他样式 */
  border: 1px solid #333; /* 轻微的边框，可选 */
  border-radius: 10px; /* 圆角边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); /* 轻微的阴影效果 */

  /* 文本样式 */
  display: flex;
  justify-content: center;
  align-items: center;
  color: white; /* 文字颜色为白色 */

  /* 其他样式 */
  aspect-ratio: 1.3229308;
  z-index: 0;

  transition: transform 0.3s ease; /* 平滑过渡效果 */
}

/* 鼠标悬停时的div样式 */
.img-gradient:hover {
  transform: scale(1.05); /* 鼠标悬停时放大到1.5倍 */
}

.vipCard .gain-list {
  display: flex;
  flex-direction: column;
  padding: 0 20px;
}

.vipDiv {
  margin-top: 40px;
  /* 创建彩虹渐变背景 */
  background-image: linear-gradient(to bottom, #e0edff, #fdfeff);
}

</style>
