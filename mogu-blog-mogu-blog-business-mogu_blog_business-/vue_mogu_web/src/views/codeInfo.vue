<template>
  <article style="position: relative;">
    <el-image-viewer
      v-if="showViewer"
      :on-close="closeViewer"
      :url-list="picList" />

    <h1 class="t_nav">
      <a href="/" class="n1">网站首页</a>
      <a
        href="javascript:void(0);"
        v-if="problemData.probleTagList"
        @click="goToSortList(problemData.probleTagList.uid)"
        class="n2"
      >{{problemData.probleTagList ? problemData.probleTagList.name :""}}</a>
    </h1>

    <div class="infosbox" >
      <div class="newsview" style="min-height: 330px">
        <h3 class="news_title" v-if="problemData.title">{{problemData.title}}</h3>
        <div class="bloginfo">
          <ul>
            <li style="padding-right: 6px; text-align: center;">
              <div :class="problemData.user.userTag > 0 ?'vip-avatar':''">
                <el-avatar  :class="problemData.user.userTag > 0 ?'vip-color':''" v-if="problemData.user" fit="fill" size="medium"  :src="problemData.user.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" size="small" src="https://empty">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: 1px;" class="vip-text pointer" v-if="problemData.user.userTag > 0">v</span>
              </div>
            </li>
            <li class="author" style="margin-top: 8px; margin-left: 4px;">
              <span class="pointer" :class="'lv'+ problemData.user.userLevel" v-if="problemData.user" @click="getUserCenter(problemData)">{{problemData.user.nickName}}</span>
            </li>

            <li class="view" style="margin-top: 8px;">
              <span class="iconfont">&#xe8c7;</span>
              {{problemData.clickCount}}
            </li>

            <li class="createTime" style="margin-top: 8px;">
              <span class="iconfont">&#xe606;</span>
              {{problemData.createTime}}
            </li>

            <li class="view" style=" margin-top: 8px; cursor: pointer;" @click="report(problemData)">
              <span class="iconfont" >&#xe65b;</span>举报
            </li>

            <li class="view" style="margin-top: 8px;">
              <Collection :bizUid="problemData.uid" :collectType="'6'"
              ></Collection>
            </li>

            <li class="view" style="margin-top: 8px;">
              <Praise :bizUid="problemData.uid" :resourceType="'6'"
              ></Praise>
            </li>

            <!-- 勘误修正按钮  -->
            <li class="view" style="margin-top: 8px; cursor: pointer;" @click="openEditGeneral">
              <span class="el-icon-edit-outline"></span>
              <span class="iconfont" ></span>勘误
            </li>

          </ul>
        </div>

        <div style="margin-top: 10px">
          <el-tag class="pointer" @click="getToProblem(problemData, '1')" size="mini" v-if="problemData.isSelection == '1'" effect="dark" type="warning">精选</el-tag>
          <el-tag class="pointer" size="mini" @click="getToProblem(problemData, '2')" effect="plain" v-for="problemType in problemTypeDictList" :key="problemType.uid" v-if="problemData.problemType == problemType.dictValue" :type="problemType.listClass">{{problemType.dictLabel}}</el-tag>
          <el-tag class="pointer" size="mini" @click="getToProblem(problemData, '3')" effect="plain" v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" v-if="problemData.problemDifficulty == problemDifficulty.dictValue" :type="problemDifficulty.listClass">{{problemDifficulty.dictLabel}}</el-tag>

          <span v-for="(problemTag, index) in problemData.problemTagList">
            <el-tag class="pointer" size="mini" @click="getToProblem(problemTag.uid, '4')" effect="plain"  style="margin-right: 3px" v-if="index%3==0" type="primary">{{problemTag.name}}</el-tag>
            <el-tag class="pointer" size="mini" @click="getToProblem(problemTag.uid, '4')" effect="plain"  style="margin-right: 3px" v-if="index%3==1" type="danger">{{problemTag.name}}</el-tag>
            <el-tag class="pointer" size="mini" @click="getToProblem(problemTag.uid, '4')" effect="plain"  style="margin-right: 3px" v-if="index%3==2" type="info">{{problemTag.name}}</el-tag>
          </span>
        </div>

        <div class="tip">
          <strong>描述</strong>
          <span class="ck-content"  v-highlight v-html="problemData.content"
                @click="imageChange"
                v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }">
            {{problemData.content}}
          </span>
        </div>

        <el-button v-if="!showAnswer" @click="handleShowAnswer" type="success">查看解析</el-button>
        <el-button v-else type="danger" @click="handleShowAnswer">隐藏解析</el-button>
        <el-button  type="primary" style="margin-top: 3px" @click="handleProblemDegree('3')">已掌握</el-button>
        <el-button  type="info" style="margin-top: 3px" @click="handleProblemDegree('1')">未掌握</el-button>

        <div class="tip-success" v-if="showAnswer">
          <strong>解析</strong>
          <span class="ck-content"  v-highlight v-html="problemData.answer"
                @click="imageChange"
                v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }">
            {{problemData.answer}}
          </span>
        </div>

      </div>

      <!--社会化分享-->
      <share v-if="loadingMore" :config="config" style="text-align: center"></share>

      <div class="news_pl" :style="opemCommentCss">
        <h2 v-if="openComment == '1'" class="title">问题评论</h2>
        <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel"  :targetId="commentInfo.blogUid" :tableName="commentInfo.source"></Comment>
      </div>

    </div>

    <div class="sidebar2" v-if="showSidebar">
      <div class="sidebarDiv cloud" style="border-radius: 3px; padding-bottom: 5px; padding-top: 5px; ">
        <el-row>
          <el-col :span="5" style="text-align: center">
              <span :class="problemData.user.userTag > 0 ?'vip-avatar':''">
                <el-avatar :class="problemData.user.userTag > 0 ?'vip-color':''" style="cursor: pointer;" shape="circle"  @click.native="getUserCenter(problemData)" :size="50" v-if="problemData.user" fit="fill"  :src="problemData.user.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" :size="50" src="https://empty" shape="circle">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="problemData.user.userTag > 0">v</span>
              </span>

          </el-col>
          <el-col :span="18">
            <div v-if="problemData.user" style="cursor: pointer">
              <el-row>
                <el-col>
                    <span  @click="getUserCenter(problemData)"  class="blackFont"  style="font-size: 16px;font-weight: 600; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                      {{problemData.user.nickName}}
                    </span>

                    <span>
                      <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="problemData.user.userTag == userTag.dictValue && problemData.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>
                    </span>

                    <span>
