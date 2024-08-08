<template>
  <div>
    <AtUser v-if="showAtUser" :input-content="inputContent" @selectUser="selectUser" topIndex="40px"></AtUser>
    <div class="commentBox">
    <span class="left" v-if="isShowAvatar">
      <img v-if="isShowAvatar" :src="getUserPhoto" onerror="onerror=null;src=defaultAvatar"/>
    </span>

    <span class="right">
        <div class="chat-input-box-container">
          <div @paste.capture.prevent="handlePaste" v-focus @focus="atUserFocus"  v-on="listeners"  v-model="inputContent" class="chat-input-box" ref="inputBox"  placeholder="既然来了，那就留下些动态吧~ 【支持Markdown语法】" :contenteditable="contenteditable"></div>
        </div>
    </span>

    </div>
    <div class="bottom">
      <el-tooltip
        effect="dark"
        :disabled="authCode.ADD_COMMENT"
        content="权限不足，无法操作"
        placement="top-end"
      >
        <span class="submit p2">
          <el-button type="primary" @click="handleSubmit" :disabled="!authCode.ADD_COMMENT">发送评论</el-button>
        </span>
      </el-tooltip>

      <el-button class="cancel p2" type="info" @click="handleCancel">取消评论</el-button>
      <emoji class="cancel p2" @selected="emojiSelected" @sendPic="sendPic" bottomIndex="15px"></emoji>
    </div>
  </div>

</template>

