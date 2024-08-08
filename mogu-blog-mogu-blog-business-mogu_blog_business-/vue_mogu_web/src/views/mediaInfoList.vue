<template>
  <article>
   <!--  搜索框start -->
    <div class="top-wrap">
      <div class="shizhan-header">
        <div class="shizhan-header-wrap w1430">
          <div class="banner">
            <a class="title" href="/">
              <img class="h100" src="../../static/images/media.png" alt="">
            </a>
            <div style="display: block; font-size: 20px;font-weight: 800;color: #000;">实战课程</div>
            <div id="codingIndexBanner" style="display: block; margin-left: 10px;">真实项目，实战演练</div>
          </div>
          <div id="szHeaderSearch" class="shizhan-header-search">
            <el-input v-model="queryParams.keyword"  placeholder="搜索感兴趣的实战课程内容"  auto-complete="off" @keyup.enter.native="getList">
                <template slot="append">
                  <el-button type="primary" icon="el-icon-search" size="mini" @click="getList"></el-button>
                </template>
            </el-input>
          </div>
        </div>
      </div>

      <div class="type">
        <div class="type-wrap w1430">
          <div class="one warp js-direction" style="box-shadow: rgba(95, 101, 105, 0) 0px 12px 20px 0px; height: 54px; background: transparent;">
            <span class="name">标签：</span>
            <ul class="items clearfix">
              <li :class="!queryParams.tagId? `cur` : `` " >
                <a @click="queryParams.tagId = ``" class="pointer">全部</a>
              </li>
              <li :class="queryParams.tagId == item.uid ? `cur` : `` " v-for="(item,index) in tagOptions" :key="index" >
                <a  @click="queryListByTag(item.uid)"  class="pointer">{{ item.content }}</a>
              </li>
            </ul>
          </div>

          <div class="two warp js-sort" style="box-shadow: rgba(95, 101, 105, 0) 0px 12px 20px 0px; height: 54px; background: transparent;">
            <span class="name">分类：</span>
            <ul class="items clearfix">
              <li :class="!queryParams.categoryId? `cur` : `` " >
                <a @click="queryParams.categoryId = ``" class="pointer">全部</a>
              </li>
              <li :class="queryParams.categoryId == item.uid ? `cur` : `` " v-for="(item,index) in categoryOptions" :key="index" >
                <a @click="queryListByCategory(item.uid)" class="pointer">{{ item.name }}</a>
              </li>
            </ul>
          </div>


          <div class="three warp filter" style="box-shadow: rgba(95, 101, 105, 0) 0px 12px 20px 0px; height: 54px; background: transparent;">
            <span class="name">排序：</span>
            <ul class="items clearfix">
              <li :class="queryParams.orderByDescColumn == `` ? `cur` : `` " >
                <a @click="changeSort(``)" class="on pointer">推荐</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `createTime` ? `cur` : `` " >
                <a @click="changeSort(`createTime`)" class="on pointer">最新</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `clickCount` ? `cur` : `` " >
                <a @click="changeSort(`clickCount`)" class="on pointer">热门</a>
              </li>
              <li :class="queryParams.orderByDescColumn == `sort` ? `cur` : `` ">
                <a @click="changeSort(`sort`)" class="on pointer">评分</a>
              </li>
            </ul>

          </div>


        </div>
      </div>
    </div>
    <!--  搜索框end -->


    <!--  列表start -->
    <div class="main" style="min-height: 590px">
        <el-empty v-if="mediaInfoList.length == 0" description="空空如也" image=""></el-empty>
        <ul class="course-list clearfix">

          <li class="course-card up" data-cid="419" data-typestr="实战" v-for="mediaInfo in mediaInfoList" :key="mediaInfo.uid"  :data-name="mediaInfo.title" data-position="">
              <router-link :to="{path:'/video', query: {mediaInfoUid: mediaInfo.uid}}" tag="a">
                <div class="img" :style="`background-image: url(`+ mediaInfo.images + `)`"></div>
                <p class="title ellipsis2">{{ mediaInfo.title }}</p>
                <p class="one">
                  <!--实战-->
                  <span>{{ mediaInfo.categoryName }} ·  {{ tagFormat(tagOptions , mediaInfo.tagId)}}  · {{ mediaInfo.clickCount }}  人学习</span>
                </p>

                <!--无优惠-->
                <p class="two clearfix">
                  <span class="price l red bold" v-if="!mediaInfo.price">免费</span>
                  <span class="price l red bold" v-else>￥{{ mediaInfo.price }}</span>
                  <span class="add-shop-cart r"><i class="icon imv2-shopping-cart"></i>观看</span>
                </p>
            </router-link>
          </li>

        </ul>
      </div>
    <!--  列表end -->
  </article>
