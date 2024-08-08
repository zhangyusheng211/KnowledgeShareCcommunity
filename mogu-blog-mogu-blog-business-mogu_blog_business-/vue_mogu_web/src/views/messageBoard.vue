<template>
  <div>
    <div class="pagebg sorts"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">留言</a>
      </h1>
      <Comment style="min-height: 805px;" :marginLeft="6" :tableName="commentInfo.source" :maxReplyLevel="commentInfo.maxReplyLevel"></Comment>
    </div>
  </div>
</template>

<script>
    import Comment from "../components/Comment";
    export default {
        data() {
            return {
                pageMinHeight: 0,
                source: "MESSAGE_BOARD",
                showCancel: false,
                submitting: false,
                value: "",
                comments: [],
                commentInfo: {
                    // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
                    maxReplyLevel: 6, // 最大回复深度
                    source: "MESSAGE_BOARD"
                },
                currentPage: 1,
                pageSize: 10,
                total: 0, //总数量
                toInfo: {},
                userInfo: {}
            };
        },
        watch: {},
        computed: {},
        components: {
          Comment
        },
        created() {

        },
        mounted () {

        },
        methods: {

        }
    };
</script>
<style scope>
  .message_infos {
    width: 100%;
    margin-left: 10px;
  }
  .noComment {
    width: 100%;
    text-align: center;
  }
  .page {
    position: relative;
  }
  .block {
    position: relative;
    bottom: 0px;
  }
</style>