<script>
  import {dateFormat} from '../../utils/webUtils'
  import {mapGetters, mapMutations} from 'vuex';
  import EmojiPanel from "@/components/EmojiPanel/EmojiPanel.vue";
  import emoji from "@/components/ChatRoom/input-box/emoji.vue";
  import AtUser from "@/components/AtUser";
  import {uploadFile} from "../../api/chat";
  import {getCookie} from "@/utils/cookieUtils";

  export default {
    name: 'CommentBox',
    props: {
      userInfo: {
        type: Object
      },
      // 回复的对象
      toInfo: {
        type: Object
      },
      // 博客信息
      commentInfo: {
        type: Object
      },
      showCancel: {
        type: Boolean,
        default: true
      }
    },
    components: {
      EmojiPanel,
      emoji,
      AtUser,
    },
    data() {
      return {
        showAtUser: false, // 展示AT用户
        comments: [],
        submitting: false,
        value: '',
        user: {},
        isShowEmojiPanel: false, // 是否显示表情面板
        isShowAvatar: true, // 是否显示头像
        defaultAvatar: process.env.defaultAvatar,
        contenteditable: true, // 设置div可编辑
        inputContent: "", // 输入框
        pasteType: ["image/jpeg", "image/png", "image/jpg", "image/gif"],
        imgCount: 0, // 记录图片上传数
        imgSrcMap: {},
        authCode: {},
      }
    },
    computed: {
      ...mapGetters(['getUserPhoto']),
      listeners() {
        return Object.assign({}, this.$listeners, {
          input: function (e) {
            const inputContent =
              this.contentType === "plain"
                ? e.target.textContent
                : e.target.innerHTML;
            this.inputContent = inputContent;
          }.bind(this),
        });
      },
    },
    watch: {
      '$store.state.user.authCode': function (newFlag, oldFlag) {
        this.authCode = this.$store.state.user.authCode
      }
    },
    mounted() {
      // 获取宽高
      window.onresize = () => {
        return (() => {
          this.resizeWin();
        })();
      };
      this.resizeWin();
      document.addEventListener('click', this.handleClickOutside)
    },
    beforeDestroy() {
      document.removeEventListener('click', this.handleClickOutside)
    },
    created() {
      this.authCode = this.$store.state.user.authCode
    },
    methods: {
      //拿到vuex中的写的方法
      ...mapMutations(['setLoginMessage']),
      handleClickOutside(event) {
        if (this.$refs.inputBox && !this.$refs.inputBox.contains(event.target)) {
          this.showAtUser = false
        } else {
          this.showAtUser = true
        }
      },
      handlePaste(e) {
        let _this = this;
        let pastedText = undefined;
        // 兼容IE
        if (window.clipboardData && window.clipboardData.getData) {
          // 获取拷贝进剪切板指定格式的数据 (此处用的Text格式)
          pastedText = window.clipboardData.getData("Text");
          document.execCommand("insertText", false, pastedText);
        } else if (
          (e.clipboardData || e.originalEvent.clipboardData) &&
          (e.clipboardData || e.originalEvent.clipboardData).getData
        ) {
          let clip = (e.originalEvent || e).clipboardData;
          if (clip.types.indexOf("Files") >= 0) {
            let file = clip.files[0];
            if (_this.pasteType.indexOf(file.type) > 0) {
              // 图片粘贴
              let imgSrc = URL.createObjectURL(file);
              let imgTag = `<img src="${imgSrc}" a="${_this.imgCount}" height="100px" style="display: inline-block; margin-left: 2px; vertical-align: middle; margin-right: 2px;">`;
              document.execCommand("insertHTML", false, imgTag);
              // 记录file点击时上传
              _this.imgSrcMap[_this.imgCount] = file;
              _this.imgCount++;
            }
          } else {
            pastedText = (e.originalEvent || e).clipboardData.getData(
              "text/plain"
            );
            document.execCommand("insertText", false, pastedText);
          }
        }
        return false;
      },
      atUserFocus() {
        console.log("获取焦点")
        this.showAtUser = true
      },
      //鼠标进入的事件。
      onEnterTd (index) {
        let tagElement = document.getElementById("atUser"+ index);
        let leftX = tagElement.getBoundingClientRect().left;
        let topX = tagElement.getBoundingClientRect().top;
        this.coorDinatex = leftX;
        this.coorDinatey = topX;
        console.log( `坐标位置为（${leftX},${topX}）`);
      },
      // 选择用户回调
      selectUser(user, newText) {
        this.inputContent = newText
        this.$refs.inputBox.innerHTML = newText
        this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
        this.atUserUid = user.uid
        console.log("替换后", this.inputContent)
      },
      emojiSelected(e, src, imageUrl) {
        // let imgSrc = require(`@/assets/emoji/${src}`);
        let imgTag = `<img src="${imageUrl}" class="chat-msg-emoji" width="20" height="20" style="width:20px; height: 20px; display: inline-block; margin-left: 2px; vertical-align: middle; margin-right: 2px;">`;
        this.inputContent = this.inputContent + imgTag
        this.$refs.inputBox.innerHTML = this.inputContent
        this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      },
      sendPic(url, fileUid) {
        let imgTag = `<img class="chat-msg-img"  src="${url}" style="max-height: 160px"  uid="${fileUid}">`;
        this.inputContent = this.inputContent + imgTag
        this.$refs.inputBox.innerHTML = this.inputContent
        this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      },
      handleSubmit() {
        let info = this.$store.state.user.userInfo
        let isLogin = this.$store.state.user.isLogin
        let content = this.inputContent
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

        if(!content || content === "") {
          this.$notify.error({
            title: '警告',
            message: '评论内容不能为空哦~',
            offset: 100
          });
          return;
        }

        // 计算移除标签后的字符数
        let count = this.$commonUtil.getValueCount(content)
        if (count > 2048) {
          this.$notify.error({
            title: '警告',
            message: '评论字符过多，已超过2048个字符',
            offset: 100
          });
          return
        }

        let userUid = info.uid;
        let toUserUid = "";
        let toCommentUid = "";
        let blogUid = "";

        // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
        let source = "";
        if(this.toInfo) {
          toUserUid = this.toInfo.uid;
          toCommentUid = this.toInfo.commentUid;
        }
        if(this.commentInfo) {
          blogUid = this.commentInfo.blogUid;
          source = this.commentInfo.source;
        }
        this.comments = {
          userUid: userUid,
          toCommentUid: toCommentUid,
          toUserUid: toUserUid,
          content: content,
          blogUid: blogUid,
          source: source,
          reply: [],
        }
        this.value = '';
        this.comments.createTime = dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
        // 图片处理器
        this.imageHandler()
      },
      imageHandler() {
        let _this = this
        // 聊天图片处理
        let imgKeys = [];
        let matchs = _this.inputContent.match(
          /[<]img[^>]*a="([^"]*)"[^>]*[>]/g
        );
        // 截取文本中的img
        if (matchs) {
          // 没有匹配一张图片的时候
          if (matchs.length === 0) {
            this.$emit("submit-box", this.comments)
            return;
          }
          if (matchs.length > 3) {
            this.$message.error("请勿一次发送太多图片");
            return;
          }

          for (let i = 0; i < matchs.length; i++) {
            let match = matchs[i];
            let matchText = match.match(/[<]img[^>]*a="([^"]*)"[^>]*[>]/)[1]
            imgKeys.push(
              matchText
            );
          }
          if (imgKeys.length > 0) {
            _this.uploadPasteImages(imgKeys).then( (res) => {
                // 替换评论窗口中的图片
                let dataArr = res.data;
                for (let i = 0; i < dataArr.length; i++) {
                  let imgTag = `<img class="chat-msg-img" style="max-height: 100px; cursor: pointer;" src="${dataArr[i].pictureUrl}">`;
                  _this.inputContent = _this.inputContent.replace(
                    matchs[i],
                    imgTag
                  );
                }
                let comments = this.comments
                comments.content =  _this.inputContent
                this.$emit("submit-box", comments)
              },
              (err) => {
                _this.$notify.error({
                  title: "警告",
                  message: err.data,
                  offset: 100,
                });
              }
            );
          }
        } else {
          this.$emit("submit-box", this.comments)
        }
      },
      uploadPasteImages(imgKeys) {
        let _this = this;
        return new Promise((resolve, reject) => {
          let formdata = new FormData();
          formdata.append("source", "web");
          formdata.append("userUid", this.$store.state.user.userInfo.uid);
          formdata.append(
            "adminUid",
            "uid00000000000000000000000000000000"
          );
          formdata.append("projectName", "blog");
          formdata.append("sortName", "chat");
          formdata.append("token", getCookie("token"));
          for (let i = 0; i < imgKeys.length; i++) {
            formdata.append("filedatas", _this.imgSrcMap[imgKeys[i]]);
          }
          uploadFile(formdata).then((res) => {
            if (res.code == "success") {
              resolve(res);
            } else {
              reject(res);
            }
          });
        });
      },
      handleCancel() {
        this.value = '';
        this.isShowEmojiPanel = false
        if(this.toInfo) {
          this.$emit("cancel-box", this.toInfo.commentUid)
        }
      },
      resizeWin() {
        //当前window 宽
        let centerWidth = document.documentElement.scrollWidth;
        if (centerWidth > 800) {
          this.isShowAvatar = true
        } else {
          // 是否显示头像框
          this.isShowAvatar = false
        }
      },
    },
  };
