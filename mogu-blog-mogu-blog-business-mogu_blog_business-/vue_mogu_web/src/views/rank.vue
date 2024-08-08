<template>

      <div class="article-list-content" style="padding-top: 60px">
        <!--  刷新图标    -->
        <div id="wechats" class="wechat-list" @scroll="scrollChange">
          <div id="chooseBtn" class="el-icon-refresh pointer" @click="refreshRank">
          </div>
        </div>

        <div class="layout-left">
          <div style="height: 20px"></div>
          <div class="ques-section">
            <!-- 1 -->
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
                  积分排行总榜
                </div>
                <ul class="ques-card-list">
                  <a
                    @click="getUserCenter(item.uid, 1)"
                    v-for="(item, index) in leaderAll"
                    :key="index"
                  >
                    <li
                      :class="
                            index == '0'
                              ? 'ques-card-list-1'
                              : index == '1'
                              ? 'ques-card-list-2'
                              : 'ques-card-list-3'
                          "
                    >
                      <div class="ques-list-box">
                        <div class="ques-list-head">
                          <div class="ques-list-image">
                            <img v-if="item.photoUrl" :src="item.photoUrl" alt="" />
                            <img v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="" />
                          </div>
                        </div>
                        <div class="ques-list-name">
                          <div class="ques-list-name-head">
                            {{ item.nickName }}
                          </div>
                          <div class="ques-list-name-text">
                            积分数: {{ item.credits }}
                          </div>

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
                            class="iconfont icon-paihangbang2"
                          ></span>
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
                  积分月度排行榜
                </div>
                <ul class="ques-card-list">
                  <a
                    v-for="(item, index) in leaderMonth"
                    :key="index"
                    @click="getUserCenter(item.user.uid, 1)"
                  >
                    <li
                      :class="
                            index == '0'
                              ? 'ques-card-list-1'
                              : index == '1'
                              ? 'ques-card-list-2'
                              : 'ques-card-list-3'
                          "
                    >
                      <div class="ques-list-box">
                        <div class="ques-list-head">
                          <div class="ques-list-image">
                            <img v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl" alt="" />
                            <img v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="" />
                          </div>
                        </div>
                        <div class="ques-list-name">
                          <div class="ques-list-name-head" v-if="item.user && item.user.nickName">
                            {{ item.user.nickName }}
                          </div>
                          <div class="ques-list-name-text">
                            积分数: {{ item.sumCredits }}
                          </div>
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
                            class="iconfont icon-paihangbang2"
                          ></span>
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
                  积分周排行榜
                </div>
                <ul class="ques-card-list">
                  <a
                    v-for="(item, index) in leaderWeek"
                    @click="getUserCenter(item.user.uid, 1)"
                  >
                    <li
                      :class="
                            index == '0'
                              ? 'ques-card-list-1'
                              : index == '1'
                              ? 'ques-card-list-2'
                              : 'ques-card-list-3'
                          "
                    >
                      <div class="ques-list-box">
                        <div class="ques-list-head">
                          <div class="ques-list-image">
                            <img v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl" alt="" />
                            <img v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="" />
                          </div>
                        </div>
                        <div class="ques-list-name">
                          <div class="ques-list-name-head" v-if="item.user && item.user.nickName">
                            {{ item.user.nickName }}
                          </div>
                          <div class="ques-list-name-text">
                            积分数: {{ item.sumCredits }}
                          </div>
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
                            class="iconfont icon-paihangbang2"
                          ></span>
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
                  文章发表排行榜
                </div>
                <ul class="ques-card-list">
                  <a
                    @click="getUserCenter(item.user.uid, 1)"
                    v-for="(item, index) in leaderBlog"
                    :key="index"
                  >
                    <li
                      :class="
                            index == '0'
                              ? 'ques-card-list-1'
                              : index == '1'
                              ? 'ques-card-list-2'
                              : 'ques-card-list-3'
                          "
                    >
                      <div class="ques-list-box">
                        <div class="ques-list-head">
                          <div class="ques-list-image">
                            <img v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl" alt="" />
                            <img v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="" />
                          </div>
                        </div>
                        <div class="ques-list-name">
                          <div class="ques-list-name-head" v-if="item.user && item.user.nickName">
                            {{ item.user.nickName }}
                          </div>
                          <div class="ques-list-name-text">
                            文章数: {{ item.sumCredits }}
                          </div>
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
                                    ? 'ques-card-list-1'
                                    : index == '1'
                                    ? 'ques-card-list-2'
                                    : 'ques-card-list-3'
                                "
                            class="iconfont icon-paihangbang2"
                          ></span>
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
                  动态发布排行榜
                </div>
                <ul class="ques-card-list">
                  <a
                    v-for="(item, index) in leaderMoment"
                    :key="index"
                    @click="getUserCenter(item.user.uid, 1)"
                  >
                    <li
                      :class="
                            index == '0'
                              ? 'ques-card-list-1'
                              : index == '1'
                              ? 'ques-card-list-2'
                              : 'ques-card-list-3'
                          "
                    >
                      <div class="ques-list-box">
                        <div class="ques-list-head">
                          <div class="ques-list-image">
                            <img v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl" alt="" />
                            <img v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="" />
                          </div>
                        </div>
                        <div class="ques-list-name">
                          <div class="ques-list-name-head" v-if="item.user && item.user.nickName">
                            {{ item.user.nickName }}
                          </div>
                          <div class="ques-list-name-text">
                            动态数: {{ item.sumCredits }}
                          </div>
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
                            class="iconfont icon-paihangbang2"
                          ></span>
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
</template>

