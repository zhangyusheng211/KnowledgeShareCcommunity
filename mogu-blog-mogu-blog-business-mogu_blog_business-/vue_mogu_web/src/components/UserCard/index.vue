<template>
  <div class="userCard" :style="getCardStyle(this.positionx,this.positiony)" style="position: absolute" @mouseenter="onEnterTd" @mouseleave="onLeaveTd">
    <el-card :body-style="{ padding: '20px' }" class="cardStyle">
      <div>
        <el-row>
          <el-col :span="5" style="text-align: center">
              <span :class="userData.userTag > 0 ?'vip-avatar':''">
                <el-avatar v-if="userData" :class="userData.userTag > 0 ?'vip-color':''" style="cursor: pointer;"
                           shape="circle" @click.native="getUserCenter(1)" :size="60" fit="fill"
                           :src="userData.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" :size="60" src="https://empty" shape="circle">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                      v-if="userData.userTag > 0">v</span>
              </span>

          </el-col>

          <el-col :span="18">
            <div v-if="userData" style="cursor: pointer">
              <el-row>
                <el-col>
                    <span @click="getUserCenter(1)" class="blackFont"
                          style="font-size: 16px;font-weight: 600; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                      {{ splitStr(userData.nickName, 8) }}
                    </span>
                  <span>
                      <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList"
                              :key="userTag.uid" v-if="userData.userTag == userTag.dictValue && userData.userTag != 0"
                              :type="userTag.listClass">{{ userTag.dictLabel }}</el-tag>
                  </span>

                  <!--                  <span class="lv" :class="'lv'+ userData.userLevel">-->
                  <!--                    Lv{{userData.userLevel}}-->
                  <!--                  </span>-->

                  <span class="iconfont" v-if="userData.gender=='1'"
                        style="margin-left: 1px; color: dodgerblue;  font-size: 16px;">&#xe641;</span>
                  <span class="iconfont" v-if="userData.gender=='2'"
                        style="margin-left: 1px; color: palevioletred;  font-size: 16px;">&#xe643;</span>

                  <span>
                    <img v-if="userLevelImage" :src="userLevelImage"
                         style="height: 20px; display: inline-block; vertical-align: top; margin-top: 3px;">
                  </span>

                  <span
                    @click="goLink('/rank')"
                    style="font-size: 13px; font-weight: 500; color: rgb(114, 119, 123);">排名：{{ userData.rank }}</span>

                  <el-tooltip class="item" effect="light" content="每隔10分钟自动更新，点击立即刷新" placement="left-end"
                              style="float: right; margin-top: -10px; margin-right: -15px">
                    <i class="el-icon-refresh pointer" @click="getUserInfo(true)"></i>
                  </el-tooltip>

                </el-col>
              </el-row>
            </div>

            <el-row v-if="userData"
                    style="font-size: 13px;color: #72777b; margin-top: 5px; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; ">
              {{ userData.summary ? userData.summary : '这家伙很懒，什么都没有留下' }}
            </el-row>
          </el-col>
        </el-row>

        <el-row style="font-size: 12px; margin-top: 5px; line-height: 26px; text-align: center">
          <el-col :span="4">
            <el-tooltip placement="top" effect="light">
              <div slot="content" placement="bottom">通过完成每日签到、任务、发布文章、问答、<br/>评论、动态、可获取经验值提升排名</div>
              <span>经验值</span>
            </el-tooltip>
          </el-col>
          <el-col :span="14">
            <el-progress :text-inside="true" :stroke-width="26" status="success" :percentage="percentage"
                         :format="valueFormat"></el-progress>
          </el-col>
          <el-col :span="6">
            <span>
                <span class="lv" :class="'lv'+ userData.userLevel" v-for="userLevel in userLevelDictList"
                      :key="userLevel.uid" v-if="userData.userLevel == userLevel.dictValue">
                          <el-tooltip placement="bottom" effect="light">
                            <div slot="content">距离解锁下个等级还需 {{userData.levelMaxCredits - userData.expValue }} 经验值</div>
                            <span>{{ userLevel.remark }}</span>
                          </el-tooltip>
               </span>

            </span>
          </el-col>
        </el-row>

        <!--用户-->
        <el-row :gutter="24" type="flex" justify="center" style="margin-top: 10px; min-height: 45px">

          <!--文章-->
          <el-col @click.native="getUserCenter('1')" :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
              <countTo :startVal='0' :endVal='userData.blogPublishCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">文章</div>
          </el-col>

          <!--评论-->
          <el-col :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
              <countTo :startVal='0' :endVal='userData.userCommentCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">评论</div>
          </el-col>

          <!--收藏-->
