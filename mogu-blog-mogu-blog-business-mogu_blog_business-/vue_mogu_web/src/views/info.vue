<template>

  <span>

    <!--加载专栏信息-->
    <SubjectNavigation style="position: fixed; top: 60px;" :dataList="subjectItemList" :entityUid="blogUid"
                       :subject="subject" v-if="subject && showSubject && showSubjectBtn"></SubjectNavigation>

    <article style="position: relative;">
    <el-image-viewer
      v-if="showViewer"
      :on-close="closeViewer"
      :url-list="picList"/>

    <h1 class="t_nav">
      <a href="/" class="n1">网站首页</a>
      <a
        href="javascript:void(0);"
        v-if="blogData.blogSort.uid"
        @click="goToSortList(blogData.blogSort.uid)"
        class="n2"
      >{{ blogData.blogSort ? blogData.blogSort.sortName : "" }}</a>
    </h1>

    <div class="leftNavigation">
      <!--加载侧边专栏-->
      <LeftNavigation :bizUid="blogData.uid" :resourceType="'1'"
                      :style="{left: leftNavigationWidth - 65 + 'px'}"
                      @commentCallback="commentCallback"
                      @subjectCallback="changeShowSubject"
                      :isSubjectValue="showSubjectBtn"
                      :showSubjectValue="subject && showSubject"
                      :showSteepRead="showSteepRead"
                      @openPayCallback="dashangToggle"
                      @steepReadCallBack="steepReadCallBack"
                      style="position: fixed; top: 120px;"></LeftNavigation>
    </div>

      <!--切换专栏文章-->
    <span v-if="showSteepRead && subjectItemList && subjectItemList.length > 0">
      <el-tooltip content="上一篇" placement="top-end" effect="light">
        <el-button type="primary" style="position: fixed; bottom: 100px; font-size: 20px"
                   :style="{left: leftNavigationWidth - 65 + 'px'}" circle icon="el-icon-arrow-left" @click="preArticle"
                   :disabled="!showPreButton"></el-button>
      </el-tooltip>

      <el-tooltip content="下一篇" placement="top-end" effect="light">
        <el-button type="primary" style="position: fixed; bottom: 100px;  font-size: 20px"
                   :style="{left: leftNavigationWidth - 65 + 958 + 'px'}" circle icon="el-icon-arrow-right"
                   @click="nextArticle" :disabled="!showNextButton"></el-button>
      </el-tooltip>
    </span>

    <div class="infosbox">

      <div class="newsview">
        <div v-if="blogData.visitAuth === 3" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="评论可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/comment.png"></img>
          </el-tooltip>
        </div>
        <div v-else-if="blogData.visitAuth === 5" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="会员可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/vip.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="blogData.visitAuth === 6" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="付费可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/pay.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="blogData.visitAuth === 7" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="点赞可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/vip.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="blogData.visitAuth === 8" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="收藏可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/collect.png"></img>
          </el-tooltip>
        </div>
        <div v-else-if="blogData.visitAuth === 9" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="关注可见" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/watch.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="blogData.isTop === 1" style="float: right; font-size:20px;  margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="置顶文章" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/top.png"></img>
          </el-tooltip>
        </div>
        <div v-else-if="blogData.isOriginal === '1'" style="float: right; font-size:20px; margin-right: -29px;">
          <el-tooltip class="item" effect="dark" content="原创文章" placement="top">
            <img style="width: 40px; height: 40px" src="../assets/img/original.png"></img>
          </el-tooltip>
        </div>

        <h3 class="news_title" v-if="blogData.title">{{ blogData.title }}</h3>
        <div class="bloginfo" v-if="blogData.blogSort.uid">
          <ul>
            <li style="padding-right: 6px; text-align: center;">
              <div :class="blogData.user.userTag > 0 ?'vip-avatar':''">
                <el-avatar :class="blogData.user.userTag > 0 ?'vip-color':''" v-if="blogData.user" fit="fill"
                           size="medium" :src="blogData.user.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" size="small" src="https://empty">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: 1px;" class="vip-text pointer"
                      v-if="blogData.user.userTag > 0">v</span>
              </div>
            </li>
            <li class="author" style="margin-top: 8px; margin-left: 4px;">
              <span class="pointer" :class="'lv'+ blogData.user.userLevel" v-if="blogData.user"
                    @click="getUserCenter(blogData)">{{ blogData.user.nickName }}</span>
            </li>

            <li class="lmname" style="margin-top: 8px;">
              <el-tag
                style="cursor:pointer;"
                :type="typeList[(blogData.blogSort.sort+2)%5]"
                @click.native="goToSortList(blogData.blogSort.uid)"
                hit
                size="mini"
                effect="light">
                {{ blogData.blogSort.sortName }}
              </el-tag>

              <el-tag
                style="margin-left: 1px; cursor:pointer;"
                v-for="(tag, index) in blogData.tagList"
                @click.native="goToTagList(tag.uid)"
                :key="tag.uid"
                :type="typeList[tag.sort%5]"
                hit
                size="mini"
                effect="light">
                {{ tag.content }}
              </el-tag>
            </li>
            <li class="view" style="margin-top: 8px;">
              <span class="iconfont">&#xe8c7;</span>
              {{ blogData.clickCount }}
            </li>

            <li class="createTime" style="margin-top: 8px;">
              <span class="iconfont">&#xe606;</span>
              {{ blogData.createTime }}
            </li>

            <li class="view" style=" margin-top: 8px; cursor: pointer;" @click="report(blogData)" v-if="componentShowMap.showUserReport">
              <span class="iconfont">&#xe65b;</span>举报
            </li>

            <!-- 勘误修正按钮  -->
            <li class="view" style="margin-top: 8px; cursor: pointer;" @click="openEditGeneral" v-if="componentShowMap.showGeneralEdit">
              <span class="el-icon-edit-outline"></span>
              <span class="iconfont"></span>勘误
            </li>

          </ul>
        </div>

        <div class="tip">
          <strong>版权</strong>
          <span v-html="blogData.copyright">
            {{ blogData.copyright }}
          </span>
        </div>


        <foldable v-if="!loadingMore" :minHeight="minHeight" :height="height">
          <div
            :style="isCopy === 0 ? 'user-select:none;' : ''"
            @selectstart="isCopy === 0 ? () => false : true"
            @contextmenu="isCopy === 0 ? () => false : true"
            @copy="isCopy === 0 ? () => false : true"
            @keydown="isCopy === 0 ? disableInfo : null"
            @resize="isCopy === 0 ? onSize : null"

            class="news_con"
            v-html="blogContent"
            v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }"
            v-highlight
            @click="imageChange"
          ></div>
          <template slot="view-more" slot-scope="{ toggle, collapsed }">
            <!--            <div @click="clickMore(toggle)"-->
            <!--                 class="csdn-view-more"-->
            <!--                 style="border-color: #E2523A; color: #E2523A"-->
            <!--                 :class="{ 'collapsed': collapsed }">-->
            <!--              <span class="iconfont">&#xe8c7;</span>-->
            <!--              {{ collapsed ? '点击阅读全文' : '收起' }}-->
            <!--            </div>-->
            <!-- 访问卡片 -->
            <VisitAuthCard :entity="blogData" resourceType="BLOG" @refresh="getBlogInfo"></VisitAuthCard>
          </template>
        </foldable>
        <div v-else
            :style="isCopy === 0 ? 'user-select:none;' : ''"
            @selectstart="isCopy === 0 ? () => false : true"
            @contextmenu="isCopy === 0 ? () => false : true"
            @copy="isCopy === 0 ? () => false : true"
            @keydown="isCopy === 0 ? disableInfo : null"
            @resize="isCopy === 0 ? onSize : null"
            >
            <div class="news_con ck-content"
                v-html="blogContent"
                v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }"
                v-highlight
                @click="imageChange"
            ></div>
        </div>
    </div>

      <!--      <VisitAuthCard :entity="blogData" @refresh="getBlogInfo"></VisitAuthCard>-->

      <!--社会化分享-->
      <share v-if="loadingMore" :config="config" style="text-align: center; margin-top: 10px;"></share>

      <!--设置博客点赞用户列表-->
      <BottomPraise v-if="componentShowMap.showUserPrise" :resourceUid="blogData.uid" resourceType="1"></BottomPraise>

      <div class="otherlink" v-if="sameBlogData.length > 0">
        <h2>相关文章</h2>
        <ul>
          <li v-for="item in sameBlogData" :key="item.uid">
            <a
              :href="item.type == 0 ? VUE_MOGU_WEB + '/info/'+item.oid : item.outsideLink" target="_blank"
              :title="item.title"
            >{{ subText(item.title, 18) }}</a>
          </li>
        </ul>
      </div>

      <div class="news_pl" :style="opemCommentCss" id="comment" v-if="componentShowMap.showUserComment">
        <h2 v-if="openComment == '1'" class="title">文章评论</h2>
        <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel" :targetId="commentInfo.blogUid"
                 :author-uid="blogData.userUid"
                 :tableName="commentInfo.source"></Comment>
      </div>
    </div>

      <!--侧边目录-->
    <Sticky :sticky-top="70">
      <div class="sidebar2" v-if="showSidebar">
        <UserCard :usrCard="blogUserInfo" v-if="showSteepRead"></UserCard>
        <side-catalog
          class="side-catalog"
          style="position: fixed; max-height: 725px; width: 385px; margin-top: 285px"
          :style="{marginTop: showSteepRead?'285px':'0px'}"
          v-bind="catalogProps"
          :htmlContent="blogContent"
        ></side-catalog>
      </div>
    </Sticky>

      <!-- 加载校验框 -->
    <el-dialog
      title="加载校验"
      :visible.sync="dialogVisible"
      close-on-click-modal
      :width="dialogSize"
    >

      <div style="text-align: center">
        <div v-html="webConfig.loadingValidText"></div>
        <div style="margin-top: 10px">
          <el-image style="width: 300px" :src="webConfig.loadingValidPhoto"></el-image>
        </div>
        <div style="margin-top: 10px">
          <el-input placeholder="请输入验证码(6位)" style="width: 200px" v-model="validCode"
                    @keyup.enter.native="submitCode"></el-input>
        </div>
        <div style="margin-top: 10px">
          <el-button type="primary" style="width: 200px" @click="submitCode">提交</el-button>
        </div>
      </div>

    </el-dialog>

      <!--支付码-->
    <PayCode v-if="openAdmiration == '1' && loadingMore && showPay" @close="dashangToggle"></PayCode>

    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo"
            @beforeClose="beforeClose"></Report>
    <GeneralEdit v-if="dialogEditVisible" :visible="dialogEditVisible" :generalInfo="generalInfo"
                 @beforeCloseEdit="beforeCloseEdit"></GeneralEdit>

  </article>
  </span>


