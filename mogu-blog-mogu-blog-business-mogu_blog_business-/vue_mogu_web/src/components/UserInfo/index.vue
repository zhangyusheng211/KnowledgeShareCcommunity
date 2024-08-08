<template>
  <div>

    <el-card :body-style="{ padding: '20px' }" class="cardStyle" v-if="isLogin">
      <div>
        <el-row>
          <el-col :span="5" style="text-align: center">
              <span :class="userData.userTag > 0 ?'vip-avatar':''">
                <el-avatar v-if="userData" :class="userData.userTag > 0 ?'vip-color':''" style="cursor: pointer;"
                           shape="circle" @click.native="getUserCenter('1')" :size="60" fit="fill"
                           :src="userData.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" :size="60" src="https://empty" shape="circle">
                  <img :src="process.env.defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                      v-if="userData.userTag > 0">v</span>
              </span>

          </el-col>

          <el-col :span="18">
            <div v-if="userData" style="cursor: pointer">
              <el-row>
                <el-col>
                    <span @click="getUserCenter('1')" class="blackFont"
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
                    style=" font-size: 13px; font-weight: 500; color: rgb(114, 119, 123);">排名：{{ userData.rank }}</span>

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
            经验值
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

        <el-row :gutter="20" type="flex" justify="center" style="margin-top: 10px; min-height: 45px">

          <el-col @click.native="personCenterHandle('MyArticle')" :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
              <countTo :startVal='0' :endVal='userData.blogPublishCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">文章</div>
          </el-col>

          <el-col @click.native="personCenterHandle('MyComment')" :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
              <countTo :startVal='0' :endVal='userData.userCommentCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">评论</div>
          </el-col>

          <el-col @click.native="personCenterHandle('MyCollect')" :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
              <countTo :startVal='0' :endVal='userData.userCollectCount' :duration='3000'></countTo>
            </div>
            <div class="countStyle2">收藏</div>
          </el-col>

          <el-col @click.native="goMedalDetail" :span="4" style="text-align: center; cursor: pointer">
            <div class="countStyle">
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

          <el-col @click.native="getUserCenter('7')" :span="4" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.userFollowCount' :duration='3000'></countTo>
              </div>
              <div class="countStyle2">粉丝</div>
            </el-row>
          </el-col>

          <!--          <el-col @click.native="getUserCenter('8')" :span="4" style="text-align: center; cursor: pointer">-->
          <!--            <el-row>-->
          <!--              <div class="countStyle">-->
          <!--                <countTo :startVal='0' :endVal='userData.userWatchCount' :duration='3000'></countTo>-->
          <!--              </div>-->
          <!--              <div class="countStyle2">关注</div>-->
          <!--            </el-row>-->
          <!--          </el-col>-->

        </el-row>

        <el-row style="margin-top: 5px;" v-if="userData.userMedalCount > 0">
          <span style="float: left; font-size: 12px; line-height: 35px;">
            <el-tooltip placement="bottom" effect="light">
              <div slot="content">活跃社区解锁海量勋章</div>
              <span>获得勋章：</span>
            </el-tooltip>
          </span>
          <span v-if="index < 7" v-for="(medal, index) in userData.userMedalList" :key="medal.uid">
            <el-tooltip class="item" effect="light"
                        :content="medal.summary + '(' + timeFormat(medal.gainTime) + ')' + '[约'+ medal.gainProbability +'用户获取]'"
                        placement="bottom">
              <img @click="goMedalDetail" :src="medal.fileUrl" class="like-user-avatar">
            </el-tooltip>
          </span>
        </el-row>


        <el-row :gutter="24" type="flex" justify="center"
                style="margin-top: 10px; padding: 10px; min-height: 45px; background: #efefef;">

          <el-col @click.native="goToUserCenter('9')" :span="12" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle2">7日新增阅读数</div>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.webVisitCountByWeek' :duration='3000'></countTo>
              </div>
            </el-row>
          </el-col>

          <el-col @click.native="goToUserCenter('7')" :span="12" style="text-align: center; cursor: pointer">
            <el-row>
              <div class="countStyle2">7日新增点赞数</div>
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userData.praiseCountByWeek' :duration='3000'></countTo>
              </div>

            </el-row>
          </el-col>
        </el-row>

      </div>
    </el-card>

    <el-card :body-style="{ padding: '20px' }" class="cardStyle" v-if="!isLogin">
      <el-row class="loginText">
        <span>登录网站，开启你的创作之旅：</span>
      </el-row>
      <el-row class="loginSummary" :span="24">
        <el-col :span="12">
          <span class="iconfont">&#xe606;</span>更懂你的优质内容
        </el-col>
        <el-col :span="12">
          <span class="iconfont">&#xe606;</span>更专业的大咖答主
        </el-col>
      </el-row>
      <el-row class="loginSummary" :span="24">
        <el-col :span="12">
          <span class="iconfont">&#xe606;</span>更深度的互动交流
        </el-col>
        <el-col :span="12">
          <span class="iconfont">&#xe606;</span>更高效的创作环境
        </el-col>
      </el-row>
      <el-row>
        <el-button type="primary" style="width: 100%; margin-top: 20px;" @click="login">立即登录</el-button>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData";
