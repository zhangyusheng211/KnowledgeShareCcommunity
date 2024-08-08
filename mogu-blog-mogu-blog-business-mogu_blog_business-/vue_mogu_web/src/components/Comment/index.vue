<template>
  <div  v-if="componentShowMap.showUserComment">
    <CommentBox
      :commentInfo="commentInfo"
      @submit-box="submitBox"
    ></CommentBox>
    <div class="message_infos">

      <CommentList :tableName="tableName" :comments="comments" :authorUid="authorUid" :marginLeft="marginLeft" :commentInfo="commentInfo" :maxReplyLevel="maxReplyLevel"></CommentList>
      <!--加载更多-->
      <LoadMore @loadMore="loadMore" :length="comments.length" :isEnd="isEnd" :loading="loading"></LoadMore>
    </div>
  </div>
</template>

<script>
  import CommentList from "@/components/Comment/CommentList";
  import CommentBox from "@/components/Comment/CommentBox"
  import LoadMore from "@/components/Comment/LoadMore"
  import {addComment, getCommentList} from "../../api/comment";
  import {mapMutations} from "vuex";
  export default {
    name: 'Comment',
    props: {
      targetId: [Number, String],
      tableName: {
        type: String,
        default: '',
      },
      // 控制最大评论层级
      maxReplyLevel: {
        type: Number,
        default: 5,
      },
      // 左边偏移量
      marginLeft: {
        type: Number,
        default: -3,
      },
      // 作者UID
      authorUid: {
        type: String,
        default: '',
      },
    },
    components: {
      //注册组件
      CommentList,
      CommentBox,
      LoadMore,
    },
    data() {
      return {
        comments: [],
        currentPage: 1,
        pageSize: 10,
        isEnd: false, //是否到底底部了
        loading: false, //是否正在加载
        componentShowMap: {},
        commentInfo: {
          blogUid: this.targetId,
          source: this.tableName
        }
      }
    },
    computed: {

    },
    watch: {
      targetId:{
        //深度监听，可监听到对象、数组的变化
        handler(newValue, oldVal){
          console.log("targetId", newValue)
          this.commentInfo.blogUid = newValue;
          this.getCommentDataList();
        }
      },
      '$store.state.app.webConfigData': function (event, oldFlag) {
        this.getComponentShowMap()
      },
    },
    mounted () {
      let that = this;
      if (this.targetId) {
        this.commentInfo.blogUid = this.targetId;
      }
      this.getCommentDataList();
      this.getComponentShowMap()
    },

    methods: {
      ...mapMutations(["setCommentList", 'setDomainEventMessage']),
      getComponentShowMap() {
        let webConfigData = this.$store.state.app.webConfigData
        this.componentShowMap = webConfigData.componentShowMap
      },
      // 发表评论
      submitBox(e) {
        let params = {};
        params.source = e.source;
        params.userUid = e.userUid;
        params.content = e.content;
        params.blogUid = e.blogUid;
        addComment(params).then(response => {
          if (response.code === this.$ECode.SUCCESS){
            if(response.data.auditStatus === "2") {
              this.$message.success("评论成功！");
            } else {
              this.$message.warning("提交成功，请耐心等待审核~");
            }
            let commentData = response.data
            this.comments.unshift(commentData)
            this.isEnd = false
            // 发送评论的领域事件
            let event = {
              "type": "action",
              "action": this.$EAction.Comment,
              "resourceUid": e.blogUid,
              "resourceType": "BLOG",
              "time": new Date(),
            }
            this.setDomainEventMessage(event)
          }else{
            this.$message.error(response.message)
          }
        });
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
      loadMore() {
        console.log("点击加载更多")
        let that = this
        let params = {};
        params.source = that.commentInfo.source;
        params.blogUid = that.commentInfo.blogUid;
        params.currentPage = that.currentPage + 1
        params.pageSize = that.pageSize;
        params.openSecondLevel = true;
        this.loading = true
        getCommentList(params).then(response => {
          if (response.code === this.$ECode.SUCCESS && response.data.records.length > 0) {
            let newDataList = response.data.records
            this.comments = this.comments.concat(response.data.records);
            this.setCommentList(this.comments);
            this.currentPage = response.data.current;
            this.pageSize = response.data.size;
            this.total = response.data.total;
            //全部加载完毕
            if (newDataList.length < this.pageSize) {
              this.isEnd = true;
            }
            if (this.comments.length === this.total) {
              this.isEnd = true;
            }
          } else {
            this.isEnd = true;
          }
          this.loading = false
        });
      },
      getCommentDataList: function() {
        let source = this.commentInfo.source
        let blogUid = this.commentInfo.blogUid
        let sourceList = ["BLOG_INFO", "PROBLEM_INFO", "QUESTION_INFO", "MEDIA_INFO"]
        if(sourceList.includes(source) && !blogUid){
            return
        }
        // if(source === "BLOG_INFO" && !blogUid) {
        //   return
        // }
        // if(source === "PROBLEM_INFO" && !blogUid) {
        //   return
        // }
        // if(source === "PROBLEM_INFO" && !blogUid) {
        //   return
        // }
        let params = {};
        params.source = this.commentInfo.source;
        params.blogUid = this.commentInfo.blogUid;
        params.currentPage = this.currentPage;
        params.pageSize = this.pageSize;
        params.openSecondLevel = true;
        getCommentList(params).then(response => {
          if (response.code === this.$ECode.SUCCESS) {
            let newDataList = response.data.records
            this.comments = response.data.records;
            this.setCommentList(this.comments);
            this.currentPage = response.data.current;
            this.pageSize = response.data.size;
            this.total = response.data.total
            if (newDataList.length < this.pageSize) {
              this.isEnd = true;
            }
            if (this.comments.length === this.total) {
              this.isEnd = true;
            }
          }
        });
      },
    }
  };
</script>


<style scoped>
  .noComment {
    width: 100%;
    text-align: center;
  }
</style>