<!--                      <el-tag size="mini" :class="'lv'+ problemData.user.userLevel" effect="plain"> Lv{{problemData.user.userLevel}} </el-tag>-->
                     <span class="lv" :class="'lv'+ problemData.user.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="problemData.user.userLevel == userLevel.dictValue">
                            {{userLevel.remark}}
                     </span>
                    </span>

                    <span class="iconfont" v-if="problemData.user.gender=='1'" style="margin-left: 1px; color: dodgerblue;  font-size: 16px;">&#xe641;</span>
                    <span class="iconfont" v-if="problemData.user.gender=='2'" style="margin-left: 1px; color: palevioletred;  font-size: 16px;">&#xe643;</span>


                </el-col>
              </el-row>
            </div>
            <div v-if="problemData.user" style="font-size: 14px;color: #72777b; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; ">
              {{problemData.user.summary}}
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="5" style="text-align: center">
            <i class="el-icon-medal-1" style="color: #d4237a; font-size: 26px;"></i>
          </el-col>
          <el-col :span="18">
            <span class="blackFont">
              发表问题 {{problemData.user.problemPublishCount}} 篇
            </span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5" style="text-align: center">
            <i class="el-icon-trophy-1" style="color: #1296db; font-size: 26px;"></i>
          </el-col>
          <el-col :span="18">
            <span class="blackFont">
              发表问题 {{problemData.user.blogPublishCount}} 篇
            </span>
          </el-col>
        </el-row>

        <el-row type="flex" justify="space-around" v-if="problemData.userUid != loginUserInfo.uid" style="margin-bottom: 5px; margin-top: 5px;">
          <el-col :span="10">
            <el-button style="width: 100%" v-if="userWatchStatus == 0" icon="el-icon-sugar" type="danger" @click="watchOtherUser()" size="mini" round> <span>&nbsp;关&nbsp;注&nbsp;</span> </el-button>
            <el-button style="width: 100%" v-if="userWatchStatus == 1" icon="el-icon-lightning" type="info" @click="unWatchOtherUser()" size="mini" round>取消关注</el-button>
            <el-button style="width: 100%" v-if="userWatchStatus == 2" icon="el-icon-ship" type="success" @click="unWatchOtherUser()" size="mini" round>互相关注</el-button>
          </el-col>
          <el-col :span="10">
            <el-button style="width: 100%"  icon="el-icon-chat-dot-round" type="primary" @click="goChat()" size="mini" round>&nbsp;私&nbsp;信&nbsp;</el-button>
          </el-col>
        </el-row>

      </div>
    </div>

    <SameProblem :sameProblemList="problemData.sameProblemList" style="background: #fff;"></SameProblem>


    <!-- 添加或修改对话框 -->
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
          <el-input placeholder="请输入验证码(6位)" style="width: 200px" v-model="validCode" @keyup.enter.native="submitCode"></el-input>
        </div>
        <div style="margin-top: 10px">
          <el-button type="primary" style="width: 200px" @click="submitCode">提交</el-button>
        </div>
      </div>
    </el-dialog>

    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo" @beforeClose="beforeClose"></Report>
    <GeneralEdit v-if="dialogEditVisible" :visible="dialogEditVisible" :generalInfo="generalInfo" @beforeCloseEdit="beforeCloseEdit"></GeneralEdit>

  </article>