<!--          <el-col :span="4" style="text-align: center; cursor: pointer">-->
<!--            <div class="countStyle">-->
<!--              <countTo :startVal='0' :endVal='userData.userCollectCount' :duration='3000'></countTo>-->
<!--            </div>-->
<!--            <div class="countStyle2">收藏</div>-->
<!--          </el-col>-->

          <!-- 勋章 -->
          <el-col :span="4" style="text-align: center; cursor: pointer" @click.native="goMedalDetail">
            <div class="countStyle" >
              <countTo :startVal='0' :endVal='userData.userMedalCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">勋章</div>
          </el-col>

          <!-- 动态 -->
          <el-col @click.native="getUserCenter('9')" :span="4" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.userMomentCount' :duration='3000'></countTo>
              </div>
              <div class="countStyle2">动态</div>
            </el-row>
          </el-col>

          <!-- 粉丝 -->
          <el-col @click.native="getUserCenter('7')" :span="4" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.userFollowCount' :duration='3000'></countTo>
              </div>
              <div class="countStyle2">粉丝</div>
            </el-row>
          </el-col>

          <!-- 关注 -->
          <el-col @click.native="getUserCenter('8')" :span="4" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.userWatchCount' :duration='3000'></countTo>
              </div>
              <div class="countStyle2">关注</div>
            </el-row>
          </el-col>
        </el-row>

        <el-row style="margin-top: 5px;">
          <span style="float: left; font-size: 12px; line-height: 35px;">
            <el-tooltip placement="bottom" effect="light">
              <div slot="content">活跃社区解锁海量勋章</div>
              <span>获得勋章：</span>
            </el-tooltip>
          </span>

          <span v-if="userData.userMedalCount === 0" style="font-size: 12px; text-align: center; line-height: 35px; color: #a1a1a1; width: 100%; cursor: pointer;">
            <span @click="goLink('/medal/' + userData.uid)">暂未获得勋章</span>
          </span>

          <span v-if="userData.userMedalCount > 0 && index < 7"  v-for="(medal, index) in userData.userMedalList" :key="medal.uid">
            <el-tooltip class="item" effect="light" :content="medal.summary + '(' + timeFormat(medal.gainTime) + ')' + '[约'+ medal.gainProbability +'用户获取]'"
                        placement="bottom">
              <img @click="goMedalDetail" :src="medal.fileUrl" class="like-user-avatar">
            </el-tooltip>
          </span>
        </el-row>

        <el-row :gutter="24" type="flex" justify="center" style="margin-top: 10px" v-if="userData.uid != loginUserInfo.uid">
          <el-col :span="12" style="text-align: center; cursor: pointer">
            <el-row>
              <el-button style="width: 90%" v-if="userWatchStatus == 0" icon="el-icon-sugar" type="danger"
                         @click="watchOtherUser(userData.uid)" size="mini" round><span>&nbsp;关&nbsp;注&nbsp;</span>
              </el-button>
              <el-button style="width: 90%" v-if="userWatchStatus == 1" icon="el-icon-lightning" type="info"
                         @click="unWatchOtherUser(userData.uid)" size="mini" round>取消关注
              </el-button>
              <el-button style="width: 90%" v-if="userWatchStatus == 2" icon="el-icon-ship" type="success"
                         @click="unWatchOtherUser(userData.uid)" size="mini" round>互相关注
              </el-button>
            </el-row>
          </el-col>

          <el-col :span="12" style="text-align: center; cursor: pointer">
            <el-row>
              <el-button style="width: 90%" icon="el-icon-chat-dot-round" type="primary" @click="goChat(userData.uid)"
                         size="mini" round>&nbsp;私&nbsp;信&nbsp;
              </el-button>
            </el-row>
          </el-col>
        </el-row>

      </div>
    </el-card>

  </div>
</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData";
import {addUserWatch, checkCurrentUserWatch, deleteUserWatch, getUserByUid} from "../../api/about";
import {mapMutations} from "vuex";
import countTo from 'vue-count-to';
import UserInfo from '../UserInfo'
import {timeAgo} from "../../utils/webUtils";

