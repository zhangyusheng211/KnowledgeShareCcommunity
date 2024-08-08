<template>
<div>

  <div  style=" padding-top: 110px; text-align: center;">

    <div style=" border: 1px solid rgba(255, 255, 255, 0.1);">

      <div>
        <el-input placeholder="请输入内容" style="width: 800px; height: 50px" v-model="keywords" autofocus @keyup.enter.native="onSearch"  clearable>
          <template slot="prepend">

            <svg-icon :svg-name="selectIcon.icon" />
            &nbsp;
            <svg-icon @click.native="dialogVisible = true" style="width: 12px; cursor: pointer" svg-name="sjx" />

          </template>
          <template slot="append">
            <i style="cursor: pointer" class="el-icon-search" @click="onSearch"></i>
          </template>
        </el-input>
      </div>

    </div>
  </div>

  <div class="article-list-content" style="padding-top: 50px">
    <div class="layout-left">
      <div style="height: 20px"></div>
      <div class="ques-section">
        <!-- 1 -->
        <div class="ques-section-item">
          <div class="ques-section-card">
            <div class="ques-card-head ques-card-red">
              <svg>
                <defs>
                  <pattern
                    id="water"
                    width=".25"
                    height="1.1"
                    patternContentUnits="objectBoundingBox"
                  >
                    <path
                      fill="#fff"
                      d="M0.25,1H0c0,0,0-0.659,0-0.916c0.083-0.303,0.158,0.334,0.25,0C0.25,0.327,0.25,1,0.25,1z"
                    ></path>
                  </pattern>
                  <g id="eff">
                    <rect
                      fill="url(#water)"
                      x="-119.461"
                      y="0"
                      width="1200"
                      height="120"
                      opacity="0.3"
                    >
                      <animate
                        attributeType="xml"
                        attributeName="x"
                        from="-300"
                        to="0"
                        repeatCount="indefinite"
                        dur="10s"
                      ></animate>
                    </rect>
                    <rect
                      fill="url(#water)"
                      y="5"
                      width="1600"
                      height="125"
                      opacity="0.3"
                      x="-399.447"
                    >
                      <animate
                        attributeType="xml"
                        attributeName="x"
                        from="-400"
                        to="0"
                        repeatCount="indefinite"
                        dur="13s"
                      ></animate>
                    </rect>
                  </g>
                </defs>
                <use
                  xlink:href="#eff"
                  opacity="1"
                  style="mix-blend-mode: normal"
                ></use>
              </svg>
            </div>
            <div class="ques-card-title ques-card-title-top">
              微博热搜
            </div>
            <ul class="ques-card-list">
              <a
                :href="item.url"
                target="_blank"
                v-for="(item, index) in leaderAll"
                :key="index"
              >
                <li>
                  <div class="ques-list-box">

                    <span :class="
                                index == '0'
                                ? 'ques-list-name-icon-1'
                                : index == '1'
                                ? 'ques-list-name-icon-2'
                                : index == '2'
                                ? 'ques-list-name-icon-3'
                                : 'ques-list-name-icon-4'
                                ">{{ index + 1 }}</span>
                    <span
                      :id="
                                  index == '0'
                                    ? 'iconfontColor1'
                                    : index == '1'
                                    ? 'iconfontColor2'
                                    : index == '2'
                                    ? 'iconfontColor3'
                                    : 'iconfontColor4'
                                "
                      class="iconfontHotSearch icon-paihangbang2"
                    ></span>

                    <div class="ques-list-name">
                      <div class="ques-list-name-head">
                        {{ item.nickName }}
                      </div>
                      <div class="ques-list-name-text">
                        {{ item.keyword }}
                      </div>
                    </div>
                  </div>
                </li>
              </a>
            </ul>
          </div>
        </div>


        <!-- 2 -->
        <div class="ques-section-item">
          <div class="ques-section-card">
            <div class="ques-card-head ques-card-bul2">
              <svg>
                <use
                  xlink:href="#eff"
                  opacity="1"
                  style="mix-blend-mode: normal"
                ></use>
              </svg>
            </div>
            <div class="ques-card-title ques-card-title-top">
              百度热搜
            </div>
            <ul class="ques-card-list">
              <a
                v-for="(item, index) in leaderMonth"
                :key="index"
                :href="item.url"
                target="_blank"
              >
                <li>
                  <div class="ques-list-box">
                    <div class="ques-list-name">

                      <span :class="
                                index == '0'
                                ? 'ques-list-name-icon-1'
                                : index == '1'
                                ? 'ques-list-name-icon-2'
                                : index == '2'
                                ? 'ques-list-name-icon-3'
                                : 'ques-list-name-icon-4'
                                ">{{ index + 1 }}</span>
                      <span
                        :id="
                                  index == '0'
                                    ? 'iconfontColor1'
                                    : index == '1'
                                    ? 'iconfontColor2'
                                    : index == '2'
                                    ? 'iconfontColor3'
                                    : 'iconfontColor4'
                                "
                        class="iconfontHotSearch icon-paihangbang2"
                      ></span>

                      <div class="ques-list-name-text">
                        {{ item.keyword }}
                      </div>

                    </div>
                  </div>
                </li>
              </a>
            </ul>
          </div>
        </div>

        <!-- 3 -->
        <div class="ques-section-item">
          <div class="ques-section-card">
            <div class="ques-card-head ques-card-bul">
              <svg>
                <use
                  xlink:href="#eff"
                  opacity="1"
                  style="mix-blend-mode: normal"
                ></use>
              </svg>
            </div>
            <div class="ques-card-title ques-card-title-top">
              知乎热搜
            </div>
            <ul class="ques-card-list">
              <a
                v-for="(item, index) in leaderWeek"
                @click="getUserCenter(item.user.uid, 1)"
                :href="item.url"
                target="_blank"
              >
                <li>
                  <div class="ques-list-box">

                    <div class="ques-list-name">
                      <span :class="
                                index == '0'
                                ? 'ques-list-name-icon-1'
                                : index == '1'
                                ? 'ques-list-name-icon-2'
                                : index == '2'
                                ? 'ques-list-name-icon-3'
                                : 'ques-list-name-icon-4'
                                ">{{ index + 1 }}</span>
                      <span
                        :id="
                                  index == '0'
                                    ? 'iconfontColor1'
                                    : index == '1'
                                    ? 'iconfontColor2'
                                    : index == '2'
                                    ? 'iconfontColor3'
                                    : 'iconfontColor4'
                                "
                        class="iconfontHotSearch icon-paihangbang2"
                      ></span>

                      <div class="ques-list-name-text">
                        {{ item.keyword }}
                      </div>
                    </div>
                  </div>
                </li>
              </a>
            </ul>
          </div>
        </div>

        <!-- 4 -->
        <div class="ques-section-item">
          <div class="ques-section-card">
            <div class="ques-card-head ques-card-gul">
              <svg>
                <defs>
                  <pattern
                    id="water"
                    width=".25"
                    height="1.1"
                    patternContentUnits="objectBoundingBox"
                  >
                    <path
                      fill="#fff"
                      d="M0.25,1H0c0,0,0-0.659,0-0.916c0.083-0.303,0.158,0.334,0.25,0C0.25,0.327,0.25,1,0.25,1z"
                    ></path>
                  </pattern>
                  <g id="eff">
                    <rect
                      fill="url(#water)"
                      x="-119.461"
                      y="0"
                      width="1200"
                      height="120"
                      opacity="0.3"
                    >
                      <animate
                        attributeType="xml"
                        attributeName="x"
                        from="-300"
                        to="0"
                        repeatCount="indefinite"
                        dur="10s"
                      ></animate>
                    </rect>
                    <rect
                      fill="url(#water)"
                      y="5"
                      width="1600"
                      height="125"
                      opacity="0.3"
                      x="-399.447"
                    >
                      <animate
                        attributeType="xml"
                        attributeName="x"
                        from="-400"
                        to="0"
                        repeatCount="indefinite"
                        dur="13s"
                      ></animate>
                    </rect>
                  </g>
                </defs>
                <use
                  xlink:href="#eff"
                  opacity="1"
                  style="mix-blend-mode: normal"
                ></use>
              </svg>
            </div>
            <div class="ques-card-title ques-card-title-top">
              CSDN热搜
            </div>
            <ul class="ques-card-list">
              <a
                :href="item.url"
                target="_blank"
                v-for="(item, index) in leaderBlog"
                :key="index"
              >
                <li>
                  <div class="ques-list-box">
                    <div class="ques-list-name">
                      <span :class="
                                index == '0'
                                ? 'ques-list-name-icon-1'
                                : index == '1'
                                ? 'ques-list-name-icon-2'
                                : index == '2'
                                ? 'ques-list-name-icon-3'
                                : 'ques-list-name-icon-4'
                                ">{{ index + 1 }}</span>
                      <span
                        :id="
                                  index == '0'
                                    ? 'iconfontColor1'
                                    : index == '1'
                                    ? 'iconfontColor2'
                                    : index == '2'
                                    ? 'iconfontColor3'
                                    : 'iconfontColor4'
                                "
                        class="iconfontHotSearch icon-paihangbang2"
                      ></span>

                      <div class="ques-list-name-text">
                        {{ item.keyword }}
                      </div>

                    </div>
                  </div>
                </li>
              </a>
            </ul>
          </div>
        </div>
        <!-- 5 -->
        <div class="ques-section-item">
          <div class="ques-section-card">
            <div class="ques-card-head ques-card-org">
              <svg>
                <use
                  xlink:href="#eff"
                  opacity="1"
                  style="mix-blend-mode: normal"
                ></use>
              </svg>
            </div>
            <div class="ques-card-title ques-card-title-top">
              头条热搜
            </div>
            <ul class="ques-card-list">
              <a
                v-for="(item, index) in leaderMoment"
                :key="index"
                :href="item.url"
                target="_blank"
              >
                <li>
                  <div class="ques-list-box">
                    <div class="ques-list-name">

                      <span :class="
                                index == '0'
                                ? 'ques-list-name-icon-1'
                                : index == '1'
                                ? 'ques-list-name-icon-2'
                                : index == '2'
                                ? 'ques-list-name-icon-3'
                                : 'ques-list-name-icon-4'
                                ">{{ index + 1 }}</span>
                      <span
                        :id="
                                  index == '0'
                                    ? 'iconfontColor1'
                                    : index == '1'
                                    ? 'iconfontColor2'
                                    : index == '2'
                                    ? 'iconfontColor3'
                                    : 'iconfontColor4'
                                "
                        class="iconfontHotSearch icon-paihangbang2"
                      ></span>

                      <div class="ques-list-name-text">
                        {{ item.keyword }}
                      </div>



                    </div>
                  </div>
                </li>
              </a>
            </ul>
          </div>
        </div>

      </div>
    </div>
  </div>

  <!--选择搜索框-->
  <el-dialog
    title="切换搜索引擎"
    :visible.sync="dialogVisible"
    width="30%">

    <el-radio-group v-model="searchRadio">
      <el-radio-button label="0" value="1">百度</el-radio-button>
      <el-radio-button label="1">Google</el-radio-button>
      <el-radio-button label="2">Github</el-radio-button>
      <el-radio-button label="3">StackOverflow</el-radio-button>
      <el-radio-button label="4">码云</el-radio-button>
      <el-radio-button label="5">哔哩哔哩</el-radio-button>
      <el-radio-button label="6">知乎</el-radio-button>
    </el-radio-group>


    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="onSubmit()">确 定</el-button>
  </span>
  </el-dialog>


