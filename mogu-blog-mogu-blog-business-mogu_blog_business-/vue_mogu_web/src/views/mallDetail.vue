<template>

  <article>
    <div class="container main" style="min-height: 590px; margin-top: 20px;">
      <el-row type="flex" justify="center">
        <el-col :span="11" class="main-left" >
          <el-row>
            <el-image :src="selectCoverPhotoUrl" class="selectImg"></el-image>
          </el-row>
          <el-row>
            <el-col :span="6" v-for="(item, index) in product.slidesPhotoUrlList" :key="index">
              <el-image :src="item" :class="item ===selectCoverPhotoUrl?'selectImg':''"  style="margin-right: 5px;"  @mouseover="selectCoverPhotoUrlMethod(item)"></el-image>
            </el-col>
          </el-row>

        </el-col>

        <el-col :span="11" class="main-middle">
          <el-row class="title">{{product.name}}</el-row>
          <el-row class="summary">{{product.summary}}</el-row>
          <el-row class="active">
            <el-col :span="16" class="active-left">
              <el-row>
                <el-col :span="6">
                  <div class="l1">限时秒杀</div>
                </el-col>
                <el-col :span="18" class="l2">
                  <span class="l21" v-if="product.price">{{formatPrice(product)}}</span>
                  <span class="l22" v-if="product.chargeType === 3">
                    <span v-if="product.payType === 1">{{product.price}}积分</span>
                    <span v-else>¥ {{product.price}}</span>
                  </span>
                </el-col>
              </el-row>

            </el-col>
            <el-col :span="8" class="active-right">
              <div class="active-right-main">
                <div class="r1">距结束还剩下</div>
                <div class="r2">
                  <div class="r2-item">06</div><span class="r2-item-symbol">:</span>
                  <div class="r2-item">22</div><span class="r2-item-symbol">:</span>
                  <div class="r2-item">06</div>
                </div>
              </div>

            </el-col>
          </el-row>
          <el-row class="product-tag">
            <el-tag class="tag" effect="dark" type="danger" size="small" hit v-if="product.isTop === 1">置顶</el-tag>
            <el-tag class="tag" effect="dark" type="warning" size="small" hit v-if="product.isSelection === 1">精选</el-tag>
            <el-tag class="tag" v-if="product.commonCategory" :type="colorList[product.commonCategory.sort % 5]" effect="plain" size="small" hit>{{product.commonCategory.name}}</el-tag>
          </el-row>

          <el-row class="product-config">
            <el-row class="pc">
              <el-col :span="4" class="p1">
                <span class="">运费</span>
              </el-col>
              <el-col :span="20">
                无需配置
              </el-col>
            </el-row>

            <el-row class="pc">
              <el-col :span="4" class="p1">
                <span class="">版本</span>
              </el-col>
              <el-col :span="20">
                标准版
              </el-col>
            </el-row>

            <el-row class="pc">
              <el-col :span="4" class="p1">
                <span class="">支付方式</span>
              </el-col>
              <el-col :span="20">
                <span v-if="product.payType == 1">积分支付</span>
                <span v-else>现金支付</span>
              </el-col>
            </el-row>

          </el-row>

          <el-row >
            <button class="buyNow" @click="buyProduct()">立即抢购</button>
          </el-row>

        </el-col>
      </el-row>

      <el-row type="flex" class="row-bg" justify="space-around">
        <el-col :span="17">
          <div class="productDetail">
            <div class="tip">
              <strong>内容描述</strong>
            </div>
            <div v-if="product.content" v-html="product.content">{{product.content}}</div>
            <div v-else>
              <el-empty description="暂无更多描述" image=""></el-empty>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="tip">
            <strong>推荐商品</strong>
          </div>
          <el-image class="recommendProduct" v-for="product in productList" :key="product.uid" :src="product.coverPhotoUrl" @click="goProduct(product)"></el-image>
        </el-col>

      </el-row>

    </div>


    <!--支付窗口组件-->
    <PayWindow v-if="showPayWindow" :product="productVO" resourceType="12" @payCallback="payCallback"></PayWindow>
  </article>

