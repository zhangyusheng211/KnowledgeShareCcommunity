<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">

      <el-row style="min-height: 820px"  :gutter="20" >
        <el-col :span="4" :xs="0">
          <Sticky :sticky-top="10" >
            <div @click="selectTopic('', '最新')"  class="subjectItemCard" :class="[selectTopicContent == '最新' ? 'topicStyle' : '']" :body-style="{ padding: '0px' }" style="height:40px; line-height: 40px; text-align: center; cursor: pointer; font-size: 14px;">
              <span>最新</span>
            </div>

            <div @click="selectTopic('', '关注')"  class="subjectItemCard" :class="[selectTopicContent == '关注' ? 'topicStyle' : '']" :body-style="{ padding: '0px' }" style="height:40px; line-height: 40px; text-align: center; cursor: pointer; font-size: 14px;">
              <span>关注</span>
            </div>
            <!--          <el-card shadow="hover" @click.native="selectTopic('', '最热')"  class="subjectItemCard" :class="[selectTopicContent == '最热' ? 'topicStyle' : '']" :body-style="{ padding: '0px' }" style="height:30px; line-height: 30px; text-align: center; cursor: pointer; font-size: 13px;">-->
            <!--            <span>最热</span>-->
            <!--          </el-card>-->
            <div v-for="momentTopic in momentTopicList">
              <a>
                <div @click="selectTopic(momentTopic.uid, momentTopic.content)"  class="subjectItemCard" :class="[momentTopic.content == selectTopicContent ? 'topicStyle' : '']" :body-style="{ padding: '0px' }" style="height:40px; line-height: 40px; text-align: center; cursor: pointer; font-size: 14px;">
                  <span>{{momentTopic.content}}</span>
                </div>
              </a>
            </div>
          </Sticky>

        </el-col>

        <el-col :span="18" :xs="24">
          <el-card shadow="hover">
            <MomentBox
              :showAvatar="false"
              :commentInfo="momentInfo"
            ></MomentBox>
          </el-card>



          <el-card shadow="hover" style="position: relative; margin-top: 5px; padding-bottom: 5px;" v-for="(userMoment, index) in momentList" :key="userMoment.uid" class="commentList">


            <div v-if="userMoment.isTop == 1" style="position: absolute; right: 0; top: 0; font-size:15px;">
              <el-tooltip class="item" effect="dark" content="置顶" placement="top">
                <img style="width: 30px; height: 30px" src="../assets/img/top.png" />
              </el-tooltip>
            </div>


            <span  @mouseover="onEnterTd(userMoment.user, index)" @mouseleave="onLeaveTd(false)" class="left p1" v-if="userMoment.user"  :class="userMoment.user.userTag > 0 ?'vip-avatar':''">
              <img :id="getTabIndex(index)"  :class="userMoment.user.userTag > 0 ?'vip-color':''" v-if="userMoment.user" style="cursor:pointer;" @click="getUserCenter(userMoment.user)" :src="userMoment.user.photoUrl ? userMoment.user.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
              <img v-else :src="defaultAvatar" />
              <span style="font-size: 10px; right: -10px; bottom: -3px;" class="vip-text pointer" v-if="userMoment.user.userTag > 0" @click.natice="getUserCenter(userMoment.user)">v</span>
            </span>

            <div class="right p1">
              <div class="rightTop" v-if="userMoment.user">
                <el-link class="userName" :underline="false" v-if="userMoment.user.nickName" @click="getUserCenter(userMoment.user)">{{userMoment.user.nickName}}</el-link>
                <el-tag size="mini" effect="plain" style=" margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="userMoment.user.userTag == userTag.dictValue && userMoment.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

                <span class="timeAgo" >
                  <span class="lv" :class="'lv'+ userMoment.user.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="userMoment.user.userLevel == userLevel.dictValue">
                        {{userLevel.remark}}
                  </span>
                </span>

                <span class="iconfont" v-if="userMoment.user.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                <span class="iconfont" v-if="userMoment.user.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>

                <span class="timeAgo" style="color: #8a919f;" v-if="userMoment.user.occupation">{{userMoment.user.occupation}}</span>

                <span class="timeAgo" style="color: #8a919f;" v-if="userMoment.user.userIpPossession">IP属地:{{userMoment.user.userIpPossession}}</span>

                <span class="timeAgo" v-if="userMoment.createTime">{{timeAgo(userMoment.createTime)}}</span>

                <span class="timeAgo" v-else>刚刚</span>

                <el-link class="b1" :underline="false" style="float: right">
                  <Collection style="margin-top: -1px;" class="b1"
                              :bizUid="userMoment.uid"
                              :collectType="'4'"
                              :collectCountValue="userMoment.collectInfo.collectCount"
                              :isCollectValue="userMoment.collectInfo.isCollect"
                  ></Collection>
                </el-link>

                <el-link class="b1" :underline="false" style="float: right; margin-right: 10px;" @click="report(userMoment)"><span class="iconfont" >&#xe65b;</span>举报</el-link>

              </div>

              <div class="rightCenter ck-content" v-highlight
                   @contextmenu.prevent="onContextImageMenu"
                   @click="imageChange"
                   v-html="$xss(replaceImg(userMoment.content), options)"></div>

              <div>
                <el-tag
                  @click.native="selectTopic(userMomentTopic.uid, userMomentTopic.content)"
                  style="margin-left: 3px; cursor:pointer; margin-bottom: 5px"
                  :key="index"
                  v-for="(userMomentTopic, index) in userMoment.userMomentTopicList"
                  :type="typeList[index%5]"
                  hit
                  size="mini"
                  effect="light"
                >{{userMomentTopic.content}}</el-tag>
              </div>

              <div v-if="userMoment.url">
                <div class="link-container">
                  <div class="zone-link-part">
                    <span class="el-icon-link zone-link-bg"></span>
                    <div class="link-right-part"><span class="title">{{userMoment.urlInfo}}</span>
                      <a :href="userMoment.url" target="_blank" class="url">{{userMoment.url}}</a>
                    </div>
                  </div>
                </div>
              </div>

              <div class="demo-image__preview" style="margin-top: 10px;" v-if="userMoment.photoList">
                <el-image
                  v-for="(image, index) in userMoment.photoList"
                  :key="index"
                  fit="contain"
                  style="height: 150px; cursor:zoom-in; margin-right: 5px;"
                  :src="image"
                  :preview-src-list="userMoment.photoList">
                </el-image>
              </div>

              <div class="rightBottom">
                <el-link class="b1" :underline="false" @click="replyTo(userMoment)">回复</el-link>

                <el-link class="b1" v-if="$store.state.user.isLogin && $store.state.user.userInfo.uid == userMoment.userUid"  :underline="false" @click="deleteMoment(userMoment)">删除</el-link>

                <div style="display: inline-block;"  class="b1" >
                  <Praise style="margin-top: -2px;" class="b1"
                          :bizUid="userMoment.uid"
                          :resourceType="'4'"
                          :isPraiseValue="userMoment.praiseInfo.isPraise"
                          :isTreadValue="userMoment.praiseInfo.isTread"
                          :praiseCountValue="userMoment.praiseInfo.praiseCount"
                          :treadCountValue="userMoment.praiseInfo.treadCount"
                          @praiseCallback="praiseCallback"
                          @cancelPraiseCallback="cancelPraiseCallback"
                  ></Praise>
                </div>

                <div style="display: inline-block; float: right"  class="b1" >
                  <div v-if="userMoment.praiseUserList && userMoment.praiseUserList.length > 0">
                    <a v-for="(userInfo, index) in userMoment.praiseUserList" :key="userInfo.uid" style="float: left; color: #0d3d86" target="_blank" :href="'/userCenter?userUid=' + userInfo.uid">
                      <img v-if="index <= 10" :src="userInfo.photoUrl" class="like-user-avatar">
                    </a>
                    <a v-if="userMoment.praiseUserList.length > 10" style="float: left; color: #0d3d86">
                      ..
                    </a>
                    <span style="line-height: 24px; margin-left: 5px; color: #909399; font-size: 14px;">
                      {{userMoment.praiseUserList.length}} 人点赞
                    </span>
                  </div>
                </div>


                <CommentBox
                  v-if="currentBizUid === userMoment.uid"
                  :id="userMoment.uid"
                  :key="userMoment.uid"
                  class="comment"
                  :toInfo="toInfo"
                  :commentInfo="commentInfo"
                  @submit-box="submitBox"
                  @cancel-box="cancelBox"
                ></CommentBox>

                <!--二级评论列表-->
                <CommentList v-if="userMoment.commentList" :comments="userMoment.commentList" :commentInfo="commentInfo"></CommentList>
              </div>
            </div>
          </el-card>
        </el-col>

        <div class="isEnd">
          <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading">查看更多</div>
          <div class="lds-css ng-scope" v-if="!isEnd&&loading">
            <div style="width:100%;height:100%" class="lds-facebook">
              <div></div>
              <div></div>
              <div></div>
            </div>
          </div>
          <span v-if="isEnd && momentList.length == 0">
          <el-empty description="空空如也" image=""></el-empty>
        </span>
          <span v-if="isEnd && momentList.length > 0">我也是有底线的~</span>
        </div>

      </el-row>
    </div>

    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo" @beforeClose="beforeClose"></Report>

    <UserCard :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>

    <el-image-viewer v-if="showBigPic" :on-close="closeViewer" :url-list="picList"/>

  </div>
