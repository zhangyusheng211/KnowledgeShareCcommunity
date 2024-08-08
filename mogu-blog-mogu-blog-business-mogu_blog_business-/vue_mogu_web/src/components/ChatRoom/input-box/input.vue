<template>
  <div class="full-height">
    <div class="input-tool-bar">
      <emoji v-if="showEmoji" class="input-tool-item" @selected="emojiSelected" @sendPic="sendPic" bottomIndex="210px"></emoji>
      <recording v-if="showRecord" class="input-tool-item" @sendAudio="sendAudio"></recording>
      <pic v-if="showPicture" class="input-tool-item" @sendPic="sendPic"></pic>

      <span v-if="showCall" class="iconfont pointer input-tool-item" style="font-size: 20px;" @click.stop="callVoice">&#xe61a;</span>
      <span v-if="showVedio" class="iconfont pointer input-tool-item" style="font-size: 20px;" @click.stop="callVideo">&#xe61f;</span>

    </div>
    <div class="chat-input-box-container">
      <!-- <div v-on:keyup.enter.ctrl.exact="sendMessage" class="chat-input-box" ref="inputBox" v-on="listeners" v-bind="$attrs" :contenteditable="contenteditable"></div> -->
      <div @paste.capture.prevent="handlePaste" v-on:keyup.ctrl.enter="sendMessage" class="chat-input-box" ref="inputBox"
           v-on="listeners" v-bind="$attrs" :contenteditable="contenteditable"></div>
      <a class="input-send-button" href="javascript:void(0)" @click="sendMessage">发送[Ctrl+Enter]</a>

      <AtUser :input-content="inputContent" topIndex="-140px"  @selectUser="selectUser"></AtUser>

    </div>

  </div>
</template>
<script>
import emoji from "./emoji.vue";
import recording from "./recording.vue";
import pic from "./pic.vue";

import {uploadFile} from "../../../api/chat";
import {getCookie} from "@/utils/cookieUtils";
import AtUser from "@/components/AtUser";