</template>
<script>
import {listTag} from "@/api/mediaTag";
import {listCategory} from "@/api/mediaCategory";
import {getListByDictTypeList} from "@/api/sysDictData";
import {getMediaInfoList} from "@/api/mediaInfo";

export default {
  data() {
    return {
      countryOptions: [],
      tagOptions: [],
      categoryOptions: [],
      timeOptions: [
        '2021',
        '2020',
        '2019',
        '2018',
        '2017',
        '2016',
        '2015',
        '2014',
        '2013',
        '2012',
        '2011',
        '2010'
      ],
      activeNames: ['1'], //折叠面板默认打开
      mediaInfoList: [],  //电影列表
      total: 0,
      queryParams: {
        keyword: "",
        currentPage: 1,
        pageSize: 12,
        images: null,
        title: null,
        mediaInfoType: null,
        country: '',
        tagId: null,
        description: null,
        publishBy: null,
        publishTime: null,
        status: null,
        categoryId: '',
        orderByDescColumn: '',
        publishYear: ''
      }
    }
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      const dictTypeList = ['mediaInfo_country', 'mediaInfo_status', 'mediaInfo_type'];
      getListByDictTypeList(dictTypeList).then(response => {
        this.countryOptions = response.data.mediaInfo_country;
      });
      listCategory({status: '1', currentPage: 1, pageSize: 100}).then(response => {
        this.categoryOptions = response.data.records;
      });
      listTag({status: '1', currentPage: 1, pageSize: 100}).then(response => {
        this.tagOptions = response.data.records;
      });
      this.getList();
    },
    changeSort(column) {
      this.queryParams.orderByDescColumn = column
      this.getList()
    },
    actorFormatter(actorList) {
      if (!actorList || actorList.length === 0) {
        return '无';
      }
      const currentSeparator = ",";
      let actions = [];
      for (let index = 0; index < actorList.length; index++) {
        actions.push(actorList[index].name + currentSeparator);
      }
      return actions.join('').substring(0, actions.join('').length - 1);
    },
    /** 标签翻译 */
    tagFormat(tagOptions, tagId) {
      if (!tagId || !tagOptions) {
        return ''
      }
      const currentSeparator = ",";
      let actions = [];
      let tempArr = tagId.split(currentSeparator);
      for (let i = 0; i < tempArr.length; i++) {
        for (let j = 0; j < tagOptions.length; j++) {
          if (tagOptions[j].uid == ('' + tempArr[i])) {
            actions.push(tagOptions[j].content + '/');
            break;
          }
        }
      }
      return actions.join('').substring(0, actions.join('').length - 1);
    },
    //1 查询第一页数据
    getList() {
      getMediaInfoList(this.queryParams).then(response => {
        this.mediaInfoList = response.data.records;
        this.total = response.total;
      })
    },
    toDetail(uid) {
      window.location.href = 'mediaInfo' + uid;
    },
    queryListByTag(tagId){
      this.queryParams.tagId = tagId
      this.getList()
    },

    queryListByCategory(categoryId){
      this.queryParams.categoryId = categoryId
      this.getList()
    }
  }
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