</template>

<script>
import CommentBox from "@/components/CommentBox";
import Report from "@/components/Report"
import CommentList from "@/components/CommentList";
import MomentBox from "@/components/Moment/MomentBox"
import {getListByDictTypeList} from "@/api/sysDictData"
import {getUserMomentList, getUserMomentTopicList, deleteBatchUserMoment} from "@/api/moment"
import {timeAgo} from "../utils/webUtils";
import Sticky from "@/components/Sticky";
import {addComment, deleteComment} from "../api/comment";
import {mapMutations} from "vuex";
import Collection from "../components/Collection";
import Praise from "../components/Praise";
import {recorderBehavior} from "../api";
import UserCard from "../components/UserCard";
import {addEmoticon} from "../api/emoticon";
import ElImageViewer from "element-ui/packages/image/src/image-viewer.vue";

export default {
  name: "moment",
  data() {
    return {
      momentInfo: {},
      defaultAvatar: process.env.defaultAvatar,
      // xss白名单配置
      options : {
        whiteList: {
          div: ['class'],
          a: ['href', 'title', 'target'],
          span: ['class'],
          h1: ['class'],
          h2: ['class'],
          h3: ['class'],
          h4: ['class'],
          h5: ['class'],
          h6: ['class'],
          pre: [],
          code: ['class'],
          p: ['class'],
          ol: ['class', 'start'],
          blockquote: ['class'],
          ul: ['class'],
          li: ['class'],
          img: ['class', 'src', 'alt', 'width', 'height', 'style', 'uid'],
          iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
          hr: [],
          video: ['class', 'src', 'controls'],
          source: ['src', 'type'],
          br: [],
        }
      },
      imageWidth: "60%",
      typeList: ['warning', 'danger', 'success', 'info','primary'],
      userTagDictList: [], // 用户标签字典
      momentTopicList: [], // 话题
      momentList: [], // 动态
      selectTopicUid: "",
      selectTopicContent: "最新",
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      orderBy: "", // 排序
      orderByDescColumn: "", // 降序字段
      dialogReportVisible: false, // 控制举报弹出
      reportInfo: {}, // 举报信息
      userLevelDictList: [],
      toInfo: {
        uid: "",
        commentUid: ""
      },
      commentInfo: {
        blogUid: "",
        source: "USER_MOMENT"
      },
      userInfo: {},
      momentUid: null, //动态uid
      userUid: "", // 用户uid
      usrCard: "",
      coorDinatex: "",
      coorDinatey: "",
      showUsrCard: false,
      currentId:1,
      currentBizUid: "", // 当前id
      showBigPic: false,
      picList: [],
    }
  },
  components: {
    //注册组件
    MomentBox,
    Sticky,
    CommentBox,
    CommentList,
    Collection,
    Praise,
    Report,
    UserCard,
    ElImageViewer,
  },
  mounted() {
    const myImgs = document.querySelectorAll(".rightCenter");
    console.log("获取选中的图片", myImgs)
    myImgs.forEach(function(img) {
      img.addEventListener("click", function() {
        // 在这里添加点击事件处理程序的代码
        console.log("点击图片")
      });
    });
  },
  created() {
    this.momentUid = this.$route.query.uid;
    this.userUid = this.$route.query.userUid
    this.getDictList()
    this.userMomentTopicList()
    this.userMomentList()

    setTimeout(()=>{
      recorderBehavior({"otherData": "动态", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)
  },
  watch: {
    'momentList.commentList':{
      deep:true,
      handler:function(newV,oldV){
        console.log('watch中：',newV)
      }
    },
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setMomentTopicList', 'setMomentList', 'setUserTag']),
    getTabIndex: function (index) {
      return "id" + index
    },
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showBigPic = true
        this.picList = [e.target.currentSrc]
      }
    },
    closeViewer() {
      this.showBigPic = false
    },
    //鼠标进入的事件。
    onEnterTd (item, index) {
      this.showUsrCard = true;
      this.usrCard = item;
      let tagElement = document.getElementById("id"+ index);
      let leftx = tagElement.getBoundingClientRect().left;
      let topx = tagElement.getBoundingClientRect().top;
      this.coorDinatex = leftx;
      this.coorDinatey = topx;
      clearTimeout(this.leaveTimeout)
    },
    praiseCallback: function (momentUid) {
      let momentList = this.momentList
      for (let a=0; a< momentList.length; a++) {
        let momentItem = momentList[a]
        if (momentItem.uid == momentUid) {
          let praiseUserList = momentItem.praiseUserList
          // 插入当前用户
          praiseUserList.push(this.$store.state.user.userInfo)
        }
      }
    },
    onContextImageMenu(event) {
      //首先需要判断点击的是否是图片
      // 事件委托是一种在父元素上监听子元素事件的技术
      let type = event.target.localName;
      if (type !== "img") {
        return false
      }
      // 右键操作的文本
      let fileUid = event.target.getAttribute("uid")
      let fileSrc = event.target.getAttribute("src")
      console.log("获取event", event)
      this.$contextmenu({
        items: [
          {
            label: "复制地址",
            icon: "el-icon-copy-document",
            divided: true,
            onClick: () => {
              // 创建输入框元素
              let oInput = document.createElement('input');
              // 将想要复制的值
              oInput.value = fileSrc;
              // 页面底部追加输入框
              document.body.appendChild(oInput);
              // 选中输入框
              oInput.select();
              // 执行浏览器复制命令
              document.execCommand('Copy');
              this.$message.success("复制成功")
            }
          },
          {
            label: "收藏表情",
            icon: "el-icon-picture-outline-round",
            onClick: () => {
              if (fileUid) {
                let params = {}
                params.fileUid = fileUid
                params.isSelection = 0
                addEmoticon(params).then(response => {
                  if (response.code == this.$ECode.SUCCESS) {
                    this.$message.success(response.message)
                  } else {
                    this.$message.error(response.message)
                  }
                })
              } else {
                this.$message.error("收藏失败，该图片无法收藏")
              }
            }
          },
        ],
        event, // 鼠标事件信息
        customClass: "custom-class", // 自定义菜单 class
        zIndex: 3, // 菜单样式 z-index
        minWidth: 100 // 主菜单最小宽度
      });
      return false;
    },
    cancelPraiseCallback: function (momentUid) {
      console.log("取消点赞回调", momentUid)
      let userUid = this.$store.state.user.userInfo.uid
      let momentList = this.momentList
      for (let a=0; a< momentList.length; a++) {
        let momentItem = momentList[a]
        if (momentItem.uid == momentUid) {

          let praiseUserList = momentItem.praiseUserList
          let deleteIndex = -1
          for (let b=0; b<praiseUserList.length; b++) {
            let user = praiseUserList[b]
            if (user.uid == userUid) {
              deleteIndex = b
              break
            }
          }
          // 需要删除的内容
          if (deleteIndex > -1) {
            Vue.delete(praiseUserList, deleteIndex)
          }
        }
      }
    },
    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    //鼠标离开的事件。
    onLeaveTd (nowClean) {
      let that = this
      if (nowClean) {
        that.showUsrCard = false;
      } else {
        this.leaveTimeout = setTimeout(() => {
          that.showUsrCard = false;
        }, 300)
      }
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_user_tag', 'sys_user_level']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelDictList = dictMap.sys_user_level.list
          this.setUserTag(dictMap.sys_user_tag.list)
        }
      });
    },
    cancelBox() {
      this.commentInfo.blogUid = ""
      this.currentBizUid = ""
    },
    deleteMoment(moment) {
      if(!this.validLogin()) {
        this.$notify.error({
          title: '错误',
          message: "登录后才能删除动态哦~",
          offset: 100
        });
        return
      }

      this.$confirm("此操作将把本动态删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = moment.uid
          let array = [params]
          deleteBatchUserMoment(array).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$notify({
                title: '成功',
                message: "删除成功",
                type: 'success',
                offset: 100
              });
              this.currentPage = 1
              this.momentList = []
              this.userMomentList()
            } else {
              this.$notify.error({
                title: '错误',
                message: "删除失败",
                offset: 100
              });
            }

          });

        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    replaceImg: function (text) {
      text = this.$commonUtil.replaceMinImg(text)
      if ( document.body.clientWidth > 1000) {
        text = this.$commonUtil.replaceMinIframe(text)
      }
      return text
    },
    selectTopic: function (topicUid, topicContent) {
      this.orderBy = ""
      this.momentUid = ""
      this.userUid = ""
      this.orderByDescColumn = ""
      this.isEnd = false;
      if(topicUid) {
        let topicUidList = [topicUid]
        this.setMomentTopicList(topicUidList)
      } else {
        this.setMomentTopicList([])
      }
      console.log("点击", topicContent)
      switch (topicContent) {
        case "最新": {
          this.orderBy = ""
          this.orderByDescColumn = "create_time"
        } break;
        case "关注": {
          this.orderBy = "userWatch"
          this.orderByDescColumn = ""
        } break;
        case "最热": {
          this.orderBy = ""
          this.orderByDescColumn = "click_count"
        } break;
      }

      this.selectTopicUid = topicUid
      this.selectTopicContent = topicContent
      this.pageSize = 10
      this.currentPage = 1
      this.momentList = []
      this.userMomentList()
    },
    // 发表评论
    submitBox(e) {
      let that = this
      console.log("发表评论", e )
      let params = {};
      params.source = e.source;
      params.userUid = e.userUid;
      params.content = e.content;
      params.blogUid = e.blogUid;
      addComment(params).then(response => {
        if (response.code === this.$ECode.SUCCESS){
          let commentData = response.data
          // 设置回复为空
          commentData.replyList = [];
          commentData.user = this.$store.state.user.userInfo
          let momentList = this.insertCommentList(commentData.blogUid, commentData)
          this.commentInfo.blogUid = ""
          this.currentBizUid = ""
          this.momentList = []
          this.$nextTick(() => {
            that.momentList = momentList
          });
          this.$message.success("发表成功！");
        }else{
          this.$message.error(response.message)
        }
      });
    },
    insertCommentList(uid, targetComment) {
      let momentList = this.momentList
      for (let moment of momentList) {
        if (moment.uid === uid) {
          let comments = moment.commentList;
          let commentList = []
          commentList.push(targetComment)
          if (comments) {
            for (let i = 0; i < comments.length; i++) {
              commentList.push(comments[i])
            }
          }
          moment.commentList = commentList
        }
      }
      return momentList;
    },
    deleteCommentList(uid) {
      let momentList = this.momentList
      for (let moment of momentList) {
        if (moment.uid === uid) {
          let commentList = moment.commentList;
          commentList.splice(commentList.indexOf(moment), 1);
        }
      }
      return momentList;
    },
    report: function (item) {
      let reportInfo = {}
      reportInfo.reportType = "moment" // 评论
      reportInfo.reportUserUid = item.userUid // 被举报人id
      reportInfo.reportContentUid = item.uid
      reportInfo.reportContent = item.content
      this.reportInfo = reportInfo
      this.dialogReportVisible = true
    },
    beforeClose: function () {
      this.dialogReportVisible = false
    },
    // 校验是否登录
    validLogin() {
      let userInfo = this.$store.state.user.userInfo
      if(userInfo.userName == undefined) {
        return false;
      } else {
        return true;
      }
    },
    replyTo: function (userMoment) {
      if(!this.validLogin()) {
        this.$notify.error({
          title: '错误',
          message: "登录后才能回复评论哦~",
          offset: 100
        });
        return
      }
      let userMomentUid = userMoment.uid;
      this.commentInfo.blogUid = userMomentUid
      this.currentBizUid = userMomentUid
      this.toInfo.blogUid = userMomentUid
    },
    getUserCenter: function (user) {
      console.log("跳转到用户中心", user)
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: user.uid}
      });
      window.open(routeData.href, '_blank');
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    loadContent: function () {
      this.currentPage = this.currentPage + 1
      this.userMomentList()
    },
    userMomentList: function () {
      let params = {}
      params.uid = this.momentUid
      params.userUid = this.userUid
      params.orderByDescColumn = this.orderByDescColumn
      params.orderBy = this.orderBy
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.topicUids = this.selectTopicUid
      this.loading = false;
      getUserMomentList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let momentList = response.data.records;
          let newMomentList = this.momentList.concat(momentList);
          this.setMomentList(newMomentList)
          this.momentList = newMomentList
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (momentList.length < this.pageSize) {
            this.isEnd = true;
          }
        }
        this.loading = false;
      });
    },
    userMomentTopicList: function () {
      let params = {}
      params.currentPage = 1;
      params.pageSize = 10;
      getUserMomentTopicList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.momentTopicList = response.data.records;
        }
      });
    },
  }
}
</script>