</template>

@import "../assets/dist/css/share.min.css";
<script>

import { getBlogByUid, getSameBlogByBlogUid } from "../api/blogContent";
import Comment from "../components/Comment";
import UserCard from "../components/UserCard"
import { checkValidCode, getLoadingValid } from "@/api/user";
import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'
// vuex中有mapState方法，相当于我们能够使用它的getset方法
import { mapMutations } from "vuex";
import ThirdRecommend from "../components/ThirdRecommend";
import FourthRecommend from "../components/FourthRecommend";
import TagCloud from "../components/TagCloud";
import HotBlog from "../components/HotBlog";
import FollowUs from "../components/FollowUs";
import PayCode from "../components/PayCode";
import Link from "../components/Link";
import Collection from "../components/Collection";
import Praise from "../components/Praise";
import LeftNavigation from "../components/LeftNavigation";
import { Loading } from "element-ui";
import Sticky from "@/components/Sticky";
import Report from "@/components/Report"
import SideCatalog from '@/components/VueSideCatalog'
import { getCookie, setCookie } from "@/utils/cookieUtils";
import { checkCurrentUserWatch, getUserByUid } from "../api/about";
import GeneralEdit from "@/components/GeneralEdit";
import BottomPraise from "@/components/BottomPraise";
import { recorderBehavior } from "../api";
import { getListByDictTypeList } from "@/api/sysDictData";
import SubjectNavigation from "../components/SubjectNavigation";
import VisitAuthCard from "../components/VisitAuthCard";
import { delCookie } from "../utils/cookieUtils";
import config from '../../conf.json'