</template>

@import "../assets/dist/css/share.min.css";
<script>

import Comment from "../components/Comment";
import {checkValidCode, getLoadingValid} from "@/api/user";
import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'
// vuex中有mapState方法，相当于我们能够使用它的getset方法
import { mapMutations } from "vuex";
import Collection from "../components/Collection";
import Praise from "../components/Praise";
import { Loading } from "element-ui";
import Sticky from "@/components/Sticky";
import Report from "@/components/Report"
import SameProblem from '@/components/SameProblem'
import {getCookie, setCookie} from "@/utils/cookieUtils";
import {addUserWatch, checkCurrentUserWatch, deleteUserWatch} from "../api/about";
import {getProblem, problemDegree, editAnswer} from "../api/problem";
import {getListByDictTypeList} from "@/api/sysDictData";
import GeneralEdit from "@/components/GeneralEdit";
import {recorderBehavior} from "../api";
import config from '../../conf.json'
export default {
  name: "info",
  data() {
    return {
      dialogEditVisible: false,
      direction: 'rtl',
      changeCount: 0, // 改变计数器
      form: {},
      formLabelWidth: "100px",
      showAnswer: false,
      showEdit: false,
      showViewer: false,
      picList: [],
      typeList: ['warning', 'danger', 'success', 'info','primary'],
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
      showStickyTop: false,
      showSidebar: true, //是否显示侧边栏
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
        source: "PROBLEM_INFO",
        maxReplyLevel: 4, // 最大回复深度
        blogUid: null,
      },
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      toInfo: {},
      userWatchStatus: 0,// 当前用户关注状态
      loginUserInfo: {}, // 当前登录用户的信息
      userInfo: {

      },
      problemTypeDictList: [], // 问题类型字典
      problemDifficultyDictList: [], // 问题难度字典
      dialogReportVisible: false, // 控制举报弹出
      reportInfo: {}, // 举报信息
      blogUid: null, //传递过来的问题uid
      problemOid: 0, // 传递过来的问题oid
      problemData: {
        blogSort: {},
        content: "",
        user: {

        }
      },
      defaultAvatar: process.env.defaultAvatar, // 默认头像
      webConfig: {}, // 网站配置
      sameProblemData: [], //相关问题
      openComment: "0", // 开启评论
      openAdmiration: "0", // 开启赞赏
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      userLevelDictList: [],
      userTagDictList: [],
      config:{
        sites: ['qq', 'qzone', 'weibo', 'wechat', 'douban'],
      },
      generalInfo: {}, // 问答信息
      rules: {
        reason: [
          {required: true, message: '修改原因不能为空', trigger: 'blur'},
          {min: 2, max: 50, message: '长度在2到50个字符'},
        ],
        content: [
          {required: true, message: '解析内容不能为空', trigger: 'blur'}
        ],
      }
    };
  },
  computed: {
    opemCommentCss: function () {
      if(this.openComment == 0) {
        return {
          "min-height": "10px"
        }
      }
    }
  },
  components: {
    //注册组件
    Sticky,
    Comment,
    Report,
    Collection,
    Praise,
    ElImageViewer,
    SameProblem,
    GeneralEdit
  },
  metaInfo() {
    return {
      title: this.problemData.title ? this.problemData.title : config.title,
      meta: [
        {
          name: "keywords",
          content: this.problemData.title,
        },
        {
          name: "content",
          content: this.problemData.title,
        },
        {
          name: "description",
          content: this.problemData.summary,
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
  mounted () {
    this.loadingInstance = Loading.service({
      fullscreen: true,
      text: "正在努力加载中~"
    });
    this.loginUserInfo = this.$store.state.user.userInfo
    let that = this;
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    this.resizeWin();

    let params = {};
    params.oid = this.problemOid
    // 开启图片懒加载
    params.isLazy =  "1"
    getProblem(params).then(response => {
      if (response.code == this.$ECode.SUCCESS) {
        let problemData = response.data;
        // 设置meta信息
        this.$commonUtil.setMetaInfo(problemData.title, problemData.summary, problemData.summary)
        this.problemData = problemData
        this.blogUid = response.data.uid
        this.commentInfo.blogUid = response.data.uid
        this.problemOid = response.data.oid
        // 获取当前的用户是否关注作者
        this.getCheckCurrentUserWatch()
        this.checkLoadingMore()
        // 判断问题是否通过审核
        if(problemData.isPublish == "0" || problemData.auditStatus != "2") {
          this.$message.warning("问题暂未发布或提交审核，仅支持自己查看~")
        }
        // 转换问题内容
        let content = this.$commonUtil.replaceImg(problemData.content)
        this.blogContent = content
        this.$nextTick(() => {
          that.blogContent = content
          that.loadingInstance.close();
        });

        //设置修改解析 富文本框内容
        this.form.content = problemData.answer;
        this.form.summary = problemData.content;

        that.form = that.getFormObject(problemData);

        let generalInfo = {}
        generalInfo.bizUid = problemData.uid
        generalInfo.bizType = 6;
        generalInfo.reason = null
        generalInfo.summary = problemData.title
        generalInfo.content = problemData.answer
        generalInfo.oldContent = problemData.answer
        this.generalInfo = generalInfo

        // 5S后埋点信息上报
        setTimeout(()=>{
          recorderBehavior({"otherData": problemData.title, "bizUid": problemData.uid, "bizType": "PROBLEM" , "behavior": "PROBLEM"}).then(response => {})
        }, 5000)

      } else {
        that.loadingInstance.close();
      }
    },function(err){
      this.loadingInstance.close();
    });

    let after = 0;
    let offset = 200
    $(window).scroll(function () {
      let docHeight = $(document).height(); // 获取整个页面的高度(不只是窗口,还包括为显示的页面)
      let winHeight = $(window).height(); // 获取当前窗体的高度(显示的高度)
      let winScrollHeight = $(window).scrollTop(); // 获取滚动条滚动的距离(移动距离)

      if (winScrollHeight < offset) {
        that.showStickyTop = false
      } else {
        that.showStickyTop = true
      }
      after = winScrollHeight;
    })

    // 屏幕自适应
    window.onresize = () => {
      return (() => {
        // 屏幕大于950px的时候，显示侧边栏
        that.showSidebar = document.body.clientWidth > 1200
      })()
    }
  },
  created() {
    this.getDictList()
    // 查看解析
    let showAnswer = getCookie("showAnswer")
    if (showAnswer) {
      if(showAnswer == "false") {
        this.showAnswer = false
      } else {
        this.showAnswer = true
      }
    }

    this.problemOid = this.$route.params.problemOid;
    // 屏幕大于1200px的时候，显示侧边栏
    this.showSidebar = document.body.clientWidth > 1200

    this.webConfig = this.$store.state.app.webConfigData
    this.setWebConfig()
  },
  methods: {
    //拿到vuex中的写的两个方法
    ...mapMutations(["setLoginMessage", "setCommentList", "setWebConfigData"]),
    handleCurrentChange: function(val) {
      this.currentPage = val;
    },
    // 取消关注用户
    goChat: function (toUserUid) {
      let blogData = this.blogData
      if(blogData.articleSource == 1) {
        this.$router.push({ path: "/chat", query: {userUid: blogData.userUid}});
      } else {
        this.$message.error("暂时无法发起私信")
      }
    },
    handleShowAnswer: function () {
      this.showAnswer = !this.showAnswer
      setCookie("showAnswer", this.showAnswer, 31)
    },
    beforeCloseEdit: function () {
      this.dialogEditVisible = false
    },
    openEditGeneral: function () {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
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
    // 提交掌握记录
    handleProblemDegree(degreeStatus) {
      let params = {}
      params.problemUid = this.problemData.uid
      params.degree = degreeStatus
      params.title = this.problemData.title
      problemDegree(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          // 已阅读不弹出
          if (degreeStatus != '2') {
            this.$message.success(response.message)
          }
        } else {
          this.$message.error(response.message)
        }
      })
    },
    getToProblem(problem, type) {
      switch (type) {
        case '1': {
          this.$router.push({
            path: "/code",
            query: {isSelection: problem.isSelection}
          });
        } break;
        case '2': {

          this.$router.push({
            path: "/code",
            query: {problemType: problem.problemType}
          });
        } break;
        case '3': {
          this.$router.push({
            path: "/code",
            query: {problemDifficulty: problem.problemDifficulty}
          });
        } break;
        case '4': {

          this.$router.push({
            path: "/code",
            query: {tagUid: problem}
          });

        } break;
      }
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_problem_difficulty', 'sys_problem_type', 'sys_user_level', 'sys_user_tag']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          this.userLevelDictList =  dictMap.sys_user_level.list
          this.userTagDictList =  dictMap.sys_user_tag.list
          if(dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if(dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }
        }
      });
    },
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.dialogSize = "35%"
      } else if(centerWidth > 1000) {
        this.dialogSize = "50%"
      } else if(centerWidth > 600) {
        this.dialogSize = "60%"
      } else {
        this.dialogSize = "95%"
        this.imageWidth = "100%"
      }


    },
    closeViewer() {
      this.showViewer = false
    },
    report: function (item) {
      if (item.articleSource == "0") {
        this.$message.error("无法举报该问题");
        return;
      }
      let reportInfo = {}
      reportInfo.reportType = "problem" // 评论
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
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: blog.userUid}
      });
      window.open(routeData.href, '_blank');
    },
    // 获取用户关注的状态
    getCheckCurrentUserWatch: function () {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
        return;
      }
      let params = {}
      let problemData = this.problemData
      console.log(this.problemData)
      if(problemData.articleSource == 1) {
        params.toUserUid = problemData.userUid
      } else {
        if (problemData.adminUid != undefined && problemData.adminUid != '' ) {
          params.toUserUid = problemData.adminUid
        }else {
          params.toUserUid = problemData.userUid
        }
      }
      checkCurrentUserWatch(params).then(response => {
        if(response.code ==  this.$ECode.SUCCESS) {
          this.userWatchStatus = response.data
        }
      })
    },
    // 关注用户
    watchOtherUser: function () {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
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
      let problemData = this.problemData
      if(problemData.articleSource == 1) {
        params.toUserUid = problemData.userUid
      } else {
        params.isAdmin = "0"
        params.toUserUid = problemData.adminUid
      }
      addUserWatch(params).then(response => {
        console.log("关注用户", response)
        if(response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userInfo.isWatchUser = true
          this.userWatchListData = []
          this.currentPage = 1
          this.getCheckCurrentUserWatch()
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 取消关注用户
    unWatchOtherUser: function (toUserUid) {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
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
      let problemData = this.problemData
      if(problemData.articleSource == 1) {
        params.toUserUid = problemData.userUid
      } else {
        params.toUserUid = problemData.adminUid
      }

      deleteUserWatch(params).then(response => {
        console.log("取消关注用户", response)
        if(response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userWatchListData = []
          this.currentPage = 1
          this.getCheckCurrentUserWatch()
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 判断问题能否加载全部
    checkLoadingMore() {
      let problemData = this.problemData
      let webConfig = this.webConfig
      if(problemData.openLoadingValid == "0" || webConfig.openLoadingValid == "0") {
        this.loadingMore = true
        return
      }
      // 从cookie中获取校验码
      let validCode = getCookie("validCode")
      if(validCode) {
        this.loadingMore = true
        return
      }
      getLoadingValid().then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          setCookie("validCode", response.data, 0)
          this.loadingMore = true
        }
      })
    },
    clickMore(toggle) {
      this.toggle = toggle
      // 从cookie中获取校验码
      let validCode = getCookie("validCode")
      if(validCode) {
        toggle()
        return
      }
      getLoadingValid().then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          setCookie("validCode", response.data, 0)
          toggle()
        } else {
          this.dialogVisible = true
        }
      })
    },
    submitCode: function () {
      let validCode = this.validCode
      if(validCode.length != 6) {
        this.$message.error("验证码长度必须为6位")
        return
      }
      let params = new URLSearchParams()
      params.append("validCode", validCode)
      checkValidCode(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.$message.success("校验成功~")
          setCookie("validCode", response.data, 0)
          this.dialogVisible = false
          this.toggle()
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 设置是否开启评论和赞赏
    setWebConfig() {
      let webConfigData = this.$store.state.app.webConfigData
      if(webConfigData.createTime) {
        this.openAdmiration = webConfigData.openAdmiration
        this.openComment = webConfigData.openComment
      }
    },
    //跳转到搜索详情页
    goToTagList(uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { tagUid: uid }
      });
      window.open(routeData.href, "_blank");
    },
    //跳转到搜索详情页
    goToSortList(uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { sortUid: uid }
      });
      window.open(routeData.href, "_blank");
    },
    //跳转到搜索详情页
    goToAuthor(author) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { author: author }
      });
      window.open(routeData.href, "_blank");
    },
    imageChange: function(e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showViewer = true
        this.picList = [e.target.currentSrc]
      }
    },
    //切割字符串
    subText: function(str, index) {
      let resultStr = ""
      let tempCount = 0
      for (let i=0; i<str.length; i++) {
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
    //切割字符串
    subTextCount: function(str) {
      let tempCount = 0
      for (let i=0; i<str.length; i++) {
        let asc = str.charCodeAt(i);
        if ((asc >= 48 && asc <= 90 || asc >= 97 && asc <= 122)) {
          tempCount += 0.5
        } else {
          tempCount += 1
        }
      }
      return tempCount;
    },
    //关闭抽屉 修改解析
    handleClose(done) {
      this.$confirm("是否关闭解析编辑窗口", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.isChange = false;
          this.changeCount = 0
          //取消时，开始状态
          this.$emit("handleClose", "");
          done();
        })
        .catch(() => {
          // this.$commonUtil.message.info("已取消")
        });
      // this.$confirm('确认关闭？')
      //   .then(_ => {
      //     done();
      //   })
      //   .catch(_ => {});
    },
    // 内容改变，触发监听
    contentChange: function() {
      console.log("内容改变")
      let that = this;
      if(that.changeCount > 1) {
        that.isChange = true;
        that.form.content = that.$refs.editor.getData(); //获取文本中的内容
        // 将内容设置到 WebStorage中
        console.log("备份的内容", that.form)
      }
      this.changeCount = this.changeCount + 1;
    },
    submitForm: function () {
      editAnswer(this.form).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$commonUtil.message.success(response.message)
          // 清空缓存
          window.localStorage.clear()
          // this.$emit("beforeClose", "");
          setTimeout(()=> {
            location.reload();
          }, 500)
        } else {
          this.$alert(response.message, '警告', {
            confirmButtonText: '确定',
            callback: action => {
              this.$message({
                type: 'info',
                message: `请稍后再试!`
              });
            }
          });
          this.$commonUtil.message.error(response.message)
        }
      });

    },
    getFormObject: function(problemData) {
      let formObject = {
        uid: problemData.uid,
        reason: null,
        summary: problemData.content,
        content: problemData.answer,
      };
      return formObject;
    },
  }
};
</script>

<style>

.tip-success {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #e7f6e0;
  border-radius: 4px;
  border-left: 5px solid #67c23a;
  color: #888888;
}
.tip-success strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.newsview {
  min-height: 400px;
}
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
}
.catalog2 {
  position: fixed;
  top: 70px;
}
.catalog3 {
  position: fixed;
  top: 20px;
}

.subject{
  position: fixed;
  left: 50px;
  top: 70px;
}
::-webkit-scrollbar {
  width: 0;
  height: 0px;
}

.subject1 {
  position: fixed;
  left: 50px;
  top: 20px;
}
.subject2 {
  position: fixed;
  left: 50px;
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
  margin-bottom: 10px;
}
.csdn-view-more {
  margin: 16px auto;
  width: 121px;
  background-color: #fff;
  transition: background-color .1s ease-in-out;
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
  background: #E0E0E0;
}

.editAnswerReason {
  margin-top: 100px;
  margin-left: -40px;
}
.editAnswerSubmit {
  margin-left: 100px;
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

@media screen and (max-width: 1200px) {
  .sidebar2 {
    width: 100%
  }
}
</style>