</script>


<style scoped>

.chat-input-box-container {
  width: 95%;
  min-height: 120px;
  padding: 5px;
  position: relative;
  background-color: #f6f7fb;
  border: 0.2px solid #e5eff8;
  overflow-y: scroll;
  border-radius: 3px;
}

.chat-input-box {
  width: 100%;
  height: 100%;
}

.chat-input-box:empty::before {
  content: attr(placeholder);
  color: #ccc;
}

.chat-input-box[contenteditable] {
  outline: none;
  height: 120px;
}

  .commentBox {
    /*min-width: 700px;*/
    width: 100%;
    height: 100px;
    margin: 0 auto;
  }
  .commentBox .left {
    display: inline-block;
    width: 4%;
    height: 100%;
    padding-top: 3px;
  }
  .commentBox .left img {
    cursor: pointer;
    margin: 0 auto;
    width: 90%;
    border-radius: 50%;
  }
  .commentBox .right {
    display: inline-block;
    margin: 5px 2px 0 0;
    width: 95%;
    height: 100%;
  }
  textarea::-webkit-input-placeholder {
    color: #909399;
  }
  .commentBox .right textarea {
    color: #606266;
    padding:10px 5px 5px 10px;
    resize: none;
    width: 98%;
    height: 100%;
  }
  .bottom {
    position: relative;
    width: 98%;
    height: 60px;
    line-height: 40px;
    margin-top: 40px;
  }
  .bottom .p2 {
    float: right;
    margin-top: 5px;
    margin-right: 10px;
  }
  .emoji-panel-btn img{
    height: 35px;
    width: 35px;
  }
  .emoji-panel-btn:hover {
    cursor: pointer;
    opacity: 0.8;
  }


  @media only screen and (min-width: 300px) and (max-width: 767px) {
    .commentBox .right {
      display: inline-block;
      margin: 5px 2px 0 0;
      width: 99%;
      height: 100%;
    }
  }
</style>