</div>
</template>

<script>
import axios from 'axios'
import {getOutsideHotSearch} from "../api/hotSearch";
import {getCookie, setCookie} from "../utils/cookieUtils";
import {recorderBehavior} from "../api";
export default {
  name: "rank",
  props: ["visible"],
  data () {
    return {
      leaderAll: [],
      leaderMonth: [],
      leaderWeek: [],
      leaderBlog: [],
      leaderMoment: [],
      dialogVisible: false,

      title: "排行榜",
      searchRadio: "1",
      selectIcon: {
        "icon": "",
        "url": ""
      },
      keywords: "",
      iconList: [
        {
          "name": "百度",
          "icon": "baidu",
          "url": "https://www.baidu.com/s?&wd="
        },
        {
          "name": "Google",
          "icon": "google",
          "url": "https://www.google.com/search?q="
        },
        {
          "name": "Github",
          "icon": "github",
          "url": "https://github.com/search?q="
        },
        {
          "name": "Stackoverflow",
          "icon": "stackoverflow",
          "url": "https://stackoverflow.com/nocaptcha?s="
        },
        {
          "name": "码云",
          "icon": "gitee",
          "url": "https://search.gitee.com/?skin=rec&type=repository&q="
        },
        {
          "name": "哔哩哔哩",
          "icon": "bilibili",
          "url": "https://search.bilibili.com/all?keyword="
        },
        {
          "name": "知乎",
          "icon": "zhihu",
          "url": "https://www.zhihu.com/search?type=content&q="
        },
        {
          "name": "必应",
          "icon": "bing",
          "url": "https://www.bing.com/search?q="
        },
        {
          "name": "搜狗",
          "icon": "sougou",
          "url": "https://www.sogou.com/sogou?query="
        },
      ],
    }
  },
  created() {
    setTimeout(()=>{
      recorderBehavior({"otherData": "热搜", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)
    this.getUserDefaultSelectIcon()
    this.getList()
  },
  mounted() {

  },
  components: {

  },
  watch: {

  },
  methods: {
    // 获取用户默认选择的Icon
    getUserDefaultSelectIcon: function () {
      // 从Cookie中获取
      let searchRadio = getCookie("searchRadio")
      // 如果存在
      if(searchRadio) {
        this.selectIcon = this.iconList[searchRadio]
      } else {
        // 如果不存在，默认百度
        this.selectIcon = this.iconList[0]
      }
    },
    onSearch: function () {
      // 判断是否输入内容
      if (this.keywords.length === 0) {
        this.$message.error("请输入搜索内容")
        return
      }
      recorderBehavior({"otherData": this.selectIcon.name + ": " + this.keywords, "behavior": "SUBMIT_OUTSIDE_SEARCH"}).then(response => {})

      window.open(this.selectIcon.url + this.keywords, "_blank")
    },
    onSubmit: function () {
      // 获取用户点击的搜索
      console.log("用户切换", this.searchRadio)
      let searchRadio = parseInt(this.searchRadio)
      this.selectIcon = this.iconList[parseInt(this.searchRadio)]
      console.log("选中的内容", this.selectIcon)
      // 设置用户的搜索引擎
      setCookie("searchRadio", searchRadio, 365)
      this.dialogVisible = false
      this.$message.success("切换成功")

      recorderBehavior({"otherData": this.selectIcon.name, "behavior": "CHANGE_SEARCH_TYPE"}).then(response => {})
    },
    getUserCenter: function (userUid, type) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: userUid, type: type}
      });
      window.open(routeData.href, '_blank');
    },
    // 关闭时的回调
    beforeClose(done) {
      this.$confirm("是否关闭排行榜窗口", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          //取消时，开始状态
          this.$emit("beforeClose", "");
          done();
        })
        .catch(() => {
          // this.$commonUtil.message.info("已取消")
        });
    },
    getList () {

      //积分排行总榜
      let params1 = new URLSearchParams()
      params1.append("type", "weibo")
      getOutsideHotSearch(params1).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderAll = response.data;
        }
      });

      //积分排行月榜
      let params2 = new URLSearchParams()
      params2.append("type", "baidu")
      getOutsideHotSearch(params2).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderMonth = response.data;
        }
      });

      //积分排行周榜
      let params3 = new URLSearchParams()
      params3.append("type", "zhihu")
      getOutsideHotSearch(params3).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderWeek = response.data;
        }
      });

      //动态排行榜
      let params4 = new URLSearchParams()
      params4.append("type", "toutiao")
      getOutsideHotSearch(params4).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderMoment = response.data;
        }
      });

      //博客排行榜
      let params5 = new URLSearchParams()
      params5.append("type", "csdn")
      getOutsideHotSearch(params5).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderBlog = response.data;
        }
      });

    },
  }
}
</script>