<style>
.chat-msg-emoji {
  display: inline-block;
  vertical-align: middle;
}
</style>

<style scoped>

.isEnd {
  margin-top: 20px;
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
}
.loadContent {
  border-radius: 25px;
  width: 120px;
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  margin: 0 auto;
  color: aliceblue;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.8);
}
.topicStyle {
  color: #fff0ff;
  background: rgb(0, 107, 255);
}
.link-container {
  position: relative;
  padding-top: 10px;
  padding-bottom: 10px;
}

.zone-link-part {
  width: 100%;
  display: flex;
  border: 1px solid #00A7EB;
  padding: 5px;
  border-radius: 4px;
  margin-top: 10px;
  justify-content: flex-start;
}
.zone-link-bg {
  padding: 20px;
  background: #0084ff;
  color: #fff;
}
.link-right-part {
  margin-left: 10px;
}
.link-right-part .title {
  line-height: 26px;
  font-size: 16px;
  color: #8a93a0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  padding-right: 10px;
}
.link-right-part .url {
  color: #0084ff;
  font-size: 14px;
  margin-top: 6px;
  opacity: .6;
  line-height: 24px;
}
.link-close-btn {
  position: absolute;
  right: 1px;
  font-size: 24px;
  color: #ebebeb;
  cursor: pointer;
  bottom: 1px;
}

