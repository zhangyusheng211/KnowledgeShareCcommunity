<template>
  <div>
    <div v-for="(item, index) in commentList" :key="item.uid" >
      <div class="commentList" style="position: relative;">

        <span  @mouseover="onEnterTd(item.user, item.uid)" @mouseleave="onLeaveTd(false)" class="left p1 " v-if="item.user"  :class="item.user.userTag > 0 ?'vip-avatar':''">
          <img  :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user" style="cursor:pointer;" @click="getUserCenter(item.user)" :src="item.user.photoUrl ? item.user.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
          <img v-else :src="defaultAvatar" />
          <span class="vip-text pointer" v-if="item.user.userTag > 0" :style="{right: marginLeft  + 'px'}" @click.natice="getUserCenter(item.user)">v</span>
        </span>

        <span class="right p1" :id="getTabIndex(item.uid)">
          <div class="rightTop" v-if="item.user">
            <el-link class="userName" :underline="false" v-if="item.user.nickName" @click="getUserCenter(item.user)">{{item.user.nickName}}</el-link>
            <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.user.userTag == userTag.dictValue && item.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>
            <span class="timeAgo" >
              <span  class="lv" v-if="item.user.userLevel" :class="'lv'+ item.user.userLevel"> Lv{{item.user.userLevel}} </span>
            </span>
            <el-tag v-if="authorUid == item.user.uid" size="mini" effect="plain" style="margin-left:5px;" type="success">作者</el-tag>



            <span class="iconfont" v-if="item.user.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
            <span class="iconfont" v-if="item.user.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>

            <span class="timeAgo" style="color: #8a919f;" v-if="item.user.userIpPossession">IP属地:{{item.user.userIpPossession}}</span>

            <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
            <span class="timeAgo" v-else>刚刚</span>

            <el-tag v-if="item.isTop == 1" size="mini" effect="plain" style="margin-left:5px;" type="danger">置顶</el-tag>
          </div>

          <div class="rightCenter ck-content" v-highlight
               @click="imageChange"
               v-html="$xss(convertContent(item), options)"></div>

          <div class="rightBottom">
            <el-link class="b1" :underline="false" @click="replyTo(item)" v-if="level < maxReplyLevel">回复</el-link>
            <el-link class="b1" :underline="false" @click="report(item)" v-if="componentShowMap.showUserReport">举报</el-link>
            <el-link class="b1" v-if="$store.state.user.isLogin && $store.state.user.userInfo.uid == item.userUid && componentShowMap.showDeleteComment" :underline="false" @click="delComment(item)">删除</el-link>

            <div style="display: inline-block" class="b1" :underline="false">
              <Praise :bizUid="item.uid" :resourceType="'5'"
                      :isPraiseValue="item.praiseInfo?item.praiseInfo.isPraise:false"
                      :isTreadValue="item.praiseInfo?item.praiseInfo.isPraise:false"
                      :praiseCountValue="item.praiseInfo?item.praiseInfo.praiseCount:0"
                      :treadCountValue="item.praiseInfo?item.praiseInfo.treadCount:0"
              ></Praise>
            </div>
          </div>

          <div class="rightCommentList">
            <CommentBox v-if="toInfo.commentUid === item.uid" class="comment" :userInfo="userInfo" :toInfo="toInfo" :id="item.uid" :commentInfo="commentInfo"
                        @submit-box="submitBox" @cancel-box="cancelBox"></CommentBox>

            <span class="childrenCommentList">
              <CommentList class="commentStyle" :level="level + 1" :marginLeft="marginLeft-6" :authorUid="authorUid" :maxReplyLevel="maxReplyLevel" :id="'commentStyle:' + item.uid" :comments="item.replyList" :commentInfo="commentInfo"></CommentList>
            </span>
          </div>
        </span>
      </div>
    </div>

    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo" @beforeClose="beforeClose"></Report>
    <UserCard v-if="showUsrCard" :usrCard="this.usrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>
    <el-image-viewer v-if="showBigPic" :on-close="closeViewer" :url-list="picList"/>

  </div>

</template>