export default {
  name: "info",
  data () {
    return {
      generalInfo: {}, //博客信息
      dialogEditVisible: false,
      showViewer: false,
      picList: [],
      articleId: this.$route.query.blogUid,
      tableName: 'BLOG_INFO',
      typeList: ['warning', 'danger', 'success', 'info', 'primary'],
      showSubjectBtn: true, // 是否显示专题
      subject: {}, // 专题信息
      subjectItemList: [], // 专题列表信息
      loadingMore: false,
      toggle: null,
      dialogSize: "35%",
      height: "50%",
      imageWidth: "60%",
      minHeight: 1500, // 查看全文前显示高度
      dialogVisible: false,
      validCode: "",
      // 目录列表数
      catalogSum: 0,
      showSidebar: true, //是否显示侧边栏
      showSubject: true, // 是否显示侧边专题
      blogContent: "",
      catalogProps: {
        // 内容容器selector(必需)
        container: '.ck-content',
        watch: true,
        levelList: ["h2", "h3"],
      },
      loadingInstance: null, // loading对象
      showCancel: false,
      submitting: false,
      comments: [],
      commentInfo: {
        // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
        source: "BLOG_INFO",
        maxReplyLevel: 4, // 最大回复深度
        blogUid: this.$route.query.blogUid
      },
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      toInfo: {},
      userWatchStatus: 0,// 当前用户关注状态
      loginUserInfo: {}, // 当前登录用户的信息
      userInfo: {},
      dialogReportVisible: false, // 控制举报弹出
      reportInfo: {}, // 举报信息
      blogUid: null, //传递过来的博客uid
      blogOid: 0, // 传递过来的博客oid
      blogData: {
        blogSort: {},
        content: "",
        user: {}
      },
      webConfig: {}, // 网站配置
      sameBlogData: [], //相关文章
      openComment: "0", // 开启评论
      openAdmiration: "0", // 开启赞赏
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      userLevelDictList: [],
      userTagDictList: [],
      config: {
        sites: ['qq', 'qzone', 'weibo', 'wechat', 'douban'],
      },
      leftNavigationWidth: (document.body.clientWidth - 1200) / 2, // 左侧区域的大小
      showPay: false,
      blogUserInfo: {},
      defaultAvatar: process.env.defaultAvatar, // 默认头像
      showPreButton: true,
      showNextButton: true,
      showSteepRead: false,
      webConfigData: {}, // 网站配置
      isCopy: 0, //传递过来的博客iscopy，0表示可复制博客内容，1表示不可复制博客内容
      componentShowMap: {},
    };
  },
  computed: {
    opemCommentCss: function () {
      if (this.openComment == 0) {
        return {
          "min-height": "10px"
        }
      }
    }
  },
  components: {
    //注册组件
    FourthRecommend,
    ThirdRecommend,
    TagCloud,
    HotBlog,
    FollowUs,
    PayCode,
    SideCatalog,
    Link,
    Sticky,
    Comment,
    Report,
    Collection,
    Praise,
    ElImageViewer,
    GeneralEdit,
    LeftNavigation,
    UserCard,
    BottomPraise,
    SubjectNavigation,
    VisitAuthCard,
  },
  metaInfo () {
    return {
      title: this.blogData.title ? this.blogData.title : config.title,
      meta: [
        {
          name: "keywords",
          content: this.blogData.title,
        },
        {
          name: "content",
          content: this.blogData.title,
        },
        {
          name: "description",
          content: this.blogData.summary,
        },
      ],
    };
  },
  watch: {
    $route (to, from) {
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
    blogContent: function (newFlag, oldFlag) {
      // this.mathJax()
    }
  },
  mounted () {
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };

    this.getBlogInfo();

    // 屏幕自适应
    let that = this
    window.onresize = () => {
      return (() => {
        // 屏幕大于950px的时候，显示侧边栏
        that.showSidebar = document.body.clientWidth > 1200
        that.showSubject = document.body.clientWidth > 1800
      })()
    };

  },
  created () {
    this.getDictList()
    let oid = this.$route.params.blogOid
    this.subjectUid = this.$route.query.subject
    console.log("subjectUid", this.subjectUid)
    // 根据类型判断是否是oid还是uid
    if (oid.length === 32) {
      this.blogUid = oid
    } else {
      this.blogOid = oid
    }
    // 屏幕大于1200px的时候，显示侧边栏
    this.showSidebar = document.body.clientWidth > 1200
    // 屏幕大于1700px的时候，显示专题侧边栏
    this.showSubject = document.body.clientWidth > 1800

    // 获取专题是否显示
    let subjectShow = getCookie("subjectShow")
    if (subjectShow) {
      if (subjectShow == "false") {
        this.showSubjectBtn = false
      } else {
        this.showSubjectBtn = true
      }
    }

    // 是否沉浸阅读
    let showSteepRead = getCookie("showSteepRead")
    if (showSteepRead) {
      if (showSteepRead == "false") {
        this.showSteepRead = false
      } else {
        this.showSteepRead = true
      }
    }

    this.webConfig = this.$store.state.app.webConfigData
    this.setWebConfig()
    // this.mathJax()
  },
  methods: {
    //拿到vuex中的写的两个方法
    ...mapMutations(["setCommentList", "setWebConfigData", "setLoginMessage"]),
    beforeCloseEdit: function () {
      this.dialogEditVisible = false
    },
    commentCallback: function () {
      document.getElementById('comment').scrollIntoView();
    },
    handleOpen (key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose (key, keyPath) {
      console.log(key, keyPath);
    },
    // 缓存专栏学习进度
    recordSubjectProcess () {
      let blog = this.blogData
      // 判断是否是专栏学习
      if (blog.subject) {
        console.log("阅读专栏", blog.subject)
        // 将专栏学习进度缓存起来
        let subjectRecord = {
          "subjectUid": blog.subject.uid,
          "subjectTitle": blog.subject.subjectName,
          "subjectSummary": blog.subject.summary,
          "blogOid": blog.oid,
          "blogTitle": blog.title,
          "date": new Date(),
        }
        // 将数据存储到LS中
        localStorage.setItem('subjectRecord', JSON.stringify(subjectRecord));
      } else {
        // 如果阅读的不是专栏，把之前的删掉
        localStorage.setItem('subjectRecord', "");
      }
    },
    // mathJax() {
    //   this.$nextTick(function () { // Vue的DOM渲染是异步的
    //     if (this.MathJax.isMathjaxConfig) { // 是否配置MathJax
    //       this.MathJax.initMathjaxConfig()
    //     }
    //     this.MathJax.MathQueue('ck-content') // 渲染对应的id/class
    //   })
    // },

    //通过按键事件阻止F12（ctrl+shift+i/shift+f10）事件
    disableInfo () {
      console.info("通过按键事件阻止F12（ctrl通过按键事件阻止F12（ctrl")
      document.onkeydown = function () {
        var e = window.event || arguments[0]
        //屏蔽F12
        if (e.keyCode == 123) {
          return false
          //屏蔽Ctrl+Shift+I
        } else if (e.ctrlKey && e.shiftKey && e.keyCode == 73) {
          return false
          //屏蔽Shift+F10
        } else if (e.shiftKey && e.keyCode == 121) {
          return false
        }
      }
      //屏蔽右键单击
      document.oncontextmenu = function () {
        return false
      }

    },


    getBlogInfo () {
      let that = this;
      this.loadingInstance = Loading.service({
        fullscreen: true,
        text: "正在努力加载中~"
      });
      const loadingInstance = this.loadingInstance
      this.loginUserInfo = this.$store.state.user.userInfo
      let params = new URLSearchParams();
      if (this.blogOid) {
        params.append("oid", this.blogOid)
      } else {
        params.append("uid", this.blogUid)
      }
      if (this.subjectUid) {
        params.append("subjectUid", this.subjectUid)
      }
      // 开启图片懒加载
      params.append("isLazy", "1")
      getBlogByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let blogData = response.data;
          // 设置meta信息
          this.$commonUtil.setMetaInfo(blogData.title, blogData.summary, blogData.summary)
          this.blogUid = response.data.uid
          this.commentInfo.blogUid = response.data.uid
          this.blogOid = response.data.oid
          this.subjectItemList = response.data.subjectItemList
          this.subject = response.data.subject
          //获取文章复制权限
          this.isCopy = response.data.isCopy
          this.blogData = blogData
          // 获取相同的博客
          this.getSameBlog()
          // 获取当前的用户是否关注作者
          this.getCheckCurrentUserWatch()
          this.checkLoadingMore()
          // 获取用户信息
          this.getUserInfo(blogData.userUid)

          // 判断文章是否通过审核
          if (blogData.isPublish == "0" || blogData.auditStatus != "2") {
            this.$message.warning("文章暂未发布或提交审核，仅支持自己查看~")
          }
          // 转换博客内容
          let content = this.$commonUtil.replaceImg(blogData.content)
          this.blogContent = content

          // 获取切换专栏按钮
          this.getSubjectIndex()

          let generalInfo = {}
          generalInfo.bizUid = blogData.uid
          generalInfo.bizType = 1;
          generalInfo.reason = null
          generalInfo.summary = blogData.title
          generalInfo.content = blogData.content
          generalInfo.oldContent = blogData.content
          this.generalInfo = generalInfo
          // this.mathJax()

          setTimeout(() => {
            that.blogContent = content
            that.loadingInstance.close();
          }, 500)

          // 5S后埋点信息上报
          setTimeout(() => {
            this.recordSubjectProcess()
            recorderBehavior({
              "otherData": blogData.title,
              "bizUid": blogData.uid,
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
    dashangToggle: function () {
      this.showPay = !this.showPay;
    },
    openEditGeneral: function () {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以勘误哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      this.dialogEditVisible = true;
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_level', 'sys_user_tag']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userLevelDictList = dictMap.sys_user_level.list
          this.userTagDictList = dictMap.sys_user_tag.list
        }
      });
    },
    handleCurrentChange: function (val) {
      this.currentPage = val;
    },
    resizeWin () {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.dialogSize = "35%"
      } else if (centerWidth > 1000) {
        this.dialogSize = "50%"
      } else if (centerWidth > 600) {
        this.dialogSize = "60%"
      } else {
        this.dialogSize = "95%"
        this.imageWidth = "100%"
      }
      this.leftNavigationWidth = (document.body.clientWidth - 1200) / 2
      console.log("浏览器大小变动", this.leftNavigationWidth)
    },
    closeViewer () {
      this.showViewer = false
    },
    report: function (item) {
      if (item.articleSource == "0") {
        this.$message.error("无法举报该文章");
        return;
      }
      let reportInfo = {}
      reportInfo.reportType = "blog" // 评论
      reportInfo.reportUserUid = item.userUid // 被举报人id
      reportInfo.reportContentUid = item.uid
      reportInfo.reportContent = item.title
      this.reportInfo = reportInfo
      this.dialogReportVisible = true
    },
    beforeClose: function () {
      this.dialogReportVisible = false
    },
    // 跳转到个人中心页
    getUserCenter: function (blog) {
      console.log("跳转到用户中心", blog)
      // 判断是后台添加，还是前台投稿
      if (blog.articleSource == 0) {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: { adminUid: blog.adminUid }
        });
        window.open(routeData.href, '_blank');
      } else {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: { userUid: blog.userUid }
        });
        window.open(routeData.href, '_blank');
      }
    },
    // 获取用户关注的状态
    getCheckCurrentUserWatch: function () {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        return;
      }
      let params = {}
      let blogData = this.blogData
      if (blogData.articleSource === 1) {
        params.toUserUid = blogData.userUid
      } else {
        params.toUserUid = blogData.adminUid
      }
      checkCurrentUserWatch(params).then(response => {
        if (response.code === this.$ECode.SUCCESS) {
          this.userWatchStatus = response.data
        }
      })
    },
    getUserInfo (userUid) {
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
    // 改变显示状态
    changeShowSubject: function () {
      console.log("是否开启", this.showSubjectBtn)
      if (this.showSubjectBtn) {
        setCookie("subjectShow", "false", 31)
        this.showSubjectBtn = false
      } else {
        setCookie("subjectShow", "true", 31)
        this.showSubjectBtn = true
      }
    },
    steepReadCallBack: function () {
      if (this.showSteepRead) {
        setCookie("showSteepRead", "false", 31)
        this.showSteepRead = false
      } else {
        setCookie("showSteepRead", "true", 31)
        this.showSteepRead = true
      }
    },
    // 判断文章能否加载全部
    checkLoadingMore () {
      let blogData = this.blogData
      // 如果文章权限校验通过，直接返回
      if (blogData.visitAuthSuccess) {
        this.loadingMore = true
        return
      }
    },
    clickMore (toggle) {
      this.toggle = toggle
      // 从cookie中获取校验码
      let validCode = getCookie("validCode")
      if (validCode) {
        toggle()
        return
      }
      getLoadingValid().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          setCookie("validCode", response.data, 0)
          toggle()
        } else {
          this.dialogVisible = true
        }
      })
    },
    submitCode: function () {
      let validCode = this.validCode
      if (validCode.length != 6) {
        this.$message.error("验证码长度必须为6位")
        return
      }
      let params = new URLSearchParams()
      params.append("validCode", validCode)
      checkValidCode(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success("校验成功~")
          setCookie("validCode", response.data, 0)
          this.dialogVisible = false
          this.toggle()
        } else {
          this.$message.error(response.message)
        }
      })
    },
    getSameBlog () {
      let blogParams = new URLSearchParams();
      blogParams.append("blogUid", this.blogUid);
      getSameBlogByBlogUid(blogParams).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.sameBlogData = response.data.records;
        }
      });
    },
    // 设置是否开启评论和赞赏
    setWebConfig () {
      let webConfigData = this.$store.state.app.webConfigData
      if (webConfigData.createTime) {
        this.webConfigData = webConfigData
        this.openAdmiration = webConfigData.openAdmiration
        this.openComment = webConfigData.openComment
        this.componentShowMap = webConfigData.componentShowMap
      }
    },
    //跳转到搜索详情页
    goToTagList (uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { tagUid: uid }
      });
      window.open(routeData.href, "_blank");
    },
    //跳转到搜索详情页
    goToSortList (uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { sortUid: uid }
      });
      window.open(routeData.href, "_blank");
    },
    //跳转到搜索详情页
    goToAuthor (author) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { author: author }
      });
      window.open(routeData.href, "_blank");
    },
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showViewer = true
        this.picList = [e.target.currentSrc]
      }
    },
    //切割字符串
    subText: function (str, index) {
      let resultStr = ""
      let tempCount = 0
      for (let i = 0; i < str.length; i++) {
        let asc = str.charCodeAt(i);
        if ((asc >= 48 && asc <= 90 || asc >= 97 && asc <= 122)) {
          tempCount += 0.5
        } else {
          tempCount += 1
        }
        if (tempCount > index) {
          return resultStr + "..."
        }
        resultStr += str[i]
      }
      return resultStr;
    },
    // 获取专栏位置
    getSubjectIndex () {
      let blogUid = this.blogUid
      let index = -1;
      let subjectItemList = this.subjectItemList
      if (!subjectItemList || subjectItemList.length == 0) {
        return
      }
      for (let a = 0; a < subjectItemList.length; a++) {
        // 判断当前文章所出的专栏位置
        if (subjectItemList[a].blogUid == blogUid) {
          index = a;
        }
      }
      if (index <= 0) {
        this.showPreButton = false
      }
      if (index == subjectItemList.length) {
        this.showNextButton = false
      }
      return index;
    },
    preArticle () {
      let index = this.getSubjectIndex()
      let subjectItemList = this.subjectItemList
      if (index <= 0 || !subjectItemList[index - 1].blog) {
        this.showPreButton = false
        return
      }
      this.$router.push({ path: "/info/" + subjectItemList[index - 1].blog.oid });
    },
    nextArticle () {
      // 判断是否在专栏最后一个
      let index = this.getSubjectIndex()
      let subjectItemList = this.subjectItemList
      if (index >= subjectItemList.length || !subjectItemList[index + 1].blog) {
        this.showNextButton = false
        return
      }
      this.$router.push({ path: "/info/" + subjectItemList[index + 1].blog.oid });
    },
  }
};
</script>