</template>

<script>
import {getProductList, getProductDetail} from "../api/mall";
import PayWindow from "../components/PayWindow"

export default {
  data() {
    return {
      showPayWindow: false,
      productList: [],
      productVO: {},
      productUid: null, // 商品uid
      product: {},
      selectCoverPhotoUrl: null,
      colorList: ["primary", "info", "success", "warning", "danger"],
    }
  },
  watch: {
    $route(to, from) {
      this.productUid = this.$route.params.productUid
      this.getProductDetailMethod()
    },
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.userInfo = this.$store.state.user.userInfo
    },
  },
  created() {
    this.userInfo = this.$store.state.user.userInfo
    this.productUid = this.$route.params.productUid
    this.getProductDetailMethod()
    this.getProductListMethod()
  },
  components: {
    PayWindow
  },
  methods: {
    goProduct(product) {
      this.$router.push({ path: "/mall/" + product.uid });

    },
    // 格式化价格
    formatPrice(product) {
      let inDiscount = this.$commonUtil.checkDiscount(product)
      if (product.payType === 1) {
        // 是否是折扣价格
        if (product.chargeType === 3 && inDiscount) {
          return product.discountPrice + "积分"
        }
        return product.price + "积分"
      } else {
        // 是否是折扣价格
        if (product.chargeType === 3 && inDiscount) {
          return "¥ " + product.discountPrice/100
        }
        return  "¥ " + product.price/100
      }
    },
    buyProduct() {
      let product = this.product
      this.productVO = this.$commonUtil.convertProduct(product)
      this.showPayWindow = true
    },
    payCallback(paySuccess) {
      this.showPayWindow = false
      // 支付成功后，打开页面刷新
      if (paySuccess) {
        setTimeout(function () {
          location.reload();
        }, 1000);
      }
    },
    selectCoverPhotoUrlMethod(item) {
      this.selectCoverPhotoUrl = item
    },
    getProductDetailMethod() {
      let params = {}
      params.uid = this.productUid
      getProductDetail(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          let product = response.data
          if (product.slidesPhotoUrlList && product.slidesPhotoUrlList.length > 0) {
            this.selectCoverPhotoUrl = product.slidesPhotoUrlList[0]
          } else {
            this.selectCoverPhotoUrl = product.coverPhotoUrl
            product.slidesPhotoUrlList = [product.coverPhotoUrl]
          }
          this.product = product
          console.log("选择的图片", this.selectCoverPhotoUrl)
        }
      })
    },
    getProductListMethod() {
      let params = {}
      params.currentPage = 1
      params.pageSize = 5
      params.orderByDescColumn = "isSelection"
      getProductList(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          this.productList = response.data.records
        }
      })
    },
  },
}
</script>

<style>
.productDetail img {
  max-width: 100%;
  max-height: 100%;
}
</style>

<style scoped>
article {
  width: 100%;
  margin-top: 40px;
  overflow: hidden
}
.main {
  background: #ffffff;
}

.main-left {
  margin-top: 20px;
  left: 352px;
  top: 137px;
  width: 560px;
  height: 560px;
  opacity: 1;
  margin-right: 30px;
}
.main-middle {
  margin-top: 20px;
}
.main-middle .title{

  width: 138px;
  height: 32px;
  opacity: 1;
  /** 文本1 */
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 0px;
  line-height: 32px;
  color: rgba(17, 17, 17, 1);
  text-align: left;
  vertical-align: top;

}

.main-middle .summary{
  width: 545px;
  height: 48px;
  opacity: 1;
  /** 文本1 */
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0px;
  line-height: 24px;
  color: rgba(102, 102, 102, 1);
  text-align: left;
  vertical-align: top;
  margin-top: 10px;
}