<script>

  import {mapMutations} from 'vuex';
  import CommentBox from "./CommentBox";
  import Praise from "../Praise";
  import Report from "@/components/Report"
  import {timeAgo} from "../../utils/webUtils"
  import {addComment, deleteComment, getCommentList, reportComment} from "../../api/comment";
  import {getListByDictTypeList} from "@/api/sysDictData"
  import UserCard from "../UserCard";
  import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'

  export default {
    name: "CommentList",
    props: {
      // 父级组件传过来的评论列表
      comments: {
        type: Array,
        default: () => []
      },
      userInfos: {
        type: Object
      },
      // 评论主体信息
      commentInfo: {
        type: Object
      },
      // 递归组件的嵌套层级，递归了1层、2层、3层、4层、5层，在最后一层可以禁止回复，拒绝无休止的评论
      level: {
        type: Number,
        default: 1,
      },
      marginLeft: {
        type: Number,
        default: 0,
      },
      // 控制最大评论层级
      maxReplyLevel: {
        type: Number,
        default: 5,
      },
      // 作者UID
      authorUid: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        // xss白名单配置
        options : {
          whiteList: {
            div: ['class'],
            br: ['class'],
            a: ['href', 'title', 'target', 'style', 'class'],
            span: ['class', 'style'],
            h1: ['class'],
            h2: ['class'],
            h3: ['class'],
            h4: ['class'],
            ol: ['start'],
            pre: [],
            code: ['class'],
            p: ['class'],
            blockquote: ['class'],
            ul: ['class'],
            li: ['class'],
            img: ['class', 'src', 'alt', 'style'],
            // iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
            hr: [],
            video: ['class', 'src', 'controls'],
            source: ['src', 'type']
          },
        },
        imageWidth: "60%",
        reportInfo: {}, // 举报信息
        taggleStatue: true,
        submitting: false,
        dialogReportVisible: false,
        value: '',
        toInfo: {
          uid: "",
          commentUid: ""
        },
        VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
        userInfo: {},
        userTagDictList: [], // 用户标签字典
        defaultAvatar: process.env.defaultAvatar,
        usrCard: "",
        coorDinatex: "",
        coorDinatey: "",
        showUsrCard: false,
        currentId:1,
        showBigPic: false,
        picList: [],
        commentList: this.comments,
        componentShowMap: {},
      };
    },
    watch:{
      comments(){
        this.commentList = this.comments;
      },
      '$store.state.app.webConfigData': function (event, oldFlag) {
        this.getComponentShowMap()
      },
    },
    created() {
      this.getDictList()
    },
    components: {
      CommentBox,
      Report,
      Praise,
      UserCard,
      ElImageViewer,
    },
    mounted() {
      this.getComponentShowMap()
    },
    compute: {},
    methods: {
      ...mapMutations(['setCommentList', 'setUserTag', 'setDomainEventMessage']),
      getTabIndex: function (index) {
        return "id" + index
      },
      getComponentShowMap() {
        let webConfigData = this.$store.state.app.webConfigData
        this.componentShowMap = webConfigData.componentShowMap
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
        console.log("位置", leftx, topx)
        clearTimeout(this.leaveTimeout)
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
        if(this.$store.state.app.userTagDictList.length > 0) {
          this.userTagDictList = this.$store.state.app.userTagDictList
          return;
        }
        let dictTypeList =  ['sys_user_tag']
        getListByDictTypeList(dictTypeList).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            let dictMap = response.data;
            this.userTagDictList = dictMap.sys_user_tag.list
            this.setUserTag(dictMap.sys_user_tag.list)
          }
        });
      },
      imageChange: function (e) {
        //首先需要判断点击的是否是图片
        let type = e.target.localName;
        if (type == "img") {
          this.showBigPic = true
          this.picList = [e.target.currentSrc]
        }
      },
      convertContent: function (comment) {
        let toUser = comment.toUser
        let content = comment.content
        if(toUser && comment.toUid != comment.firstCommentUid) {
          let userCenterUrl = this.VUE_MOGU_WEB +"/userCenter?userUid=" + toUser.uid
          content = "<span><a class='atUserLine' href='"+ userCenterUrl +"' target='_blank'>@" + toUser.nickName + "</a></span>"+ content;
          console.log("评论内容", content)
        }
        content = this.$commonUtil.replaceMinImg(content)
        if ( document.body.clientWidth > 1000) {
          content = this.$commonUtil.replaceMinIframe(content)
        }
        return content
      },
      closeViewer() {
        this.showBigPic = false
      },
      beforeClose: function () {
        this.dialogReportVisible = false
      },
      replyTo: function (item) {
        if(!this.validLogin()) {
          this.$notify.error({
            title: '错误',
            message: "登录后才能回复评论哦~",
            offset: 100
          });
          return
        }
        let userUid = item.userUid;
        this.toInfo.commentUid = item.uid
        this.toInfo.uid = userUid
      },
      submitBox(e) {
        let params = {};
        params.userUid = e.userUid;
        params.content = e.content;
        params.blogUid = e.blogUid;
        params.toUid = e.toCommentUid;
        params.toUserUid = e.toUserUid;
        params.source = e.source
        addComment(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let commentData = response.data
              this.toInfo.commentUid = ""
              let comments = this.$store.state.app.commentList;
              commentData.user = this.userInfo;
              // 设置回复为空
              commentData.replyList = [];
              commentData.user = this.$store.state.user.userInfo
              this.updateCommentList(comments, commentData.toUid, commentData)
              this.$store.commit("setCommentList", comments);
              this.$message.success("评论成功！")
              // 发送评论的领域事件
              let event = {
                "type": "action",
                "action": this.$EAction.Comment,
                "resourceUid": e.blogUid,
                "resourceType": "BLOG",
                "time": new Date(),
              }
              this.setDomainEventMessage(event)
            } else {
              this.$message.error(response.message)
            }
          }
        )
        ;
      },
      cancelBox(toCommentUid) {
        this.toInfo.commentUid = ""
      },
      report: function (item) {
        let reportInfo = {}
        reportInfo.reportType = "comment" // 评论
        reportInfo.reportUserUid = item.userUid // 被举报人id
        reportInfo.reportContentUid = item.uid
        reportInfo.reportContent = item.content
        this.reportInfo = reportInfo
        this.dialogReportVisible = true
      },
      delComment: function (item) {
        if(!this.validLogin()) {
          this.$notify.error({
            title: '错误',
            message: "登录后才能删除评论哦~",
            offset: 100
          });
          return
        }

        this.$confirm("此操作将把本评论和子评论删除, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            let params = {};
            params.uid = item.uid;
            params.userUid = this.$store.state.user.userInfo.uid
            deleteComment(params).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$notify({
                  title: '成功',
                  message: "删除成功",
                  type: 'success',
                  offset: 100
                });

              } else {
                this.$notify.error({
                  title: '错误',
                  message: "删除失败",
                  offset: 100
                });
              }
              let comments = this.$store.state.app.commentList;
              this.deleteCommentList(comments, params.uid, null)
              this.$store.commit("setCommentList", comments);
              this.$emit("deleteComment", "")
            });

          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消删除"
            });
          });


      },
      getUserCenter: function (user) {
        console.log("跳转到用户中心", user)
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: user.uid}
        });
        window.open(routeData.href, '_blank');
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
      /**
       * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
       * @param dateTimeStamp
       * @returns {string}
       */
      timeAgo(dateTimeStamp) {
        return timeAgo(dateTimeStamp)
      },
      // 追加内容
      updateCommentList(commentList, uid, targetComment) {
        if (commentList == undefined || commentList.length <= 0) {
          return;
        }
        for (let item of commentList) {
          let replyList = item.replyList;
          if (replyList == null) {
            replyList = []
          }
          if (item.uid == uid) {
            replyList.unshift(targetComment);
            item.replyList = replyList
            return;
          }
          for (let reply of replyList) {
            if (reply.uid == uid) {
              replyList.unshift(targetComment);
              item.replyList = replyList
              return;
            }
          }
        }
      },
      deleteCommentList(commentList, uid, parentList) {
        if (commentList == undefined || commentList.length <= 0) {
          return;
        }
        for (let item of commentList) {
          if (item.uid === uid) {
            commentList.splice(commentList.indexOf(item), 1);
          } else {
            this.deleteCommentList(item.replyList, uid, item);
          }
        }
      },
    },
  };
</script>

<style scoped>

  .childrenCommentList {
    background: #f5f6f7;
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
    margin-left: 10px;
  }

  @media only screen and (min-width: 300px) and (max-width: 767px) {
    .commentList .left {
      display: inline-block;
      width: 35px;
      height: 100%;
    }

    .commentList .right {
      display: inline-block;
      width: calc(100% - 40px);
      margin-left: 5px;
    }
  }
</style>