.commentStyle {
  display: block;
  margin-top: 10px;
  margin-left: 10px;
  border-left: 1px dashed SlateGray;
}
.comment {
  /*display: none;*/
}
.commentList {
  padding-top: 1px;
  width: 100%;
  margin: 0 auto;
}
.commentList .p1 {
  float: left;
}
.commentList .left {
  display: inline-block;
  width: 5%;
  height: 100%;
}
.commentList .left img {
  margin: 0 auto;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.commentList .right {
  display: inline-block;
  width: 93%;
  margin-left: 5px;
}
.commentList .rightTop {
  height: 30px;
  margin-top: 2px;
}
.commentList .rightTop .userName {
  color: #303133;
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
}
.commentList .rightTop .timeAgo {
  color: #909399;
  margin-left: 5px;
  font-size: 12px;
}
.commentList .rightCenter {
  margin-left: 20px;
  min-height: 50px;
  margin-top: 15px;
}
.commentList .rightBottom {
  margin-left: 10px;
  height: 30px;
}
.commentList .rightBottom el-link {

}
.commentList .rightBottom .b1 {
  margin-top: -5px;
  margin-right: 10px;
}

.like-user-avatar {
  height: 20px;
  width: 20px;
  border-radius: 40px;
  float: left;
  margin-left: -5px;
  box-shadow: 5px 0 10px #c9c9c9;
  border: 2px solid white;
  cursor: pointer;
  transition: all 0.6s;
}

.like-user-avatar:hover{
  transform: scale(1.5); /*transform:变形属性，scale：缩放1.1倍 */
}

@media only screen and (min-width: 300px) and (max-width: 767px) {
  .commentList .left {
    display: inline-block;
    width: 35px;
    height: 100%;
  }
  .vip-text {
    bottom: -5px;
    padding: 0 7px 2px;
    border-radius: 50%;
    right: -20px;
    color: #FFD700;
    position: absolute;
  }
  .commentList .right {
    display: inline-block;
    width: calc(100% - 40px);
    margin-left: 5px;
  }
}
</style>