.main-middle .active .active-left {
  width: 340px;
  height: 76px;
  opacity: 1;
  border-radius: 8px 0px 0px 8px;
  background: linear-gradient(90deg, rgba(255, 51, 64, 1) 0%, rgba(255, 191, 102, 1) 100%);
  line-height: 76px;
}

.main-middle .active .active-left .l1{

  margin-top: 12px;
  margin-left: 12px;
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(245, 132, 138, 1);
  backdrop-filter: blur(4px);

  /** 文本1 */
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0px;
  line-height: 24px;
  color: rgba(255, 255, 255, 1);
  text-align: center;
  vertical-align: center;

}

.main-middle .active .active-left .l2{
  /*margin-top: 17px;*/
  line-height: 70px;
  height: 70px;
  text-align: left;
}

.main-middle .active .active-left .l21{

  opacity: 1;
  font-size: 28px;
  font-weight: 500;
  letter-spacing: 0px;

  color: rgba(255, 255, 255, 1);
  text-align: left;
  vertical-align: top;
}

.main-middle .active .active-left .l22{
  opacity: 1;
  /** 文本1 */
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0px;
  margin-left: 10px;
  color: rgba(255, 178, 166, 1);
  text-align: left;
  vertical-align: top;
  text-decoration-line: line-through;
}

.main-middle .active .active-right {
  width: 182px;
  height: 76px;
  opacity: 1;
  border-radius: 0px 8px 8px 0px;
  background: rgba(253, 240, 232, 1);
}

.main-middle .active .active-right .active-right-main{
  margin-top: 12px;
  margin-left: 12px;
}

.main-middle .active .active-right .r1{

  height: 24px;
  opacity: 1;
  /** 文本1 */
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0px;
  line-height: 24px;
  color: rgba(237, 111, 33, 1);
  text-align: center;
  vertical-align: top;

}

.main-middle .active .active-right .r1{

  height: 24px;
  opacity: 1;
  /** 文本1 */
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0px;
  line-height: 24px;
  color: rgba(237, 111, 33, 1);
  text-align: center;
  vertical-align: top;

}

.main-middle .active .active-right .r2{
  text-align: center;
}

.main-middle .active .active-right .r2-item{
  width: 22px;
  height: 22px;
  opacity: 1;
  border-radius: 4px;
  background: rgba(237, 111, 33, 1);
  display: inline-block;

  /** 文本1 */
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0px;
  line-height: 21px;
  color: rgba(255, 255, 255, 1);
  text-align: center;
  vertical-align: top;
}

.main-middle .active .active-right .r2-item-symbol{
  margin-left: 2px;
  color: rgba(237, 111, 33, 1);
}

.main-middle .product-tag {
  height: 40px;
  line-height: 40px;
  text-align: left;
}
.main-middle .product-tag .tag {
  margin-right: 10px;
}

.main-middle .product-config {
  min-height: 120px;
}

.main-middle .product-config .pc {
  margin-top: 10px;
}

.main-middle .product-config .p1{
  color: #aaa9a9;
}

.main-middle .buyNow {
  cursor: pointer;
  margin-top: 50px;
  width: 100%;
  height: 52px;
  opacity: 1;
  border-radius: 26px;
  background: linear-gradient(270deg, rgba(215, 27, 26, 1) 0%, rgba(253, 88, 68, 1) 100%);
  border: none;                /* 去除边框 */

  font-size: 20px;
  font-weight: 900;
  letter-spacing: 2px;
  line-height: 18px;
  color: rgba(255, 255, 255, 1);
  text-align: center;
  vertical-align: top;
}

.selectImg {
  border: 2px #0db9f0 solid
}

.tip {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #ecf8ff;
  border-radius: 4px;
  border-left: 5px solid #50bfff;
  color: #888888;
}
.tip strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}
.recommendProduct {
  margin-bottom: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.recommendProduct:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px 0 rgba(95,101,105,.15);
}
</style>
