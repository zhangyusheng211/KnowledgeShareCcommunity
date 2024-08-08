<template>

  <article>
    <!--  搜索框start -->
    <div class="top-wrap">
      <div class="shizhan-header">
        <div class="shizhan-header-wrap w1430">
          <div class="banner">
            <a class="title" href="/">
              <img class="h100" src="../../static/images/mall.png" alt="">
            </a>
            <div style="display: block; font-size: 20px;font-weight: 800;color: #000;">社区商城</div>
            <div id="codingIndexBanner" style="display: block; margin-left: 10px;">便捷商城，好物云集</div>
          </div>
          <div id="szHeaderSearch" class="shizhan-header-search">
            <el-input v-model="queryParams.keyword"  placeholder="搜索感兴趣的实战课程内容"  auto-complete="off" @keyup.enter.native="getProductListMethod({})">
              <template slot="append">
                <el-button type="primary" icon="el-icon-search" size="mini" @click="getProductListMethod({})"></el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>

      <div class="type">
        <div class="type-wrap w1430">
          <div class="one warp js-sort" style="box-shadow: rgba(95, 101, 105, 0) 0px 12px 20px 0px; height: 54px; background: transparent;">
            <span class="name">分类：</span>
            <ul class="items clearfix">
              <li :class="!queryParams.categoryUid? `cur` : `` " >
                <a @click="getProductListMethod({categoryUid:''})" class="pointer">全部</a>
              </li>
              <li :class="queryParams.categoryUid === item.uid ? `cur` : `` " v-for="(item,index) in categoryList" :key="index" >
                <a @click="getProductListMethod({categoryUid:item.uid})" class="pointer">{{ item.name }}</a>
              </li>
            </ul>
          </div>

          <div class="two warp filter" style="box-shadow: rgba(95, 101, 105, 0) 0px 12px 20px 0px; height: 54px; background: transparent;">
            <span class="name">排序：</span>
            <ul class="items clearfix">
              <li :class="queryParams.orderByDescColumn == `` ? `cur` : `` " >
                <a @click="getProductListMethod({orderByDescColumn:''})" class="on pointer">推荐</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `createTime` ? `cur` : `` " >
                <a @click="getProductListMethod({orderByDescColumn:'createTime'})" class="on pointer">最新</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `isSelection` ? `cur` : `` " >
                <a @click="getProductListMethod({orderByDescColumn:'isSelection'})" class="on pointer">精选</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `sort` ? `cur` : `` ">
                <a @click="getProductListMethod({orderByDescColumn:'sort'})" class="on pointer">评分</a>
              </li>
            </ul>
          </div>

        </div>
      </div>
    </div>
    <!--  搜索框end -->

    <!--  列表start -->
    <div class="main" style="min-height: 590px">
      <el-empty v-if="productList.length === 0" description="空空如也" image=""></el-empty>


      <ul class="course-list clearfix">

        <li class="course-card up" data-cid="419" data-typestr="商品" v-for="product in productList" :key="product.uid" :data-name="product.title" data-position="">
          <!--无优惠-->
          <div class="product" @click="buyProduct(product)" style="cursor: pointer">

            <div v-if="product.isTop === 1"  style="position: absolute; right: 0px; font-size:15px;">
              <el-tooltip class="item" effect="dark" content="置顶" placement="top">
                <img style="width: 30px; height: 30px" src="../assets/img/top.png" />
              </el-tooltip>
            </div>

            <div class="img" :style="`background-image: url(`+ product.coverPhotoUrl + `)`"></div>
            <p class="title">
              {{ product.name }}
            </p>
            <p class="one">
              <span>{{product.summary}}</span>
            </p>

            <div style="text-align: center" >
              <el-tag effect="dark" type="warning" size="small" hit v-if="product.isSelection === 1">精选</el-tag>
              <el-tag :type="colorList[product.commonCategory.sort % 5]" effect="plain" size="small" hit>{{product.commonCategory.name}}</el-tag>
            </div>

            <div  style="text-align: center; margin-top: 5px;" >

              <span class="price">{{formatPrice(product)}}</span>

              <span class="discountPrice" v-if="product.chargeType === 3">
                  <span v-if="product.payType === 1">
                    {{product.price }} 积分
                  </span>
                  <span v-if="product.payType === 2">
                      ¥ {{ product.price / 100 }}
                  </span>
                </span>
            </div>

          </div>
        </li>

      </ul>
    </div>
    <!--  列表end -->

    <!--支付窗口组件-->
    <PayWindow v-if="showPayWindow" :product="productVO" resourceType="12" @payCallback="payCallback"></PayWindow>
  </article>