<style>
/*.newsview {*/
/*  min-height: 400px;*/
/*}*/

.emoji-panel-wrap {
  box-sizing: border-box;
  border: 1px solid #cccccc;
  border-radius: 5px;
  background-color: #ffffff;
  width: 470px;
  height: 190px;
  position: absolute;
  z-index: 999;
  top: 10px;
}

.emoji-size-small {
  zoom: 0.3;
  margin: 5px;
  vertical-align: middle;
}

.emoji-size-large {
  zoom: 0.5;
  margin: 5px;
}

.iconfont {
  font-size: 14px;
  margin-right: 3px;
}

.message_infos {
  width: 96%;
  margin-left: 10px;
}

.noComment {
  width: 100%;
  text-align: center;
}

.catalog {
  position: fixed;
  /*margin-left: 20px;*/
}

.catalog2 {
  position: fixed;
  /*margin-left: 20px;*/
  top: 70px;
}

.catalog3 {
  position: fixed;
  /*margin-left: 20px;*/
  top: 20px;
}

.subject {
  position: fixed;
  top: 70px;
}

::-webkit-scrollbar {
  width: 0;
  height: 0px;
}

.subject1 {
  position: fixed;
  top: 20px;
}

.subject2 {
  position: fixed;
  top: 70px;
}