<style scoped>

/deep/ .el-input--suffix .el-input__inner {
  height: 50px;
}
/deep/ .particles-js-canvas-el {
  width: 1697px !important;
  height: 1697px  !important;
}
#chooseBtn {
  z-index: 0;
}
.wechat-list {
  position: fixed;
  bottom: 190px;
  right: 45px;
  overflow: auto;
}
.el-icon-refresh {
  font-size: 30px;
}
.lizi {
  position: fixed;
  z-index: 1;
}
.cloud ul a {
  background-color: transparent !important;
}
li {
  list-style: none;
}

.ques-section {
  margin: 0 auto;
}

.ques-section-item {
  width: 293px;
  position: relative;
  float: left;
}
.ques-section-card {
  position: relative;
  height: 860px;
  text-align: center;
  border: 1px solid #f2f2f2;
  border-radius: 2px;
}

.ques-card-head {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 30px;
  padding-top: 48px;
  border-radius: 2px 2px 0 0;
}

.ques-card-gul {
  background-color: #fc5632;
  background-image: -webkit-radial-gradient(50% 3%, circle, #f07a56, #fc5632);
  background-image: radial-gradient(circle at 50% 3%, #f07a56, #fc5632);
  height: 78px;
}

.ques-card-red {
  background-color: #d52c2b;
  background-image: -webkit-radial-gradient(50% 3%, circle, #d63736, #d52c2b);
  background-image: radial-gradient(circle at 50% 3%, #d63736, #d52c2b);
  height: 78px;
}

.ques-card-org {
  background-color: #f04142;
  background-image: -webkit-radial-gradient(50% -6%, circle, #f24e4f 2%, #f04142);
  background-image: radial-gradient(circle at 50% -6%, #f24e4f 2%, #f04142);
  height: 78px;
}

.ques-card-bul {
  background-color: #0066ff;
  background-image: -webkit-radial-gradient(50% -45%, circle, #53a7f5, #0066ff);
  background-image: radial-gradient(circle at 50% -45%, #53a7f5, #0066ff);
  height: 78px;
}

.ques-card-bul2 {
  background-color: #4e6ef2;
  background-image: -webkit-radial-gradient(50% -45%, circle, #53a7f5, #4e6ef2);
  background-image: radial-gradient(circle at 50% -45%, #53a7f5, #4e6ef2);
  height: 78px;
}



.ques-card-green {
  background-color: green;
  height: 78px;
}

.ques-card-title {
  font-size: 18px;
  font-weight: 500;
  line-height: 24px;
  position: relative;
  padding-top: 15px;
  color: #fff;
}

.ques-section-item+.ques-section-item {
  margin-left: 20px;
}

.ques-card-title-top {
  padding: 27px 0;
}
.ques-card-list-1:before {
  position: absolute;
  z-index: 2;
  top: 6px;
  left: 37px;
  display: block;
  content: '';
  background-position: -115px -44px;
  width: 20px;
  height: 20px;
  background-image: url('../assets/img/icon-body.png');
}

.ques-card-list-2:before {
  background-position: -115px -66px;
  width: 20px;
  height: 20px;
  position: absolute;
  z-index: 2;
  top: 6px;
  left: 37px;
  display: block;
  content: '';
  background-image: url('../assets/img/icon-body.png');
}

.ques-card-list-3:before {
  position: absolute;
  z-index: 2;
  top: 6px;
  left: 37px;
  display: block;
  content: '';
  background-position: -115px -88px;
  width: 20px;
  height: 20px;
  background-image: url('../assets/img/icon-body.png');
}

.ques-card-list {
  text-align: left;
  padding-top: 85px;
}

.ques-card-list li {
  position: relative;
}

.ques-card-list li:after {
  position: absolute;
  bottom: 0;
  left: 10px;
  display: block;
  width: 280px;
  height: 1px;
  content: '';
}

.ques-list-box {
  position: relative;
  display: block;
  height: 25px;
  width: 230px;
  padding: 16px 15px 17px;
}

.ques-list-head {
  position: relative;
  float: left;
  padding-right: 8px;
}

.ques-list-image {
  position: relative;
  display: block;
  width: 36px;
  height: 36px;
  z-index: 2;
  margin: 0 auto;
}

.ques-list-image:before {
  position: absolute;
  top: 0;
  left: 0;
  width: 34px;
  height: 34px;
  content: '';
  border: 2px solid #f87851;
  border-radius: 100%;
}

.ques-list-image img {
  border-radius: 100%;
  width: 100%;
  height: auto;
  display: block;
  border: none;
}

.ques-list-name {
  overflow: hidden;
  margin-left: 33px;
}

.ques-list-name-head {
  line-height: 18px;
  color: #333;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.ques-list-name-text {
  font-size: 12px;
  line-height: 16px;
  margin-top: 2px;
  color: #566573;
}

.iconfontHotSearch {
  font-size: 30px;
  position: absolute;
  top: 12px;
  right: 20px;
}

#iconfontColor1 {
  color: #FF6314;
}

#iconfontColor2 {
  color: #FFBD24;
}

#iconfontColor3 {
  color: #4C8DFC;
}

#iconfontColor4 {
  color: #CED2D7;
}

.font {
  position: absolute;
  top: 25px;
  right: 32px;
  color: #fff;
  z-index: 2;
}
.ques-list-name-icon-1 {
  font-size: 12px;
  line-height: 24px;
  position: absolute;
  top: 50%;
  left: 14px;
  display: block;
  margin-top: -13px;
  text-align: center;
  color: #fff;
  background-position: 125px 0;
  width: 25px;
  height: 25px;
  background-image: url('../assets/img/icon-body.png');
}
.ques-list-name-icon-2 {
  font-size: 12px;
  line-height: 24px;
  position: absolute;
  top: 50%;
  left: 14px;
  display: block;
  margin-top: -13px;
  text-align: center;
  color: #fff;
  background-position: 125px -27px;
  width: 25px;
  height: 25px;
  background-image: url('../assets/img/icon-body.png');
}
.ques-list-name-icon-3 {
  font-size: 12px;
  line-height: 24px;
  position: absolute;
  top: 50%;
  left: 14px;
  display: block;
  margin-top: -13px;
  text-align: center;
  color: #fff;
  background-position: 159px -32px;
  width: 25px;
  height: 25px;
  background-image: url('../assets/img/icon-body.png');
}

.ques-list-name-icon-4 {
  font-size: 12px;
  line-height: 24px;
  position: absolute;
  top: 50%;
  left: 14px;
  display: block;
  margin-top: -13px;
  text-align: center;
  color: #fff;
  background-position: 0 -60px;
  width: 25px;
  height: 25px;
  background-image: url('../assets/img/icon-body.png');
}
.ques-list-box:hover {
  cursor: pointer;
  background-color: #f5f5f5;
}

.article-list-content {
  width: 100%;
  min-height: calc(100vh - 110px);
  display:flex;
  justify-content: center;
  /*align-items:center;*/
}

@media only screen and (max-width: 768px) {

  .ques-section {
    width: 100%;
  }

  .ques-section-item, svg {
    margin: 0 auto;
    float: none;
    width: 90%;
    height: 770px;
    overflow: auto;
  }

  svg {
    width: 100%;
  }
}

.layout-left {
  text-align: center;
}

</style>

<style rel="stylesheet/scss" lang="scss" scoped>

$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.svg-container {
  padding: 6px 5px 6px 15px;
  color: $dark_gray;
  vertical-align: middle;
  width: 30px;
  display: inline-block;
  &_login {
    font-size: 20px;
  }
}

.title {
  font-size: 26px;
  font-weight: 400;
  color: $light_gray;
  margin: 0px auto 40px auto;
  text-align: center;
  font-weight: bold;
}
.show-pwd {
  position: absolute;
  right: 10px;
  top: 7px;
  font-size: 16px;
  color: $dark_gray;
  cursor: pointer;
  user-select: none;
}


</style>
