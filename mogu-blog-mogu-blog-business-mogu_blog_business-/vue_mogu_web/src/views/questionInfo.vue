<template>
  <article>
    <el-dialog :visible.sync="dialogPictureVisible" fullscreen>
      <img :src="dialogImageUrl" alt="dialogImageUrl" style="margin: 0 auto;" />
    </el-dialog>

    <div class="infosbox">
      <div class="newsview">
        <h3 class="news_title" v-if="questionData.title">{{questionData.title}}</h3>
        <div class="bloginfo" >
          <ul>
            <li style="padding-right: 6px; text-align: center;">
              <span :class="questionData.user.userTag > 0 ?'vip-avatar':''">
                <el-avatar :class="questionData.user.userTag > 0 ?'vip-color':''" fit="fill" size="medium" :src="questionData.user.photoUrl"></el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="questionData.user.userTag > 0">v</span>
              </span>

            </li>
            <li class="author" style="margin-top: 9px;margin-left: 3px;">
              <a href="javascript:void(0);" v-if="questionData.user" @click="goToAuthor(questionData.user.nickName)">{{questionData.user.nickName}}</a>
            </li>
            <li class="view" style="margin-top: 8px">
              <span class="iconfont">&#xe8c7;</span>
              {{questionData.clickCount}}
            </li>
            <li class="like" style="margin-top: 8px">
              <span class="iconfont">&#xe663;</span>
              {{questionData.collectCount}}
            </li>
            <li class="createTime" style="margin-top: 8px">
              <span class="iconfont">&#xe606;</span>
              {{questionData.createTime}}
            </li>

            <li class="createTime" style=" margin-top: 9px; cursor: pointer;" @click="report(questionData)">
              <span>举报</span>
            </li>

            <li class="createTime" style="margin-top: 7px;">
              <Collection :bizUid="questionData.uid" :collectType="'2'"></Collection>
            </li>

          </ul>
        </div>
        <div class="tags">
          <a
            v-for="item in questionData.questionTagList"
            :key="item.uid"
            href="javascript:void(0);"
            @click="goToList(item.uid)"
            target="_blank"
          >{{item.name}}</a>
        </div>

        <div
          class="news_con ck-content"
          v-html="questionData.content"
          v-highlight
          v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }"
          @click="imageChange"
          style="min-height: 210px"
        >{{questionData.content}}</div>
      </div>

      <!--社会化分享-->
      <share :config="config" style="text-align: center"></share>

      <div class="otherlink" v-if="sameBlogData.length > 0">
        <h2>相关问答</h2>
        <ul>
          <li v-for="item in sameBlogData" :key="item.uid">
            <a
              href="javascript:void(0);"
              @click="goToInfo(item.uid)"
              :title="item.title"
            >{{subText(item.title, 18)}}</a>
          </li>
        </ul>
      </div>

      <div class="news_pl" :style="opemCommentCss">
        <h2 v-if="openComment == '1'" class="title">问答回复</h2>
<!--        <ul v-if="openComment == '1'">-->
<!--          <div class="message_infos" style="margin-top: 5px">-->
<!--            <QuestionCommentList :comments="comments" :commentInfo="commentInfo"></QuestionCommentList>-->
<!--            <div class="noComment" v-if="comments.length ==0">还没有回复，快来抢沙发吧！</div>-->
<!--          </div>-->
<!--        </ul>-->

        <!--通用评论模块-->
        <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel" :targetId="commentInfo.blogUid"
                 :author-uid="questionData.userUid"
                 :tableName="commentInfo.source"></Comment>



      </div>

<!--      <div class="news_pl" :style="opemCommentCss">-->
<!--        <ul v-if="openComment == '1'">-->
<!--          <QuestionCommentBox-->
<!--            :userInfo="userInfo"-->
<!--            :commentInfo="commentInfo"-->
<!--            @submit-box="submitBox"-->
<!--            :showCancel="showCancel"-->
<!--          ></QuestionCommentBox>-->
<!--        </ul>-->
<!--      </div>-->

    </div>
    <div class="sidebar2" v-if="showSidebar">
      <side-catalog
        :class="vueCategory"
        v-bind="catalogProps"
      >
      </side-catalog>
    </div>
    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo" @beforeClose="beforeClose"></Report>

  </article>