export default {
  components: {emoji, recording, pic, AtUser},
  data() {
    return {
      contenteditable: true,
      inputContent: "",
      showEmoji: false,
      showPicture: false,
      showRecord: false,
      showCall: false,
      showVedio: false,
      pasteType: ["image/jpeg", "image/png", "image/jpg", "image/gif"],
      imgCount: 0,
      imgSrcMap: {},
      replaceMap: null,
      atUserUid: "",
      authCode: {},
    };
  },
  watch: {
    // 判断状态是否改变
    "$store.state.app.webConfigData": function (newFlag, oldFlag) {
      this.setLoginTypeList();
    },
    // 判断状态是否改变
    "$store.state.app.chatMessageEvent": function (newFlag, oldFlag) {
      console.log("聊天输出框接收到变动")
      this.appendMessage()
    },
    '$store.state.user.authCode': function (newFlag, oldFlag) {
      this.authCode = this.$store.state.user.authCode
    }
  },
  created() {
    this.setLoginTypeList();
    this.authCode = this.$store.state.user.authCode
  },
  methods: {
    selectUser(user, newText) {
      this.inputContent = newText
      this.$refs.inputBox.innerHTML = newText
      this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      this.atUserUid = user.uid
      console.log("替换后", this.inputContent)
    },
    replaceFromEnd(str, oldStr, newStr) {
      let index = str.lastIndexOf(oldStr);
      if (index === -1) {
        return str;
      }
      let before = str.slice(0, index);
      let after = str.slice(index).replace(oldStr, newStr);
      return before.concat(after);
    },
    appendMessage() {
      let chatMessageEvent = this.$store.state.app.chatMessageEvent
      console.log("事件：", chatMessageEvent)
      let event = chatMessageEvent.event
      let messageText = chatMessageEvent.messageText
      let messageHtml = chatMessageEvent.messageHtml
      this.inputContent = this.inputContent + " " + messageHtml + " "
      this.atUserUid = chatMessageEvent.atUserUid

      this.$refs.inputBox.innerHTML = this.inputContent
      if (event === "at") {
        // AT动作
        console.log("触发AT动作")
        this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      } else if(event === "quote") {
        // 引用事件
        console.log("触发引用动作")
        this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      } else {
         // 移动元素光标到前方
         this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      }
    },
    // 视频通话
    callVideo() {
      this.$emit('call', 20004);
    },
    // 语音通话
    callVoice() {
      this.$emit('call', 20005);
    },
    setLoginTypeList: function () {
      let webConfigData = this.$store.state.app.webConfigData;
      console.log("获取聊天类型", webConfigData);
      if (webConfigData.chatTypeList != undefined) {
        let chatTypeList = JSON.parse(webConfigData.chatTypeList);
        for (let a = 0; a < chatTypeList.length; a++) {
          switch (chatTypeList[a]) {
            case "1": {
              this.showEmoji = true;
            }
              break;
            case "2": {
              this.showRecord = true;
            }
              break;
            case "3": {
              this.showPicture = true;
            }
              break;
            case "4": {
              this.showCall = true;
            }
              break;
            case "5": {
              this.showVedio = true;
            }
              break;
            default: {
              console.log("聊天方式设置有误！！");
            }
          }
        }
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
    clear() {
      this.inputContent = "";
      document.querySelector(".chat-input-box").innerText = "";
    },
    sendPic(url, fileUid) {
      let imgTag = `<img class="chat-msg-img"  src="${url}" uid="${fileUid}">`;

      let messageVO = {
        "category": 20003,
        "content": imgTag,
        "duration": 0,
        "atUserUid": this.atUserUid,
      }
      this.$emit("send", messageVO);
    },
    sendAudio(url, duration) {
      let messageVO = {
        "category": 20002,
        "content": url,
        "duration": duration,
        "atUserUid": this.atUserUid,
      }
      this.$emit("send", messageVO);
    },
    sendMessage() {
      let _this = this;
      _this.inputContent = _this.inputContent.replace(/<div>/g, "");
      _this.inputContent = _this.inputContent.replace(/<\/div>/g, "");
      // _this.inputContent = _this.inputContent.replace(/<br>/g, "");
      // _this.inputContent = _this.inputContent.replace(/&nbsp;/g, "");

      // 替换内容
      if (this.replaceMap != null) {
        let replaceMap = this.replaceMap
        for(let key of replaceMap.keys()){
          console.log("开始替换内容Key:", key)
          let value = replaceMap.get(key)
          console.log("开始替换内容Value: ", value)
          if (value) {
            _this.inputContent = _this.inputContent.replace(key, value);
          }
        }
      }

      // 聊天图片处理
      let imgKeys = [];
      let matchs = _this.inputContent.match(
        /[<]img[^>]*a="([^"]*)"[^>]*[>]/g
      );
      // 截取文本中的img
      if (matchs) {
        // 没有匹配一张图片的时候
        if (matchs.length === 0) {

          let messageVO = {
            "category": 20001,
            "content": _this.inputContent,
            "duration": 0,
            "atUserUid": this.atUserUid,
          }


          _this.$emit("send",messageVO);
          return;
        }
        if (matchs.length > 5) {
          this.$message.error("请勿一次发送太多图片");
          return;
        }

        for (let i = 0; i < matchs.length; i++) {
          let match = matchs[i];
          imgKeys.push(
            match.match(/[<]img[^>]*a="([^"]*)"[^>]*[>]/)[1]
          );
        }
        if (imgKeys.length > 0) {
          _this.uploadPasteImgs(imgKeys).then(
            (res) => {
              // 替换聊天窗口中的图片
              let dataArr = res.data;
              for (let i = 0; i < dataArr.length; i++) {
                let imgTag = `<img class="chat-msg-img"  src="${dataArr[i].pictureUrl}">`;
                _this.inputContent = _this.inputContent.replace(
                  matchs[i],
                  imgTag
                );
              }
              // 发送聊天
              let messageVO = {
                "category": 20001,
                "content": _this.inputContent,
                "duration": 0,
                "atUserUid": "",
              }
              _this.$emit("send", messageVO);
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
        // 不存在文本时候
        let messageVO = {
          "category": 20001,
          "content": _this.inputContent,
          "duration": 0,
          "atUserUid": this.atUserUid,
        }
        _this.$emit("send", messageVO);
      }
    },
    uploadPasteImgs(imgKeys) {
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
    emojiSelected(e, src, imageUrl) {
      this.getFocus();
      // let imgSrc = require(`@/assets/emoji/${src}`);
      let imgTag = `<img src="${imageUrl}" class="chat-msg-emoji" width="20" height="20" style="height: 20px;width: 20px; display: inline-block; margin-left: 2px; vertical-align: middle; margin-right: 2px;">`;
      document.execCommand("insertHTML", false, imgTag);
    },
    getFocus() {
      let range = window.getSelection(); //创建range
      range.selectAllChildren(this.$refs.inputBox); //range 选择obj下所有子内容
      range.collapseToEnd(); //光标移至最后
    },
  },
  computed: {
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
};
</script>

<style scoped>
.input-tool-item {
  cursor: pointer;
}
.chat-input-box-container {
  width: 100%;
  min-height: 150px;
  padding: 5px;
  position: relative;
  color: #1c1b19;
}

.chat-input-box {
  width: 100%;
  height: 100%;
  overflow-y: scroll;
}

.chat-input-box[contenteditable] {
  outline: none;
  height: 166px;
}

.input-tool-bar {
  height: 33px;
  border-top: 1px solid #00000030;
  /*border-bottom: 1px solid #00000030;*/
}

.input-send-button {
  position: absolute;
  bottom: 10px;
  right: 20px;
  margin: 0 auto;
  width: 160px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  text-decoration: none;
  font-size: 18px;
  color: #fff;
  background: linear-gradient(to right, #03a9f4, #f44141, #ffeb3b, #09a8f4);
  background-size: 400%;
  border-radius: 50px;
  /*z-index: 1;*/
}

.input-send-button::before {
  content: "";
  position: absolute;
  top: -5px;
  left: -5px;
  bottom: -5px;
  right: -5px;
  background: linear-gradient(to right, #03a9f4, #f44141, #ffeb3b, #09a8f4);
  background-size: 400%;
  border-radius: 50px;
  z-index: -1;
  filter: blur(20px);
}

.input-send-button:hover {
  /* 执行动画 */
  animation: streamer 18s infinite;
}

.input-send-button:hover::before {
  animation: streamer 18s infinite;
}

@keyframes streamer {
  100% {
    background-position: -400% 0;
  }
}
</style>