import {mapMutations} from "vuex";
import {getUserByUid} from "../../api/about";
import countTo from 'vue-count-to';
import {timeAgo} from "../../utils/webUtils";

export default {
  name: "UserInfo",
  props: [],
  data() {
    return {
      userLevelDictList: [],
      userTagDictList: [],
      userInfo: {},
      isLogin: false,
      customColor: '#409eff',
      percentage: 0,
      userData: {
        blogPublishCount: 0,
        userMomentCount: 0,
        userFollowCount: 0,
        userWatchCount: 0,
        userCommentCount: 0,
        userCollectCount: 0,
        userMedalCount: 0
      },
      userLevelImagesDictList: [],
      userLevelImage: null,
      defaultAvatar: process.env.defaultAvatar, // 默认头像
    };
  },
  components: {
    countTo
  },
  created() {
    this.getDictList()
    this.getUserInfo(false)
    this.getPercentage()
  },
  mounted() {
    this.loginUserInfo = this.$store.state.user.userInfo
    this.isLogin = this.$store.state.user.isLogin
  },
  watch: {
    // 判断用户信息
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.getUserInfo(false)
    },
  },
  methods: {
    ...mapMutations(["setLoginMessage", "setDomainEventMessage"]),
    goLink(url) {
      console.log("跳转url", url)
      this.$router.push(url)
    },
    getLvImage(level) {
      this.userLevelImage = this.userLevelImagesDictList[level - 1].dictValue
    },
    login() {
      if (this.isLogin) {
        this.$message.success("用户已登录")
        return
      }
      this.setLoginMessage(Math.random())
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
    getPercentage() {
      if (this.userData.expValue && this.userData.levelMaxCredits) {
        this.percentage = this.userData.expValue * 100 / this.userData.levelMaxCredits;
      }
    },
    valueFormat() {
      return '经验值：' + this.userData.expValue + "/" + this.userData.levelMaxCredits;
    },
    getUserInfo: function (refresh) {
      this.loginUserInfo = this.$store.state.user.userInfo
      this.isLogin = this.$store.state.user.isLogin
      if (this.isLogin) {
        let params = new URLSearchParams()
        params.append("userUid", this.loginUserInfo.uid)
        params.append("refresh", refresh) // 是否强制刷新
        getUserByUid(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            if (refresh) {
              this.$message.success("刷新成功")
            }
            this.userData = response.data
            this.getPercentage()
            this.getLvImage(this.userData.userLevel)
          }
        })
      }
    },
    goMedalDetail: function () {
      this.$router.push("/medal/" + this.loginUserInfo.uid)
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
    personCenterHandle(action) {
      // 发送个人中心领域事件
      let event = {
        "type": "personCenter",
        "action": action,
        "time": new Date(),
      }
      this.setDomainEventMessage(event)
    },
    // 跳转到个人中心页
    getUserCenter: function (type) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: this.userData.uid, type: type}
      });
      window.open(routeData.href, '_blank');
    },

  }
};
</script>

<style scoped>
.cardStyle {
  min-height: 210px;
  margin-bottom: 10px;
}

.loginText {
  margin-top: 15px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0px;
  line-height: 0px;
  color: rgba(0, 0, 0, 1);
}

.loginSummary {
  margin-top: 30px;
  font-size: 16px;
  font-weight: 500;
  color: rgba(51, 51, 51, 1);
}

.countStyle {
  color: #000;
  font-weight: bold;
}

.countStyle2 {
  color: #000;
}

/deep/ .el-progress-bar__innerText {
  color: #f4f4f5;
}

/deep/ .el-progress-bar__outer {
  background: #d8d8d8;
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

.like-user-avatar:hover {
  transform: scale(1.5); /*transform:变形属性，scale：缩放1.1倍 */
}
</style>
