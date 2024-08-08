<template>
  <div class="video-body">
    <div></div>
    <el-row type="flex" >

      <el-col :md="18" :xs="24" :sm="24">
        <div class="mt70">
          <span class="mediaTitle">{{mediaInfo.title}}</span>
        </div>
        <div style="min-height: 529px; margin-top: 10px;">
          <!--嵌入Iframe显示-->
          <iFrame v-show="video.url"
                  v-if="video.storageType === `bilibiliStorage`"
                  :src="video.url"
          />
          <!--本地播放器展示-->
          <video-preview v-else ref="player" :videoId="videoId" :storageType="video.storageType" ></video-preview>
        </div>

        <!-- 评论 -->
        <el-card class="box-card">
            <div class="news_pl" :style="opemCommentCss">
              <h2 v-if="openComment == '1'" class="title">课程评论</h2>
              <Comment v-if="openComment == '1'" :maxReplyLevel="commentInfo.maxReplyLevel"  :targetId="commentInfo.blogUid" :tableName="commentInfo.source"></Comment>
            </div>
        </el-card>

      </el-col>

      <!--右边-->
      <el-col  class="hidden-sm-and-down" style="margin-top: 20px;" :md="6">
        <!--剧情集数-->
        <div class="ml15 mt90">
          <el-menu
            :default-active="`/video/` + video.uid"
            class="el-menu-vertical-demo"
            background-color="#f4f4f4"
            text-color="#222"
            active-text-color="#00a1d6"
            router
          >
            <el-submenu index="0">
              <template slot="title">
                <i class="el-icon-film"></i>
                <span>剧集列表</span>
              </template>
              <el-menu-item  :index="`/video/`+video.uid" v-for="video in mediaInfo.videoList" :key="video.uid" >
                <span class="fsize12">{{ video.title }}</span>
              </el-menu-item>

            </el-submenu>
          </el-menu>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import VideoPreview from '@/components/Play/VideoPreview'
import { getMediaVideo } from '@/api/mediaInfo'
import iFrame from "@/components/iFrame/index";
import Comment from "../components/Comment";

export default {
  name: 'playVideo',
  components: {
    VideoPreview,
    iFrame,
    Comment
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
  data() {
    return {
      openComment: 1,
      activeName: 'description',
      videoId: '',
      mediaInfoUid: '',
      video: {},
      mediaInfo: {},
      sourceVideoUrl: '',
      videoPreviewList: [],
      webConfig: {},
      // 视频秘钥对话框数据
      dialogVideoFile: {
        visible: false,
        passwordForm: {
          password: ''
        },
        passwordFormRules: {
          password: [
            {
              required: true,
              message: '请输入密码',
              trigger: 'blur'
            },
            { min: 6,
              max: 12,
              message:
                '密码长度必须介于 6 和 12 之间',
              trigger: 'blur'
            }
          ]
        }
      },
      tableName: 'MEDIA_INFO',
      comments: [],
      commentInfo: {
        // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
        source: "MEDIA_INFO",
        maxReplyLevel: 4, // 最大回复深度
        blogUid: this.videoId
      },
    }
  },
  created() {
    this.videoId = this.$route.params && this.$route.params.videoId;
    if (!this.videoId) {
      this.mediaInfoUid = this.$route.query.mediaInfoUid
      this.commentInfo.blogUid = this.$route.query.mediaInfoUid
    }
    this.getVideoById(this.videoId);
  },
  mounted() {
  },
  watch: {
    "$route": {
      async handler(route) {
        this.videoId = route.params && route.params.videoId;
        await this.getVideoById(this.videoId);
      }
    }
  },
  methods: {
    getVideoById(videoId) {
      const params = {
        mediaInfoUid: this.mediaInfoUid,
        uid: videoId
      }
      getMediaVideo(params).then(response => {
        this.video = response.data.video;
        this.mediaInfo = response.data.mediaInfo;
        this.playVideo();
      });
    },
    playVideo() {
      this.videoPreviewList = []
      // 如果是B站来源的，直接return
      if (this.video.storageType === `bilibiliStorage`){
        return
      }
      // 从视频文件中加载对应的清晰度
      if (this.video.m3u8Url) {
        this.addVideoPreview(this.video.name, this.video.m3u8Url, 'application/x-mpegURL', true);
      } else {
        this.addVideoPreview(this.video.name, this.video.url, 'video/mp4', false);
      }
      let data = {
        videoPreviewVisible: true,
        videoPreviewList: this.videoPreviewList,
        activeIndex: 0
      };
      this.$store.commit('setVideoPreviewData', data);
    },
    addVideoPreview(name, url, type, openMediaPlay) {
      let data = {
        withCredentials: false,
        type: type,
        src: url,
        name: name,
        openMediaPlay: openMediaPlay,
      };
      this.videoPreviewList.push(data)
    }
  }
}
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  height: 440px;
  overflow: auto;
}

.video-body{
  width: 1200px;
  margin: auto;
}
.mediaTitle {
  font-size: 18px;
  color: #1c1f21;
  font-weight: 700;
}

.fsize12{font-size:12px}
.fsize20{font-size:20px}
.ml15{margin-left:15px}
.mt70{margin-top:70px}
.mt90{margin-top:90px}
</style>