export default {
  name: "UserCard",
  props: ["usrCard", "coorDinatex", "coorDinatey"],
  data() {
    return {
      userData: {
        blogPublishCount: 0,
        userMomentCount: 0,
        userFollowCount: 0,
        userWatchCount: 0
      },
      positionx: this.coorDinatex,
      positiony: this.coorDinatey,
      userLevelDictList: [],
      userTagDictList: [],
      userLevelImagesDictList: [],
      userWatchStatus: 0,// 当前用户关注状态
      loginUserInfo: {}, // 当前登录用户的信息
      currentPage: 1,
      userInfo: {},
      percentage: 0,
      userLevelImage: null,
      defaultAvatar: process.env.defaultAvatar, // 默认头像
    };
  },
  created() {
    console.log("初始化用户信息", this.usrCard)
    this.loginUserInfo = this.$store.state.user.userInfo
    this.userData = this.usrCard
    this.getUserInfo(false)
    this.getDictList()
  },
  mounted() {
    this.loginUserInfo = this.$store.state.user.userInfo
  },
  watch: {
    usrCard: function () {
      this.userData = this.usrCard
      let params = new URLSearchParams()
      params.append("userUid", this.usrCard.uid)
      getUserByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.userData = response.data
          this.getCheckCurrentUserWatch(this.userData.uid)
          this.getPercentage()
          this.getLvImage(this.userData.userLevel)
        }
      })
    },
    coorDinatex: function () {
      this.positionx = this.coorDinatex;
    },
    coorDinatey: function () {
      this.positiony = this.coorDinatey;
    },
  },
  components: {
    countTo,
    UserInfo,
  },
  methods: {
    ...mapMutations(["setLoginMessage"]),
    goLink(url) {
      console.log("跳转url", url)
      this.$router.push(url)
    },
    getUserInfo: function (refresh) {
      if (!this.usrCard.uid) {
        return
      }
      let params = new URLSearchParams()
      params.append("userUid", this.usrCard.uid)
      params.append("refresh", refresh)
      getUserByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.userData = response.data
          this.getCheckCurrentUserWatch(this.userData.uid)
          this.getPercentage()
          this.getLvImage(this.userData.userLevel)
        }
      })
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    timeFormat(gainTime) {
      if (gainTime) {
        return this.timeAgo(gainTime) + '获得'
      } else {
        return '暂未获得'
      }
    },
    goMedalDetail: function () {
      this.$router.push("/medal/" + this.usrCard.uid)
    },
    splitStr: (str, flagCount) => {
      if (str == null || str == '') {
        return ""
      } else if (str.length > flagCount) {
        return str.substring(0, flagCount)
      } else {
        return str
      }
    },
    getPercentage() {
      if (this.userData.expValue && this.userData.levelMaxCredits) {
        this.percentage = this.userData.expValue * 100 / this.userData.levelMaxCredits;
      }
    },
    valueFormat() {
      return '经验值：' + this.userData.expValue + "/" + this.userData.levelMaxCredits;
    },
    getLvImage(level) {
      this.userLevelImage = this.userLevelImagesDictList[level - 1].dictValue
    },
    // 鼠标移走
    onLeaveTd: function () {
      this.$emit("leave", true)
    },
    onEnterTd: function () {
      this.$emit("enter")
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_level', 'sys_user_tag', 'user_level_images']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userLevelDictList = dictMap.sys_user_level.list
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelImagesDictList = dictMap.user_level_images.list
        }
      });
    },
    // 关注用户
    watchOtherUser: function (uid) {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以关注哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.toUserUid = uid

      addUserWatch(params).then(response => {
        console.log("关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userInfo.isWatchUser = true
          this.userWatchListData = []
          this.currentPage = 1
          this.getCheckCurrentUserWatch(uid)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 调到聊天页
    goChat: function (uid) {
      this.$router.push({path: "/chat", query: {userUid: uid}});
    },
    // 取消关注用户
    unWatchOtherUser: function (toUid) {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以评论哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }

      let params = {}
      params.toUserUid = toUid
      deleteUserWatch(params).then(response => {
        console.log("取消关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userWatchListData = []
          this.currentPage = 1
          this.getCheckCurrentUserWatch(toUid)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 获取用户关注的状态
    getCheckCurrentUserWatch: function (uid) {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        return;
      }
      let params = {}
      params.toUserUid = uid
      checkCurrentUserWatch(params).then(response => {
        this.userWatchStatus = response.data
      })
    },
    // 跳转到个人中心页
    getUserCenter: function (type) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: this.userData.uid, type: type}
      });
      window.open(routeData.href, '_blank');
    },
    getCardStyle: function (x, y) {
      let yy = y - 282
      // 如果当前的卡片是登录用户的，会没有关注和私信功能，因此还需要减少
      if (this.userData.uid == this.loginUserInfo.uid) {
        yy = y - 235
      }
      return 'left: ' + (x - 70) + 'px;\n' +
        '    position: fixed;\n' +
        '    top: ' + yy + 'px;' + 'z-index: 1000'
    }
  }
};
</script>

<style scoped>
.sidebarDiv {
   width: 160px;
   height: 160px;
 }

.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.image {
  height: 100px;
  left: 50px;
  top: 20px;
  position: relative;
  border-radius: 50%;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.cardStyle {
  width: 600px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
}

.btnBox {
  display: flex;
  justify-content: center;
}

.topBox {
  display: flex;
  justify-content: center;
}

.bottomBox {
  display: flex;
  justify-content: center;
}

/deep/ .el-card {
  width: 383px;
}

/deep/ .el-progress-bar__innerText {
  color: #f4f4f5;
}

/deep/ .el-progress-bar__outer {
  background: #d8d8d8;
}

.blackFont {
  text-align: center;
  font-size: 13px;
}

.like-user-avatar {
  height: 35px;
  width: 35px;
  border-radius: 40px;
  float: left;
  margin-left: 4px;
  cursor: pointer;
  transition: all 0.6s;
}

.like-user-avatar:hover{
  transform: scale(1.5); /*transform:变形属性，scale：缩放1.1倍 */
}

.userCard {
  position: absolute;
  z-index: 100;
}
</style>