<script>
import {leaderBoardAll, leaderBoardMonth, leaderBoardWeek, getLeaderMoment, getLeaderBlog } from "@/api/creditsLog";
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
      dialogRankVisible: this.visible,
      title: "排行榜",
    }
  },
  created() {

    setTimeout(()=>{
      recorderBehavior({"otherData": "排行榜", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)

    this.getList(false)
  },
  mounted() {

  },
  components: {

  },
  watch: {
    visible: function () {
      this.dialogRankVisible = this.visible;
    }
  },
  methods: {
    // 刷新图标悬浮
    scrollChange () {
      let scw = document.getElementById('wechats').clientWidth
      let sw = document.getElementById('wechats').scrollWidth
      let sl = document.getElementById('wechats').scrollLeft
      if (sw > scw) {
        document.getElementById('chooseBtn').style.marginRight = -sl + 'px'
      } else {
        document.getElementById('chooseBtn').style.marginRight = 0 + 'px'
      }
    },
    refreshRank: function () {
      this.getList(true)
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
    getList (isRefresh) {
      console.log(isRefresh)
      //积分排行总榜
      leaderBoardAll(isRefresh).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderAll = response.data;
        }
      });
      //积分排行月榜
      leaderBoardMonth(isRefresh).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderMonth = response.data;
        }
      });
      //积分排行周榜
      leaderBoardWeek(isRefresh).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderWeek = response.data;
        }
      });
      //动态排行榜
      getLeaderMoment(isRefresh).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderMoment = response.data;
        }
      });
      //博客排行榜
      getLeaderBlog(isRefresh).then((response) => {
        if (response.code == this.$ECode.SUCCESS) {
          this.leaderBlog = response.data;
          if (isRefresh) {
            this.$message.success("更新成功")
          }
        }
      });
    },
  }
}
</script>

<style scoped>
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
  background-color: #f55727;
  background-image: -webkit-radial-gradient(50% 3%, circle, #f07a56, #f55727);
  background-image: radial-gradient(circle at 50% 3%, #f07a56, #f55727);
  height: 78px;
}

.ques-card-org {
  background-color: #f59b00;
  background-image: -webkit-radial-gradient(50% -6%, circle, #ffb83d 2%, #f59b00);
  background-image: radial-gradient(circle at 50% -6%, #ffb83d 2%, #f59b00);
  height: 78px;
}

.ques-card-bul {
  background-color: #397bee;
  background-image: -webkit-radial-gradient(50% -45%, circle, #53a7f5, #397bee);
  background-image: radial-gradient(circle at 50% -45%, #53a7f5, #397bee);
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
  margin-right: 33px;
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
  color: #a3a3a3;
}

.iconfont {
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
  right: 14px;
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
  right: 14px;
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
  right: 14px;
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
  right: 14px;
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
}

@media only screen and (max-width: 768px) {
  margin: 5px 5px 0 5px;

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

@media screen and (min-width: 768px) {
  margin: 10px 10px 0 10px;
}

@media screen and (min-width: 992px) {
  margin: 15px 35px 0 35px;
}

@media screen and (min-width: 1200px) {
  width: 1200px;
  margin: 15px auto 0;
  margin-bottom: 200px;
}

.layout-left, .layout-right {
  text-align: center;
}
@media only screen and (max-width: 768px) {
  padding: 0;
}

@media screen and (min-width: 768px) {
  padding: 0;
}

@media screen and (min-width: 992px) {
  padding: 0 10px;
}

@media screen and (min-width: 1200px) {
  padding: 0 10px;
}

</style>