.main .course-list .course-card .title.ellipsis2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.main .course-list .course-card .title {
  color: #545c63;
  line-height: 20px;
  height: 40px;
  margin-bottom: 8px;
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
.delete-line {
  text-decoration: line-through;
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

.shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner {
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
  margin: 17px 0 7px;
  border: 1px solid rgba(84,92,99,.2);
}

.shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner .shizhan-search-input {
  width: 286px;
  font-size: 12px;
  color: #93999f;
  line-height: 24px;
  padding: 5px 12px;
  border: none;
  border-radius: 0;
  box-sizing: border-box;
  background: 0 0;
}

.shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner .shizhan-search-button {
  width: 34px;
  height: 34px;
  font-size: 18px;
  text-align: center;
  line-height: 34px;
  color: #fff;
  background: #545c63;
  cursor: pointer;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
  float: right;
}

.type {
  padding-bottom: 27px;
}

.type .type-wrap {
  position: relative;
  height: 129px;
}
.w1430 {
  width: 1430px;
  margin: 0 auto;
}


.type .type-wrap .warp.one {
  margin-bottom: 25px;
  z-index: 3;
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
  top: 52px;
  z-index: 2;
}

.type .type-wrap .warp.three {
  top: 81px;
  z-index: 2;
}

.type .filter {
  margin: 20px 0;
}


@media screen and (min-width: 960px) and (max-width: 1199px) {
  .mediaList {
    width: 100%;
    margin-top: 60px;
    overflow: hidden;
  }

  .w1430 {
    width:  100%;
    margin: 0 auto;
  }
}

@media screen and (min-width: 380px) and (max-width: 768px) {
/*  article {
    width: 96%;
    margin-top: 60px;
  }*/

  .w1430 {
    width:  100%;
    margin: 0 auto;
  }

  .main .course-list {
    max-width: 500px;
    margin: 0 40px 10px 0;
    padding: 0 10px;
  }

  .main .course-list .course-card .img {
    height: 105px;
    background: no-repeat center/cover;
    margin-bottom: 8px;
    border-radius: 8px 8px 0 0;
    overflow: hidden;
  }


  .main .course-list .course-card {
    position: relative;
    width: 172px;
    height: 174px;
    float: left;
    margin: 0 10px 10px 0;
    box-shadow: 0 4px 8px 0 rgb(95 101 105 / 5%);
    border-radius: 8px;
    background-color: #fff;
    transition: transform .2s, box-shadow .2s;
  }

  .main .course-list .course-card .title.ellipsis2 {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .main .course-list .course-card .title {
    color: #545c63;
    line-height: 20px;
    height: 20px;
    margin-bottom: 4px;
    padding: 0 4px;
  }


  .main .course-list .course-card .one .discount i.name, .main .course-list .course-card .two .discount i.name {
    color: #fff;
    background-color: rgba(242,13,13,.6);
  }


  .main .course-list .course-card .one, .main .course-list .course-card .two {
    font-size: 6px;
    color: #9199a1;
    line-height: 13px;
    padding: 0 4px;
    margin-bottom: 4px;
  }

  .main .course-list .course-card .one .price, .main .course-list .course-card .two .price {
    line-height: 13px;
    margin-right: 2px;
  }

  .main .course-list .course-card .one .add-shop-cart.add-shop-cart, .main .course-list .course-card .one .add-shop-cart.stared, .main .course-list .course-card .one .star.add-shop-cart, .main .course-list .course-card .one .star.stared, .main .course-list .course-card .two .add-shop-cart.add-shop-cart, .main .course-list .course-card .two .add-shop-cart.stared, .main .course-list .course-card .two .star.add-shop-cart, .main .course-list .course-card .two .star.stared {
    color: #ff655d;
  }


  .main .course-list .course-card .one .origin-price, .main .course-list .course-card .two .origin-price {
    color: #6d7278;
    line-height: 13px;
  }
  .delete-line {
    text-decoration: line-through;
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
    font-size: 15px;
    color: #ff655d;
    line-height: 22px;
  }


  .shizhan-header .shizhan-header-wrap .shizhan-header-search {
    position: relative;
    width: 80%;
    margin-top: 60px;
  }

  .shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner {
    width: 100%;
    border-radius: 4px;
    overflow: hidden;
    margin: 8px 0 4px;
    border: 1px solid rgba(84,92,99,.2);
  }

  .shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner .shizhan-search-input {
    width: 100px;
    font-size: 9px;
    color: #93999f;
    line-height: 22px;
    padding: 4px 7px;
    border: none;
    border-radius: 0;
    box-sizing: border-box;
    background: 0 0;
  }

  .shizhan-header .shizhan-header-wrap .shizhan-header-search .search-inner .shizhan-search-button {
    font-size: 20px;
    text-align: center;
    line-height: 20px;
    color: #fff;
    background: #545c63;
    cursor: pointer;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    float: right;
  }


}




</style>