</template>

<script>
    import {getWebConfig} from "../api/index";
    import { getQuestion } from "../api/question";
    import QuestionCommentList from "../components/QuestionCommentList";
    import QuestionCommentBox from "../components/QuestionCommentBox";
    import Collection from "../components/Collection";
    // vuex中有mapState方法，相当于我们能够使用它的getset方法
    import { mapMutations } from "vuex";
    import ThirdRecommend from "../components/ThirdRecommend";
    import FourthRecommend from "../components/FourthRecommend";
    import TagCloud from "../components/TagCloud";
    import HotBlog from "../components/HotBlog";
    import FollowUs from "../components/FollowUs";
    import PayCode from "../components/PayCode";
    import Link from "../components/Link";
    import { addComment, getCommentList } from "../api/comment";
    import { Loading } from "element-ui";
    import Sticky from "@/components/Sticky";
    import SideCatalog from '@/components/VueSideCatalog'
    import Report from "@/components/Report"
    import Comment from "@/components/Comment"
    export default {
        name: "info",
        data() {
            return {
                // 目录列表数
                catalogSum: 0,
                showStickyTop: false,
                showSideCatalog: true,
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
                  source: "QUESTION_INFO",
                  maxReplyLevel: 4, // 最大回复深度
                  blogUid: this.$route.query.blogUid
                },
                currentPage: 1,
                pageSize: 10,
                total: 0, //总数量
                toInfo: {},
                userInfo: {},
                questionOid: 0, // 传递过来的问答oid
                questionData: {
                  user: {}
                },
                dialogReportVisible: false, // 控制举报弹出
                reportInfo: {}, // 举报信息
                webConfig: {},
                sameBlogData: [], //相关文章
                dialogPictureVisible: false,
                dialogImageUrl: "",
                openComment: "0", // 开启评论
                openAdmiration: "0", // 开启赞赏
                config:{
                  sites: ['qq', 'qzone', 'weibo', 'wechat', 'douban'],
                }
            };
        },
        computed: {
          vueCategory: function () {
            if(!this.showStickyTop && this.showSideCatalog) {
              return 'catalog'
            }
            if(!this.showStickyTop && !this.showSideCatalog) {
              return 'catalog'
            }
            if(this.showStickyTop && this.showSideCatalog) {
              return 'catalog3'
            }
            if(this.showStickyTop && !this.showSideCatalog) {
              return 'catalog2'
            }
          },
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
          FourthRecommend,
          ThirdRecommend,
          TagCloud,
          HotBlog,
          FollowUs,
          PayCode,
          QuestionCommentList,
          QuestionCommentBox,
          SideCatalog,
          Link,
          Sticky,
          Report,
          Collection,
          Comment,
        },
        watch: {
          $route(to, from) {
            location.reload()
          },
          // 判断配置
          '$store.state.app.webConfigData': function (newFlag, oldFlag) {
            this.webConfig = this.$store.state.app.webConfigData
            this.setWebConfig()
          },
        },
        mounted () {
          let that = this;
          let params = {};
          params.oid = this.questionOid
          params.isLazy = "1"
          getQuestion(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let questionData = response.data
              this.$commonUtil.setMetaInfo(questionData.title, questionData.summary, questionData.summary)
              let content = this.$commonUtil.replaceImg(questionData.content);
              questionData.content = content
              this.questionData = questionData
              this.questionOid = response.data.oid
              this.commentInfo.blogUid = response.data.uid;
              this.commentInfo.blogOid = response.data.oid;

              that.loadingInstance.close();
              this.getCommentDataList()
            }
          });

          let after = 0;
          let offset = 110;
          $(window).scroll(function () {
            let docHeight = $(document).height(); // 获取整个页面的高度(不只是窗口,还包括为显示的页面)
            let winHeight = $(window).height(); // 获取当前窗体的高度(显示的高度)
            let winScrollHeight = $(window).scrollTop(); // 获取滚动条滚动的距离(移动距离)

            if (winScrollHeight < offset) {
              that.showStickyTop = false
            } else {
              that.showStickyTop = true
            }

            if (winScrollHeight > after) {
              that.showSideCatalog = true
            } else {
              that.showSideCatalog = false
            }
            after = winScrollHeight;
            //还有30像素的时候,就查询
            if(docHeight == winHeight + winScrollHeight){
              if(that.comments.length >= that.total) {
                console.log('已经到底了')
                return;
              }
              let params = {};
              params.source = that.commentInfo.source;
              params.blogUid = that.commentInfo.questionUid;
              params.currentPage = that.currentPage + 1
              params.pageSize = that.pageSize;
              getCommentList(params).then(response => {
                if (response.code == that.$ECode.SUCCESS) {
                  that.comments = that.comments.concat(response.data.records);
                  that.setCommentList(that.comments);
                  that.currentPage = response.data.current;
                  that.pageSize = response.data.size;
                  that.total = response.data.total;
                }
              });
            }
          })

          // 屏幕自适应
          window.onresize = () => {
            return (() => {
              // 屏幕大于950px的时候，显示侧边栏
              that.showSidebar = document.body.clientWidth > 950
            })()
          }
        },
        created() {
            this.loadingInstance = Loading.service({
                fullscreen: true,
                text: "正在努力加载中~"
            });
            this.questionOid = this.$route.params.questionOid;
            // 屏幕大于950px的时候，显示侧边栏
            this.showSidebar = document.body.clientWidth > 950
          this.setWebConfig()
        },
        methods: {
            //拿到vuex中的写的两个方法
            ...mapMutations(["setCommentList", "setWebConfigData"]),
            handleCurrentChange: function(val) {
                this.currentPage = val;
                this.getCommentDataList();
            },
          report: function (item) {
            if (item.questionSource == "0") {
              this.$message.error("无法举报该文章");
              return;
            }
            let reportInfo = {}
            reportInfo.reportType = "question" // 评论
            reportInfo.reportUserUid = item.userUid // 被举报人id
            reportInfo.reportContentUid = item.uid
            reportInfo.reportContent = item.title
            this.reportInfo = reportInfo
            this.dialogReportVisible = true
          },
          beforeClose: function () {
            this.dialogReportVisible = false
          },
          // 设置是否开启评论和赞赏
          setWebConfig() {
            let webConfigData = this.$store.state.app.webConfigData
            if(webConfigData.createTime) {
              this.openAdmiration = webConfigData.openAdmiration
              this.openComment = webConfigData.openComment
            }
          },
            submitBox(e) {
                let params = {};
                params.blogUid = e.blogUid;
                params.source = e.source;
                params.userUid = e.userUid;
                params.content = e.content;
                params.blogUid = e.blogUid;
                addComment(params).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$notify({
                            title: "成功",
                            message: "发表成功~",
                            type: "success",
                            offset: 100
                        });
                    } else {
                        this.$notify.error({
                            title: "错误",
                            message: response.data,
                            offset: 100
                        });
                    }
                    this.getCommentDataList();
                });
            },
            getCommentDataList: function() {
                console.log("开始获取评论列表")
                let params = {};
                params.source = this.commentInfo.source;
                params.blogUid = this.commentInfo.blogUid;
                params.currentPage = this.currentPage;
                params.pageSize = this.pageSize;
                getCommentList(params).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.comments = response.data.records;
                        this.setCommentList(this.comments);
                        this.currentPage = response.data.current;
                        this.pageSize = response.data.size;
                        this.total = response.data.total
                    }
                });
            },
            //跳转到文章详情
            goToInfo(uid) {
                let routeData = this.$router.resolve({
                    path: "/info",
                    query: { blogUid: uid }
                });
                window.open(routeData.href, "_blank");
            },
            //跳转到搜索详情页
            goToList(uid) {
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
                    // window.open(e.target.currentSrc);
                    this.dialogPictureVisible = true;
                    this.dialogImageUrl = e.target.currentSrc;
                }
            },
            //切割字符串
            subText: function(str, index) {
                if (str.length < index) {
                    return str;
                }
                return str.substring(0, index) + "...";
            }
        }
    };
</script>

<style scoped>
  .news_pl {
    border-top: 10px solid #f6f6f6;
    min-height: 10px;
  }
</style>

<style>

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
    margin-left: 20px;
    /*max-height: 700px*/
  }
  .catalog2 {
    position: fixed;
    margin-left: 20px;
    top: 70px;
  }
  .catalog3 {
    position: fixed;
    margin-left: 20px;
    top: 20px;
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
</style>