.line-style {
  display: inline-block;
  height: 20px;
  width: 3px;
  background: transparent;
}

.line-style--active {
  background: currentColor;
}

.vue-foldable-container {
  transition: max-height 0.3s;
}

.vue-foldable-mask {
  transition: opacity 3s;
}

.vue-foldable-container {
  transition: max-height 0.3s;
}

.vue-foldable-mask {
  transition: opacity 3s;
  bottom: 36px;
}

.vue-foldable .vue-foldable-mask.collapsed {
  /*margin-bottom: 10px;*/
  bottom: 200px;
}

.csdn-view-more {
  margin: 16px auto;
  width: 121px;
  background-color: #fff;
  transition: background-color 0.1s ease-in-out;
  color: #ca0c16;
  border-color: #ca0c16;
  border-width: 1px;
  border-style: solid;
  padding: 0 8px;
  font-size: 14px;
  border-radius: 4px;
  text-align: center;
  height: 34px;
  line-height: 34px;
  min-width: 72px;
  cursor: pointer;
}

.subjectSpan {
  color: rgb(0, 107, 255);
}

.subjectDefaultSpan {
  font-weight: 600;
}

.subjectItemCard:hover {
  background: #e0e0e0;
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
  color: #38485a;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.side-catalog div {
  background: #fff;
}

/deep/ .el-progress-bar__innerText {
  background: none;
}
</style>
