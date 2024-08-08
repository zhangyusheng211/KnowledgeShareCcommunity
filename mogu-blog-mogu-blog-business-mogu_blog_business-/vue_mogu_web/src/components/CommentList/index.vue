<template>
  <div>
    <div v-for="(item, index) in commentList" :key="item.uid" style="margin-top: 10px;">
      <div class="commentList">
        <span class="left p1" @mouseover="onEnterTd(item.user, item.uid)" @mouseleave="onLeaveTd(false)">
          <img :id="getTabIndex(item.uid)" v-if="item.user" style="cursor:pointer;" @click="getUserCenter(item.user)" :src="item.user.photoUrl ? item.user.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
          <img v-else :src="defaultAvatar" />
        </span>

        <span class="right p1">
          <div class="rightTop" v-if="item.user">
            <el-link class="userName" :underline="false" @click="getUserCenter(item.user)">{{item.user.nickName}}</el-link>
            <el-tag size="mini" :class="'lv'+ item.user.userLevel" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.user.userTag == userTag.dictValue && item.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

            <span class="timeAgo" >
              <span  class="lv" v-if="item.user.userLevel" :class="'lv'+ item.user.userLevel"> Lv{{item.user.userLevel}} </span>
            </span>

            <span class="iconfont" v-if="item.user.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
            <span class="iconfont" v-if="item.user.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>

            <span class="timeAgo" style="color: #8a919f;" v-if="item.user.userIpPossession">IP属地:{{item.user.userIpPossession}}</span>

            <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
            <span class="timeAgo" v-else>刚刚</span>
          </div>

          <div class="rightCenter ck-content imageSelect"
               v-highlight
               @click="imageChange"
               v-html="$xss(convertConent(item), options)"></div>

          <div class="rightBottom">
            <el-link class="b1" style="margin-top: -4px;" :underline="false" @click="replyTo(item)">回复</el-link>
            <el-link class="b1" style="margin-top: -4px;" :underline="false" @click="report(item)">举报</el-link>
            <el-link class="b1" style="margin-top: -4px;" v-if="$store.state.user.isLogin && $store.state.user.userInfo.uid == item.userUid" :underline="false" @click="delComment(item)">删除</el-link>

            <div style="display: inline-block; "  class="b1" :underline="false">
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

          </div>
        </span>
      </div>
    </div>

    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo" @beforeClose="beforeClose"></Report>
    <UserCard :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="coorDinatex" :coorDinatey="coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>
    <el-image-viewer v-if="showBigPic" :on-close="closeViewer" :url-list="picList"/>


  </div>
</template>

<script>
  import {mapMutations} from 'vuex';
  import CommentBox from "../CommentBox";
  import Praise from "../Praise";
  import Report from "@/components/Report"
  import {timeAgo} from "../../utils/webUtils"
  import {addComment, deleteComment, getCommentList, reportComment} from "../../api/comment";
  import {getListByDictTypeList} from "@/api/sysDictData"
  import UserCard from "../UserCard";
  import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'

  export default {
    name: "CommentList",
    props: ['comments', 'userInfos', 'commentInfo', 'businessUid'],
    data() {
      return {
        // xss白名单配置
        options : {
          whiteList: {
            div: ['class'],
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
        commentList: this.comments,
        imageWidth: "60%",
        dialogImageUrl: "",
        taggleStatue: true,
        submitting: false,
        value: '',
        toInfo: {
          uid: "",
          commentUid: ""
        },
        userInfo: {},
        userTagDictList: [], // 用户标签字典
        defaultAvatar: process.env.defaultAvatar,
        VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
        dialogReportVisible: false,
        reportInfo: {}, // 举报信息
        usrCard: "",
        coorDinatex: "",
        coorDinatey: "",
        showUsrCard: false,
        currentId:1,
        showBigPic: false,
        picList: [],
      };
    },
    created() {
      this.getDictList()
      // 获取信息
      if (this.businessUid) {
        this.commentInfo.blogUid = this.businessUid
      }
    },
    components: {
      CommentBox,
      Praise,
      Report,
      UserCard,
      ElImageViewer,
    },
    mounted() {

    },
    compute: {},
    methods: {
      ...mapMutations(['setCommentList', 'setUserTag']),
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
        console.log("位置", this.coorDinatex, this.coorDinatey)
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
            // this.setUserTag(dictMap.sys_user_tag.list)
          }
        });
      },
      beforeClose: function () {
        this.dialogReportVisible = false
      },
      replyTo: function (item) {
        console.log("开始回复", )
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
        this.commentInfo.blogUid = item.blogUid
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
              let comments = this.commentList;
              commentData.user = this.userInfo;
              // 设置回复为空
              commentData.replyList = [];
              commentData.user = this.$store.state.user.userInfo
              let newCommentList = this.updateCommentList(comments, commentData.toUid, commentData)
              this.commentList = newCommentList
              this.$store.commit("setCommentList", comments);
              this.toInfo.commentUid = ""
              this.$message.success("发表成功！")
            } else {
              this.$message.error(response.message)
            }
          }
        )
      },
      convertConent: function (comment) {
        let toUser = comment.toUser
        let content = comment.content
        if(toUser) {
          let userCenterUrl = this.VUE_MOGU_WEB +"/userCenter?userUid=" + toUser.uid
          content = "<span><a class='atUserLine' href='"+ userCenterUrl +"' target='_blank'>@" + toUser.nickName + "</a></span>"+ content;
        }
        content = this.$commonUtil.replaceMinImg(content)
        if ( document.body.clientWidth > 1000) {
          content = this.$commonUtil.replaceMinIframe(content)
        }
        return content
      },
      getCommentList: function () {
        let params = {};
        params.currentPage = 0;
        params.pageSize = 10;
        getCommentList(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.comments = response.data;
          }
        });
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
                location.reload()
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
      updateCommentList(commentList, uid, targetComment) {
        let newCommentList = []
        for (let item of commentList) {
          if (item.uid === uid) {
            newCommentList.push(targetComment)
            if (commentList) {
              for (let i = 0; i < commentList.length; i++) {
                newCommentList.push(commentList[i])
              }
            }
          }
        }
        return newCommentList
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
  .at {
    line-height: 40px;
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
    font-size: 14px;
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
      width: 30px;
      height: 100%;
    }
    .commentList .right {
      display: inline-block;
      width: calc(100% - 35px);
      margin-left: 5px;
    }
  }
</style>
