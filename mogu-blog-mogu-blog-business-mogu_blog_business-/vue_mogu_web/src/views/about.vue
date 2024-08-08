<template>
  <div>
    <!-- 图片全屏显示 -->
    <el-dialog :visible.sync="dialogPictureVisible" fullscreen>
      <img :src="dialogImageUrl" alt="dialogImageUrl" style="margin: 0 auto;" />
    </el-dialog>

    <div class="pagebg ab"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">关于我</a>
      </h1>
      <div class="news_infos" style="min-height: 805px;">
        <div
          class="news_con fixck newsview ck-content"
          v-html="info.personResume"
          v-lazy-container="{ selector: 'img', error: '../../static/images/loading.gif', loading: '../../static/images/loading.gif' }"
          @click="imageChange"
          v-highlight
        >{{info.personResume}}</div>

        <el-divider></el-divider>

        <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel"  :tableName="commentInfo.source"></Comment>

      </div>
      <div class="sidebar">
        <Sticky :sticky-top="20" style="min-height: 1000px">
          <div class="about">
            <p class="avatar" v-if="info.photoList">
              <img :src="info.photoList[0]" alt />
            </p>
            <p class="abname">{{info.nickName}}</p>
            <p class="abposition">{{info.occupation}}</p>
            <p class="abtext">{{info.summary}}</p>
          </div>
          <follow-us></follow-us>
        </Sticky>
      </div>
    </div>
  </div>
</template>

<script>
    import Comment from "../components/Comment";
    import FollowUs from "../components/FollowUs";
    import { getMe } from "../api/about";
    import { mapMutations } from "vuex";
    import Sticky from "@/components/Sticky";
    import {getWebConfig, recorderBehavior} from "../api";

    export default {
        name: "about",
        data() {
            return {
                dialogPictureVisible: false,
                dialogImageUrl: "",
                showCancel: false,
                submitting: false,
                comments: [],
                personResume: "", // 个人简介
                commentInfo: {
                    // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
                    maxReplyLevel: 3, // 最大回复深度
                    source: "ABOUT"
                },
                currentPage: 1,
                pageSize: 10,
                total: 0, //总数量
                toInfo: {},
                userInfo: {},
                info: {},
                sid: "test",
                isRouterAlive: false,
                openComment: "0", // 开启评论
            };
        },
        components: {
            //注册组件
            FollowUs,
            Sticky,
            Comment
        },
        mounted () {

        },
        created() {
            getMe().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.info = response.data;
                }
            });
            this.setCommentAndAdmiration()

          // 5S后埋点信息上报
          setTimeout(()=>{
            recorderBehavior({"otherData": "关于我", "behavior": "VISIT_PAGE"}).then(response => {})
          }, 5000)

        },
        methods: {
            //拿到vuex中的写的两个方法
            ...mapMutations(["setCommentList", "setWebConfigData"]),
            // 设置是否开启评论和赞赏
            setCommentAndAdmiration() {
              let webConfigData = this.$store.state.app.webConfigData
              if(webConfigData.createTime) {
                this.openComment = webConfigData.openComment
              } else {
                getWebConfig().then(response => {
                  if (response.code == this.$ECode.SUCCESS) {
                    webConfigData = response.data;
                    // 存储在Vuex中
                    this.setWebConfigData(response.data)
                    this.openComment = webConfigData.openComment
                  }
                });
              }
            },
            imageChange: function(e) {
                //首先需要判断点击的是否是图片
                var type = e.target.localName;
                if (type == "img") {
                    // window.open(e.target.currentSrc);
                    this.dialogPictureVisible = true;
                    this.dialogImageUrl = e.target.currentSrc;
                }
            },
        }
    };
</script>

<style scoped>
  .emoji-panel-wrap {
    width: 470px;
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
  .news_infos .newsview img {
    max-width: 650px;
    height: auto;
  }

  .fixck p {
    margin: 12px 0 !important;
  }

  .fixck a {
    text-decoration: underline !important;
    color: #00e !important;
  }

  .fixck ul li {
    list-style: disc;
  }

  .fixck ol li {
    list-style: decimal;
  }

  .fixck ul,
  .fixck ol {
    padding-left: 40px !important;
    padding-right: 40px !important;
  }

  .fixck li {
    display: list-item !important;
  }

  .fixck h1 {
    font-weight: bold !important;
    font-size: 32px !important;
    margin: 21px 0 !important;
  }

  .fixck h2 {
    font-weight: bold !important;
    font-size: 24px !important;
    margin: 19px 0 !important;
  }

  .fixck h3 {
    font-weight: bold !important;
    font-size: 19px !important;
    margin: 18px 0 !important;
  }

  .fixck h4 {
    font-weight: bold !important;
    font-size: 16px !important;
    margin: 21px 0 !important;
  }

  .fixck h5 {
    font-weight: bold !important;
    font-size: 13px !important;
    margin: 22px 0 !important;
  }

  .fixck h6 {
    font-weight: bold !important;
    font-size: 11px !important;
    margin: 24px 0 !important;
  }

  .news_con {
    line-height: 1.8;
    font-size: 16px;
    text-align: justify;
  }
  .el-pagination {
    white-space: normal;
  }
  .message_infos {
    width: 99%;
    margin-left: 10px;
  }
  .noComment {
    width: 100%;
    text-align: center;
  }
  .personResume {
    margin: 20px 20px 20px 20px;
    font-size: 16px;
  }
</style>
