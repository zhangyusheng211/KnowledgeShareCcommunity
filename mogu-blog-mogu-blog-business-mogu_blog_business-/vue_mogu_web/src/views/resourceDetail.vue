<template>
  <article style="position: relative;">
    <el-image-viewer
      v-if="showViewer"
      :on-close="closeViewer"
      :url-list="picList"/>

    <!--    <h1 class="t_nav">-->
    <!--      <a href="/" class="n1">网站首页</a>-->
    <!--      <a-->
    <!--        href="javascript:void(0);"-->
    <!--        v-if="resourceData.blogSort.uid"-->
    <!--        @click="goToSortList(resourceData.blogSort.uid)"-->
    <!--        class="n2"-->
    <!--      >{{ resourceData.blogSort ? resourceData.blogSort.sortName : "" }}</a>-->
    <!--    </h1>-->


    <div class="leftNavigation">
            <LeftNavigation :bizUid="resourceData.uid" :resourceType="'11'"
                            :style="{left: leftNavigationWidth - 65 + 'px'}"
                            @commentCallback="commentCallback"
                            isSubjectValue="false"
                            showSubjectValue="false"
                            @openPayCallback="dashangToggle"
                            style="position: fixed; top: 120px;"></LeftNavigation>
    </div>

    <div class="infosbox">
      <div class="newsview">
        <h3 class="news_title" v-if="resourceData.name">{{ resourceData.name }}</h3>
        <div class="bloginfo">
          <ul>
            <span v-if="resourceData.user">
               <li style="padding-right: 6px; text-align: center;">
                <div :class="resourceData.user.userTag > 0 ?'vip-avatar':''">
                  <el-avatar :class="resourceData.user.userTag > 0 ?'vip-color':''" v-if="resourceData.user" fit="fill"
                             size="medium" :src="resourceData.user.photoUrl"></el-avatar>
                  <el-avatar v-else fit="fill" size="small" src="https://empty">
                    <img :src="defaultAvatar"/>
                  </el-avatar>
                  <span style="font-size: 10px; right: -4px; bottom: 1px;" class="vip-text pointer"
                        v-if="resourceData.user.userTag > 0">v</span>
                </div>
              </li>
              <li class="author" style="margin-top: 8px; margin-left: 4px;">
                <span class="pointer" :class="'lv'+ resourceData.user.userLevel" v-if="resourceData.user"
                      @click="getUserCenter(resourceData)">{{ resourceData.user.nickName }}</span>
              </li>
            </span>


            <li class="lmname" style="margin-top: 8px;">
              <!--              <el-tag-->
              <!--                style="cursor:pointer;"-->
              <!--                :type="typeList[(resourceData.blogSort.sort+2)%5]"-->
              <!--                @click.native="goToSortList(resourceData.blogSort.uid)"-->
              <!--                hit-->
              <!--                size="mini"-->
              <!--                effect="light">-->
              <!--                {{ resourceData.blogSort.sortName }}-->
              <!--              </el-tag>-->

              <!--              <el-tag-->
              <!--                style="margin-left: 1px; cursor:pointer;"-->
              <!--                v-for="(tag, index) in resourceData.tagList"-->
              <!--                @click.native="goToTagList(tag.uid)"-->
              <!--                :key="tag.uid"-->
              <!--                :type="typeList[tag.sort%5]"-->
              <!--                hit-->
              <!--                size="mini"-->
              <!--                effect="light">-->
              <!--                {{ tag.content }}-->
              <!--              </el-tag>-->

            </li>
            <li class="view" style="margin-top: 8px;">
              <span class="iconfont">&#xe8c7;</span>
              {{ resourceData.clickCount }}
            </li>
            <!--            <li class="like" style="margin-top: 8px;">-->
            <!--              <span class="iconfont">&#xe663;</span>-->
            <!--              {{resourceData.collectCount}}-->
            <!--            </li>-->

            <li class="createTime" style="margin-top: 8px;">
              <span class="iconfont">&#xe606;</span>
              {{ resourceData.createTime }}
            </li>

            <li class="view" style=" margin-top: 8px; cursor: pointer;" @click="report(resourceData)">
              <span class="iconfont">&#xe65b;</span>举报
            </li>

          </ul>
        </div>

        <div class="tip">
          <strong>简介</strong>
          <span v-html="resourceData.summary">
            {{ resourceData.summary }}
          </span>
        </div>

        <div style="width: 100%; display: flex; justify-content: center; align-items: center;">
          <el-card shadow="hover" style="width: 400px; background: #d6ecff" >
            <div class="payCardFont" style="font-size: 14px;">付费方式：
              <el-tag
                size="mini"
                v-for="item in payTypeDictList"
                v-if="resourceData.payType == item.dictValue"
                :key="item.uid"
                :type="item.listClass"
              >{{ item.dictLabel }}
              </el-tag>
            </div>
            <div class="payCardFont" style="font-size: 14px;">付费金额：
              <el-tag
                size="mini"
                v-for="item in chargeTypeDictList"
                v-if="resourceData.chargeType == item.dictValue"
                :key="item.uid"
                :type="item.listClass"
              >
                        <span>
                            {{ item.dictLabel }}
                            <span v-if="resourceData.price && resourceData.price > 0">
                            (
                            <span v-if="resourceData.payType == 1">
                                {{resourceData.price}}积分
                            </span>
                            <span v-if="resourceData.payType == 2">
                                {{resourceData.price/100}}元
                            </span>
                            )
                            </span>
                        </span>
              </el-tag>
            </div>

            <div style="margin-top: 5px; font-size: 14px;">
                <span v-if="resourceData.panPath">网盘链接: {{resourceData.panPath}}</span>
                <span v-else>网盘链接: ******</span>
                <span v-if="resourceData.panCode">&nbsp;密码：{{resourceData.panCode}}</span>
                <span v-else >&nbsp;密码: ******</span>
            </div>

            <el-button style="margin-top: 5px;" type="primary" size="mini" plain v-if="resourceData.panCode" @click="copyResource">复制链接</el-button>
            <el-button style="margin-top: 5px;" type="primary" size="mini"  plain v-if="!resourceData.panCode" @click="getPanCode">获取密码</el-button>
          </el-card>

        </div>


        <div class="contentTip" v-if="resourceData.content">
          <strong>内容描述</strong>
        </div>
        <span v-if="resourceData.content" v-html="resourceData.content" class="resourceDetail">
            {{ resourceData.content }}
        </span>
      </div>

      <!--社会化分享-->
      <share  :config="config" style="text-align: center"></share>
      <PayCode v-if="showPay" @close="dashangToggle"></PayCode>
      <PayWindow v-if="showPayWindow" :product="productVO" @payCallback="payCallback"></PayWindow>

      <div class="news_pl" id="comment">
        <h2 v-if="openComment == '1'" class="title">资源评论</h2>
        <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel" :targetId="commentInfo.blogUid"
                 :tableName="commentInfo.source"></Comment>
      </div>

    </div>

    <Sticky>
      <div class="sidebar2" v-if="showSidebar">
        <UserCard :usrCard="blogUserInfo"></UserCard>
        <resourceRank title="点击排行榜" orderByDesc="clickCount" background-color="#c98e5f" width="363px" height="580px" :page-size="9" style="margin-top: 300px; margin-left: 0;  position: fixed;"></resourceRank>
      </div>

    </Sticky>

  </article>