</template>

<script>
import {getProductList, getCommonCategoryList} from "../api/mall";
import PayWindow from "../components/PayWindow"

export default {
  data() {
    return {
      productList: [], // 商品列表
      categoryList: [], // 分类列表
      showPayWindow: false,
      productVO: {},
      userInfo: {}, // 用户信息
      minHeight: window.screen.height - 273,
      currentPage: 1,
      pageSize: 20,
      colorList: ["primary", "info", "success", "warning", "danger"],
      queryParams: {
        keyword: "",
        currentPage: 1,
        pageSize: 12,
        categoryUid: '',
        orderByDescColumn: '',
      }
    }
  },
  watch: {
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.userInfo = this.$store.state.user.userInfo
    },
  },
  created() {
    this.userInfo = this.$store.state.user.userInfo
    this.getProductListMethod({})
    this.getProductCategoryListMethod()
  },
  components: {
    PayWindow
  },
  methods: {
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
    buyProduct(product) {
      this.$router.push({ path: "/mall/" + product.uid });
      // this.productVO = this.$commonUtil.convertProduct(product)
      // this.showPayWindow = true
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
    getProductCategoryListMethod() {
      let params = {}
      params.currentPage = 1
      params.pageSize = 100
      getCommonCategoryList(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          this.categoryList = response.data.records
        }
      })
    },
    getProductListMethod(queryParams) {
      this.queryParams.orderByDescColumn = queryParams.orderByDescColumn
      this.queryParams.categoryUid = queryParams.categoryUid
      let params = {}
      params.currentPage = this.currentPage
      params.pageSize = this.pageSize
      params.orderByDescColumn = queryParams.orderByDescColumn
      params.categoryUid = queryParams.categoryUid
      getProductList(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          this.productList = response.data.records
        }
      })
    },
  },
}
</script>

<style scoped>
article {
  width: 100%;
  margin-top: 40px;
  overflow: hidden
}


body, html {
  color: #1c1f21;
  font: 14px/1.5 "PingFang SC","微软雅黑","Microsoft YaHei",Helvetica,"Helvetica Neue",Tahoma,Arial,sans-serif;
  background-color: #fff;
  min-width: 1200px;
  overflow-y: auto;
  position: relative;
  padding-top: 0;
  -webkit-transition: -webkit-transform .3s ease;
  transition: transform .3s ease;
}

ol, ul {
  list-style: none;
  list-style-position: initial;
  list-style-image: initial;
  list-style-type: none;
}

.red {
  color: #f01414;
}
.bold {
  font-weight: 700;
}

.l {
  float: left;
}
.r {
  float: right;
}

li {
  display: list-item;
  text-align: -webkit-match-parent;
}

.main {
  width: 1630px;
  margin: auto;
}

.main .course-list {
  margin: 0 40px 40px 40px;
  padding: 0 20px;
}

.main .course-list .course-card .img {
  height: 152px;
  background: no-repeat center/cover;
  margin-bottom: 8px;
  border-radius: 8px 8px 0 0;
  overflow: hidden;
}


.main .course-list .course-card {
  position: relative;
  width: 270px;
  height: 270px;
  float: left;
  margin: 0 20px 20px 0;
  box-shadow: 0 4px 8px 0 rgb(95 101 105 / 5%);
  border-radius: 8px;
  background-color: #fff;
  transition: transform .2s, box-shadow .2s;
}

.main .course-list .course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px 0 rgba(95,101,105,.15);
}


.main .course-list .course-card .title {
  color: #545c63;
  line-height: 20px;
  text-align: center;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 700;
  color: rgba(12, 24, 36, 1);
  padding: 0 8px;
}