</template>

<script>

import Comment from "../components/Comment";
import UserCard from "../components/UserCard"
import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'
import PayWindow from "../components/PayWindow"

// vuex中有mapState方法，相当于我们能够使用它的getset方法
import {mapMutations} from "vuex";
import LeftNavigation from "../components/LeftNavigation";
import {Loading} from "element-ui";
import SideCatalog from '@/components/VueSideCatalog'
import {recorderBehavior} from "../api";
import {getListByDictTypeList} from "@/api/sysDictData";
import {getResourceDetail, downloadResource} from "../api/resource";
import Sticky from "@/components/Sticky";
import PayCode from "../components/PayCode";
import {getUserByUid} from "../api/about";
import resourceRank from "../components/Resource/resourceRank"
export default {
  name: "info",
  data() {
    return {
      productVO: {},
      showViewer: false,
      picList: [],
      tableName: 'BLOG_INFO',
      typeList: ['warning', 'danger', 'success', 'info', 'primary'],
      // 目录列表数
      catalogSum: 0,
      showStickyTop: false,
      showSideCatalog: true,
      showSidebar: true, //是否显示侧边栏
      showSubject: true, // 是否显示侧边专题
      loadingInstance: null, // loading对象
      resourceData: {},
      comments: [],
      blogUserInfo: {},
      commentInfo: {
        // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
        source: "RESOURCE_INFO",
        maxReplyLevel: 4, // 最大回复深度
        blogUid: this.$route.params.resourceUid
      },
      config: {
        sites: ['qq', 'qzone', 'weibo', 'wechat', 'douban'],
      },
      showPay: false,
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      toInfo: {},
      payTypeDictList: [], // 支付类型
      chargeTypeDictList: [], // 收费模式
      webConfig: {}, // 网站配置
      openComment: "0", // 开启评论
      openAdmiration: "0", // 开启赞赏
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      leftNavigationWidth: (document.body.clientWidth - 1200) / 2, // 左侧区域的大小
      resourceUid: "",
      showPayWindow: false,
      defaultAvatar: process.env.defaultAvatar
    };
  },
  computed: {},
  components: {
    //注册组件
    SideCatalog,
    Comment,
    ElImageViewer,
    LeftNavigation,
    UserCard,
    Sticky,
    PayCode,
    resourceRank,
    PayWindow,
  },
  metaInfo() {
    return {
      title: this.resourceData.title,
      meta: [
        {
          name: "keywords",
          content: this.resourceData.title,
        },
        {
          name: "content",
          content: this.resourceData.title,
        },
        {
          name: "description",
          content: this.resourceData.summary,
        },
      ],
    };
  },
  watch: {
    $route(to, from) {
      location.reload()
    },
    // 判断用户信息
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.loginUserInfo = this.$store.state.user.userInfo
    },
    // 判断配置
    '$store.state.app.webConfigData': function (newFlag, oldFlag) {
      this.webConfig = this.$store.state.app.webConfigData
      this.setWebConfig()
    },
  },
  mounted() {
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    this.resourceDetail()
  },
  created() {
    this.getDictList()
    this.resourceUid = this.$route.params.resourceUid
  },
  methods: {
    //拿到vuex中的写的两个方法
    ...mapMutations(["setCommentList", "setWebConfigData", "setLoginMessage"]),
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_charge_type', 'sys_pay_type']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.chargeTypeDictList = dictMap.sys_charge_type.list
          this.payTypeDictList = dictMap.sys_pay_type.list
        }
      });
    },
    resourceDetail() {
      this.loadingInstance = Loading.service({
        fullscreen: true,
        text: "正在努力加载中~"
      });
      const loadingInstance = this.loadingInstance
      this.loginUserInfo = this.$store.state.user.userInfo
      let that = this;
      let params = {};
      params.uid = this.resourceUid
      getResourceDetail(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          console.log("加载资源内容", response)
          let resourceData = response.data
          this.getUserInfo(resourceData.userUid)
          this.productVO = this.$commonUtil.convertResource(resourceData)
          this.resourceData = resourceData
          that.loadingInstance.close();

          // 5S后埋点信息上报
          setTimeout(() => {
            recorderBehavior({
              "otherData": resourceData.title,
              "bizUid": resourceData.uid,
              "bizType": "BLOG",
              "behavior": "BLOG_CONTNET"
            }).then(response => {
            })
          }, 5000)

        } else {
          this.$message.error(response.message);
          that.loadingInstance.close();
        }
      }, function (err) {
        loadingInstance.close();
      });
    },
    getUserInfo(userUid) {
      let params = new URLSearchParams()
      params.append("userUid", userUid)
      getUserByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let blogUserInfo = response.data
          this.blogUserInfo = blogUserInfo
        } else {
          this.$message.error(response.message)
        }
      })
    },
    commentCallback: function () {
      document.getElementById('comment').scrollIntoView();
    },
    dashangToggle: function () {
      this.showPay = !this.showPay;
    },
    buyProduct() {
      let params = {}
      params.uid = this.resourceUid
      downloadResource(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          this.$message.success("支付成功")
          this.resourceData = response.data
        } else {
          this.$message.error(response.message)
        }
      })
    },
    getPanCode() {
      // if (this.resourceData.price > 0) {
      //   this.$confirm('此动作将会扣除金额进行支付, 是否继续?', '支付提醒', {
      //     confirmButtonText: '确定',
      //     cancelButtonText: '取消',
      //     type: 'warning'
      //   })
      //     .then(() => {
      //       this.buyProduct()
      //     })
      //     .catch(() => {
      //       this.$commonUtil.message.info('已取消支付')
      //     })
      // } else {
      //   this.buyProduct()
      // }
      this.showPayWindow = true
    },
    copyResource() {
      this.$commonUtil.copyText("地址：" + this.resourceData.panPath + "  密码：" + this.resourceData.panCode)
      this.$message.success("复制成功")
    },
    closeViewer() {
      this.showViewer = false
    },
    payCallback(paySuccess) {
      this.showPayWindow = false
      if (paySuccess) {
        this.resourceDetail()
      }
    },
    // 设置是否开启评论和赞赏
    setWebConfig() {
      let webConfigData = this.$store.state.app.webConfigData
      if (webConfigData.createTime) {
        this.openAdmiration = webConfigData.openAdmiration
        this.openComment = webConfigData.openComment
      }
    },
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showViewer = true
        this.picList = [e.target.currentSrc]
      }
    },
  }
};
</script>

<style>

.newsview {
  min-height: 400px;
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


.contentTip {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #e7f6e0;
  border-radius: 4px;
  border-left: 5px solid #67c23a;
  color: #888888;
}

.contentTip strong {
  color: #617c4f;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.side-catalog div {
  background: #FFF;
}

/deep/ .el-progress-bar__innerText {
  background: none;
}

.payCardFont {
  font-size: 15px;
  margin-top: 5px;
}

.resourceDetail img {
  max-width: 100%;
  max-height: 100%;
}

</style>