.main .course-list .course-card .one .discount i.name, .main .course-list .course-card .two .discount i.name {
  color: #fff;
  background-color: rgba(242,13,13,.6);
}


.main .course-list .course-card .one, .main .course-list .course-card .two {
  font-size: 12px;
  color: #9199a1;
  line-height: 18px;
  padding: 0 8px;
  margin-bottom: 8px;
  text-align: center;
}

.main .course-list .course-card .one .price, .main .course-list .course-card .two .price {
  line-height: 20px;
  margin-right: 2px;
}

.main .course-list .course-card .one .add-shop-cart.add-shop-cart, .main .course-list .course-card .one .add-shop-cart.stared, .main .course-list .course-card .one .star.add-shop-cart, .main .course-list .course-card .one .star.stared, .main .course-list .course-card .two .add-shop-cart.add-shop-cart, .main .course-list .course-card .two .add-shop-cart.stared, .main .course-list .course-card .two .star.add-shop-cart, .main .course-list .course-card .two .star.stared {
  color: #ff655d;
}


.main .course-list .course-card .one .origin-price, .main .course-list .course-card .two .origin-price {
  color: #6d7278;
  line-height: 20px;
}



.main .course-list .course-card .one .add-shop-cart, .main .course-list .course-card .one .star, .main .course-list .course-card .two .add-shop-cart, .main .course-list .course-card .two .star {
  cursor: pointer;
  display: -webkit-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  -webkit-align-items: center;
  align-items: center;
}

.main .course-list .course-card .one .look-details, .main .course-list .course-card .two .look-details {
  font-size: 12px;
  color: #ff655d;
  line-height: 18px;
}


.top-wrap {
  background-color: #f5f7fa;

  background-repeat: no-repeat;
  background-position: top center;
  background-size: cover;
}
.shizhan-header .shizhan-header-wrap {
  height: 100%;
  display: -webkit-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  -webkit-align-items: center;
  align-items: center;
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  -webkit-justify-content: space-between;
  justify-content: space-between;
  padding-top: 8px;
}

.h100 {
  height: 100%;
}

.shizhan-header .shizhan-header-wrap .banner {
  display: -webkit-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  -webkit-align-items: center;
  align-items: center;
}

.shizhan-header .shizhan-header-wrap .banner .title {
  height: 46px;
  margin-right: 8px;
}

#codingIndexBanner {
  color: #666666;
  font-size: 13px;
  height: 46px;
  line-height: 46px;
  min-width: 48px;
  max-width: 360px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  z-index: 899;
  display: none;
}


.shizhan-header .shizhan-header-wrap .shizhan-header-search {
  position: relative;
  width: 320px;
}

.type .type-wrap {
  position: relative;
  height: 129px;
}
.w1430 {
  width: 1430px;
  margin: 0 auto;
}

.type .type-wrap .warp {
  display: -webkit-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  position: absolute;
  width: 1430px;
  height: 54px;
  overflow: hidden;
  padding: 10px;
  box-sizing: border-box;
  box-shadow: 0 12px 20px 0 rgb(95 101 105 / 0%);
  border-radius: 8px;
  transition: all .2s;
}
.type .type-wrap .warp .name {
  width: 3em;
  color: #07111b;
  line-height: 32px;
  font-weight: 700;
  margin-right: 6px;
}

.type .type-wrap .warp .items {
  width: 0;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  -webkit-flex: 1;
  flex: 1;
}

.type .type-wrap .warp .items li.cur {
  background-color: rgba(233,142,70,.1);
}
.type .type-wrap .warp .items li {
  float: left;
  line-height: 16px;
  padding: 8px;
  border-radius: 6px;
  margin: 0 12px 12px 0;
}

.type .type-wrap .warp.two {
  top: 35px;
  z-index: 2;
}

.type .filter {
  margin: 20px 0;
}

.product .price {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0px;
  line-height: 24px;
  color: rgba(244, 78, 75, 1);
}

.product .discountPrice {
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 0px;
  line-height: 19px;
  color: rgba(153, 153, 153, 1);
  text-decoration: line-through;
}

@media screen and (min-width: 960px) and (max-width: 1199px) {
  .w1430 {
    width:  100%;
    margin: 0 auto;
  }
}
</style>
