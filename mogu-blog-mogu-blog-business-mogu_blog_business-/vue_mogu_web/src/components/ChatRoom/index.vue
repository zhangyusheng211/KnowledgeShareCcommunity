<template>
  <div>
    <div class="chat-room-container" style="color: #f4f4f4;">
      <div class="chat-body">
        <el-row>
          <el-col :span="6" style="background-color: #2e3238;">
            <left-panel :user-uid="userUid" ref="left-panel" @newRoom="newRoomHandle"
                        @selectRoom="selectRoom"></left-panel>
          </el-col>
          <el-col :span="18" style="border-left: 1px solid #00000030; background-color: #eee;">
            <el-row>
              <msg-box  @read="read" @send="sendMessage" ref="msg-box" @withdrawMsg="withdrawMsgHandle" ></msg-box>
            </el-row>
            <el-row>
              <div class="message-input-box">
                <chatInput ref="chatInput" @send="sendMessage" @call="call"></chatInput>
              </div>
            </el-row>
          </el-col>
        </el-row>
      </div>

      <div v-show="calling || onCall" class="video-container" id="calling-container">
        <video muted v-show="onCall && initvideo" class="local-video" id="local-video" autoplay></video>
        <video v-show="onCall && initvideo" class="remote-video" id="remote-video" autoplay></video>
        <span v-show="onCall && !initvideo" class="calling-span">语音通话中</span>
        <span v-show="calling" class="calling-span"> 呼叫中... </span>
        <span v-show="connecting" class="calling-span">正在建立连接</span>
        <a class="hangup fa fa-phone" href="javascript:void(0)" @click="hangup">挂断</a>
      </div>
    </div>
  </div>
</template>
<script>
import chatInput from "./input-box/input.vue";
import LeftPanel from "./left/left-panel.vue";
import MsgBox from "./right/msg-box.vue";
import {getCookie} from "@/utils/cookieUtils";
import {animate, clearAll, init} from "./js/calling-animation";
import {MessageBox} from "element-ui";


var iceServers = {
  iceTransportPolicy: "relay",
  iceServers: [
    {
      urls: [
        'turn:81.70.100.87:3478?transport=udp',
        "turn:81.70.100.87:3478?transport=tcp",
      ],
      credential: "Mogu2022",
      username: "mogu",
    },
  ],
};


export default {
  components: {chatInput, LeftPanel, MsgBox},
  name: "chat-room",
  data() {
    var _this = this;
    return {
      chatIsOpen: false,
      msgLoading: false,
      chating: false,
      // 当前选中的聊天室
      currentRoom: {},
      webSocket: null,
      query: {
        toUserId: null,
        page: 1,
        pageSize: 20,
        messageType: null,
      },
      WS_API: process.env.WS_API,
      userUid: null,
      // 视频通话
      localStream: null,
      localPeer: null,
      // 正在呼叫
      calling: false,
      // 通话中
      onCall: false,
      callMessageId: null,
      // 时长
      duration: 0,
      // 计时器
      interval: null,
      // 是否视频通话
      initvideo: false,
      callCategory: null,
      hasMessageBox: false,
      connecting: false,
      watchMessage: false,
      defaultTitle: document.title,
    };
  },
  watch: {
    $route(to, from) {
      window.location.reload();
    },
    // 判断个人中心是否弹出新内容(用于控制弹出框)
    watchMessage: function (event, oldFlag) {
      console.log("处理领域事件", event)
      // this.domainEventHandler(event)
    },
  },
  created() {
    this.userUid = this.$route.query.userUid;
    this.initWebSocket();
  },
  destroyed() {
    // 离开路由之后断开websocket连接
    this.webSocket.close();
  },
  beforeDestroy() {
    // console.log("离开页面")
    // if (this.watchMessage) {
    //   document.title = "收到新的消息"
    // }
    document.removeEventListener("visibilitychange", this.handleVisibilityChange);
  },
  mounted() {
    document.addEventListener("visibilitychange", this.handleVisibilityChange);
  },
  methods: {
    handleVisibilityChange() {
      if (document.visibilityState === "visible") {
        // 用户停留在当前页面
        console.log("用户停留在当前页面");
        document.title = this.defaultTitle
        this.watchMessage = false
        // 可以在这里执行相关逻辑
      } else {
        // 用户离开了当前页面
        console.log("用户离开了当前页面");
        if (this.watchMessage) {
          document.title = "收到一条新的消息~"
        }
        // 可以在这里执行相关逻辑
      }
    },
    hangup() {
      let _this = this;
      // 发给对方，这里由于双方都可以挂断，所以需要判断一下接收人
      let toUserId =
        _this.currentRoom.receiveId == _this.$store.state.user.uid
          ? _this.currentRoom.belongUserId
          : _this.currentRoom.receiveId;
      let messageVo = {
        toUserId: toUserId,
        messageType: 10008,
        type: "hang-up",
        uid: _this.callMessageId,
        duration: _this.duration,
        category: _this.callCategory,
        roomId: _this.currentRoom.sessionId,
      };
      // 发起方挂断
      if (_this.calling) {
        // 取消通话
        messageVo.type = "cancel";
      }
      _this.doSend(messageVo);
    },
    doHangUp(messageVo) {
      let _this = this;
      _this.callMessageId = null;
      // 清理计时器
      clearInterval(this.interval);
      // 清空时长
      _this.duration = 0;
      // 等待接听状态对方挂断
      if (messageVo.type == "cancel" && _this.hasMessageBox) {
        MessageBox.close();
      }
      _this.$message("已断开");
      if (_this.localPeer) {
        _this.localPeer.close();
        _this.localPeer = null;
      }
      _this.calling = false;
      _this.onCall = false;
      if (_this.localStream) {
        _this.localStream.getTracks().forEach((track) => {
          track.stop();
        });
        _this.localStream = null;
      }
      // 清空动画背景
      clearAll();
    },
    async apply(messageVo) {
      let _this = this;
      switch (messageVo.type) {
        case "id":
          _this.callMessageId = messageVo.id;
          break;
        case "check-online":
          if (messageVo.message == "online") {
            _this.doCall();
          } else {
            this.$notify.error({
              title: "通知",
              message: "对方不在线",
              offset: 100,
            });
          }
          break;
        case "call": // 收到视频通话邀请
          console.log("收到通话申请");
          let msg =
            messageVo.category == 20004 ? "视频通话" : "语音通话";
          _this.hasMessageBox = true;
          MessageBox.confirm(
            `用户${messageVo.fromUserNickName}向你发起${msg}`,
            "提示",
            {
              confirmButtonText: "接听",
              cancelButtonText: "挂断",
              closeOnClickModal: false,
              type: "info",
              callback: async (action) => {
                _this.hasMessageBox = false;
                _this.callMessageId = messageVo.uid;
                if (action === "confirm") {
                  // 开始建立连接
                  _this.connecting = true;
                  _this.calling = false;
                  _this.callCategory = messageVo.category;
                  _this.initvideo = messageVo.category == 20004;
                  // 接听后弹出聊天页面
                  _this.chooseRoom(messageVo);
                  init();
                  animate();
                  // 接听后初始化本地的连接与事件
                  _this.initConnect().then(() => {
                    console.log('本地连接与事件初始化完成');
                    // 通知对方 已接受通话邀请
                    _this.sendCallMessage(
                      "accept",
                      messageVo.category,
                      messageVo.fromUserId,
                      messageVo.roomId,
                      messageVo.uid
                    );
                  });
                } else {
                  // 通知对方 已拒绝通话邀请
                  _this.sendCallMessage(
                    "reject",
                    messageVo.category,
                    messageVo.fromUserId,
                    messageVo.roomId,
                    messageVo.uid
                  );
                }
              },
            }
          );
          break;
        case "accept": // 对方接受了通话
          console.log("对方接受了通话");
          _this.callMessageId = messageVo.uid;
          // 开始建立连接
          _this.connecting = true;
          // 初始化连接与本地流媒体
          _this.initConnect('receive').then(() => {
            // 发送offer
            _this.sendOffer().then(offer => {
              console.log("offer=======================");
              console.log(offer);
              console.log("offer=======================");
              _this.localPeer.setLocalDescription(offer);
              // 发送offer到对方
              let messageVoT = {
                sdp: offer,
                messageType: 10008,
                type: "offer",
                toUserId: messageVo.fromUserId,
              };
              _this.doSend(
                messageVoT
              );
            })
          });
          break;
        case "cancel": // 取消
        case "reject": // 拒接
        case "hang-up": // 挂断
          console.log("断开");
          _this.doHangUp(messageVo);
          break;
        case "ice": // 接收到通话发起方传递的ice候选信息
          console.log("收到对方传来的ice候选信息");
          // var candidateInfo = JSON.parse(messageVo.sdp);
          // var candidate = new RTCIceCandidate({
          //     sdpMLineIndex: candidateInfo.sdpMLineIndex,
          //     candidate: candidateInfo.candidate
          // });
          // await _this.localPeer.addIceCandidate(candidate);
          await _this.localPeer.addIceCandidate(
            JSON.parse(messageVo.sdp)
          );
          break;
        case "offer": // 发起方的offer信息
          // 设置远程端 offer
          _this.localPeer.setRemoteDescription(
            JSON.parse(messageVo.sdp)
          );
          // 创建 answer
          let answer = await _this.localPeer.createAnswer();
          // 设置本地 answer 描述
          _this.localPeer.setLocalDescription(answer);
          let answerVo = {
            sdp: answer,
            messageType: 10008,
            type: 'answer',
            toUserId: messageVo.fromUserId
          };
          // 发送answer
          _this.doSend(answerVo);
          break;
        case "answer": // 接收端的offer信息
          _this.localPeer.setRemoteDescription(
            JSON.parse(messageVo.sdp)
          );
          break;
      }
    },
    sendOffer() {
      return this.localPeer.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true,
      });
    },
    initConnect(type) {
      let _this = this;
      return new Promise((resolve, reject) => {
        _this.initLocalMedia().then(async stream => {
          // ====================解决浏览器差异初始化Connection========================
          let PeerConnection =
            window.RTCPeerConnection ||
            window.mozRTCPeerConnection ||
            window.webkitRTCPeerConnection;
          _this.localPeer = new PeerConnection(iceServers);
          // ====================解决浏览器差异初始化Connection========================


          // ====================流媒体添加至通道========================
          console.log("正在添加流媒体至通道...");
          console.log(stream);
          for (const track of stream.getTracks()) {
            await _this.localPeer.addTrack(track, stream);
          }
          // _this.localPeer.addStream(stream);
          console.log("流媒体已添加至通道...");
          // ====================流媒体添加至通道========================


          // ====================注册各种事件========================
          _this.registryEvent(type);
          // ====================注册各种事件========================

          resolve();
        }, err => {
          reject(err);
        });
      })
    },
    // 注册各种事件
    registryEvent(type) {
      let _this = this;
      // 流数据回调
      // _this.localPeer.onaddstream = (event) => {
      //     // 连接建立完成
      //     console.log("连接建立完成...");
      //     _this.connecting = false;
      //     // 记录通话时长
      //     _this.interval = setInterval(
      //         () => {
      //             this.duration += 1;
      //         },
      //         1000,
      //         1000
      //     );
      //     _this.onCall = true;
      //     _this.calling = false;
      //     // 远程的流媒体展示
      //     let video = document.querySelector("#remote-video");
      //     video.srcObject = event.stream;
      // };
      _this.localPeer.ontrack = (event) => {
        _this.connecting = false;
        if (!_this.onCall) {
          console.log("连接建立完成...");
          // 记录通话时长
          _this.interval = setInterval(
            () => {
              this.duration += 1;
            },
            1000,
            1000
          );
        }
        _this.onCall = true;
        _this.calling = false;
        let videoTag = document.getElementById("remote-video");
        videoTag.srcObject = event.streams[0];
        console.log("接收到Media消息，数据如下：");
        console.log(event);
        // console.log(event.streams[0]);
      };

      // if (type == 'receive') {
      console.log('开始监听 icecandidate.....');
      // 发起通话方
      _this.localPeer.onicecandidate = (event) => {
        console.log('event.candidate ======>');
        console.log(event.candidate);
        // 通过 socket 将ice候选发送给对方
        if (event.candidate) {
          let messageVo = {
            sdp: event.candidate,
            messageType: 10008,
            type: 'ice',
            toUserId: _this.currentRoom.receiveId
          };
          _this.doSend(messageVo);
        }
      };
      // }

      // 监听sdp
      _this.localPeer.iceconnectionstatechange = event => {
        console.log(
          `iceGatheringState======>${_this.localPeer.iceConnectionState}`
        );
        console.log(event)
      };
      // 状态监听事件
      _this.localPeer.onconnectionstatechange = (
        event
      ) => {
        let state = _this.localPeer.connectionState;
        console.log("连接状态：" + state);
        switch (state) {
          // case 'disconnected':
          case "failed":
            // 出现异常，走hangup
            _this.$message.error(
              "网络异常，聊天中断!"
            );
            _this.hangup();
            break;
        }
      };
    },
    initLocalMedia() {
      let _this = this;
      return new Promise((resolve, reject) => {
        try {
          // 老的浏览器可能根本没有实现 mediaDevices，所以我们可以先设置一个空的对象
          if (navigator.mediaDevices === undefined) {
            navigator.mediaDevices = {};
          }
          // 一些浏览器部分支持 mediaDevices。我们不能直接给对象设置 getUserMedia
          // 因为这样可能会覆盖已有的属性。这里我们只会在没有getUserMedia属性的时候添加它。
          if (navigator.mediaDevices.getUserMedia === undefined) {
            navigator.mediaDevices.getUserMedia = function (
              constraints
            ) {
              // 首先，如果有getUserMedia的话，就获得它
              var getUserMedia =
                navigator.webkitGetUserMedia ||
                navigator.mozGetUserMedia;
              // 一些浏览器根本没实现它 - 那么就返回一个error到promise的reject来保持一个统一的接口
              if (!getUserMedia) {
                return Promise.reject(
                  new Error(
                    "该浏览器不支持getUserMedia函数"
                  )
                );
              }
              // 否则，为老的navigator.getUserMedia方法包裹一个Promise
              return new Promise(function (resolve, reject) {
                getUserMedia.call(
                  navigator,
                  constraints,
                  resolve,
                  reject
                );
              });
            };
          }

          navigator.mediaDevices
            .getUserMedia({audio: true, video: _this.initvideo})
            .then((stream) => {
              _this.localStream = stream;
              console.log('本地Media媒体信息如下：');
              console.log(stream);
              let video = document.querySelector("#local-video");
              video.srcObject = stream;
              resolve(stream);
            })
            .catch((err) => {
              reject(err);
            });
        } catch (err) {
          console.log("本地连接开始失败");
          console.log(err);
          reject(err);
        }
      });
    },
    call(category) {
      if (this.currentRoom.uid == "10001") {
        this.$message.error("聊天室无法使用该功能");
        return;
      }
      console.log("开始电话", this.currentRoom);
      let _this = this;
      // 检查对方是否在线
      _this.checkOnline();
      _this.callCategory = category;
    },
    doCall() {
      let _this = this;
      _this.calling = true;
      _this.connecting = false;
      _this.callMessageId = null;
      _this.initvideo = _this.callCategory == 20004;
      init();
      animate();
      // 发起呼叫
      _this.sendCallMessage(
        "call",
        _this.callCategory,
        _this.currentRoom.receiveId,
        _this.currentRoom.sessionId,
        null
      );
    },
    checkOnline() {
      let _this = this;
      _this.sendCallMessage(
        "check-online",
        null,
        _this.currentRoom.receiveId,
        null,
        null
      );
    },
    withdrawMsgHandle(messageId) {
      let _this = this;
      let messageVo = {
        messageType: 10011,
        type: "123",
        uid: messageId,
        message: ""
      };
      // 撤回消息
      _this.doSend(messageVo);
    },
    sendCallMessage(type, category, toUserId, roomId, messageId) {
      let _this = this;
      let messageVo = {
        toUserId: toUserId,
        messageType: 10008,
        type: type,
        roomId: roomId,
        category: category,
        uid: messageId,
      };
      _this.doSend(messageVo);
    },
    chooseRoom(messageVo) {
      let _this = this;
      _this.$emit("packup", true);
      setTimeout(() => {
        _this.$refs["left-panel"].mediaRoom(messageVo);
      }, 50);
    },
    packup() {
      this.$emit("packup");
    },
    initWebSocket() {
      if (window.WebSocket) {
        this.webSocket = new WebSocket(
          this.WS_API + `/ws?token=${getCookie("token")}`
        );
        this.webSocket.onmessage = this.onMessage;
        this.webSocket.onopen = this.onOpen;
        this.webSocket.onerror = this.onError;
        this.webSocket.onclose = this.onClose;
      } else {
        alert("您的浏览器不支持websocket");
        // 重定向到首页
      }
    },
    doSend(messageVo) {
      let _this = this;
      let isLogin = this.$store.state.user.isLogin;
      if (!isLogin) {
        this.$notify.error({
          title: "警告",
          message: "登录后才可以接入聊天~",
          offset: 100,
        });
      } else {
        // 登录检查
        _this.webSocket.send(JSON.stringify(messageVo));
      }
    },
    newRoomHandle(toUser) {
      let _this = this;
      let messageVo = {
        messageType: 10010,
        messageLevel: 10003,
        category: 20001,
        toUserId: toUser.uid,
        toUserAvatar: toUser.photoUrl,
        toUserNickName: toUser.nickName,
        message: "你好，交个朋友",
      };
      _this.doSend(messageVo);
    },
    // 检查是否@了用户
    checkAtUser(messageVO) {
      // 校验是否包含@用户的信息
      let pattern = /href=\"([^\"]+)\"[^>]+>([^<]+)/; // 匹配链接和链接文字的正则表达式
      let matches = messageVO.content.match(pattern); // 提取链接和链接文字
      if (matches) {
        let url = matches[1];
        return url.match(/userUid=([\w]+)/)[1]
      }
      return ""
    },
    sendMessage(messageVO) {
      console.log("发送消息", messageVO)
      let category = messageVO.category
      let content = messageVO.content
      let duration = messageVO.duration
      let atUserUid = this.checkAtUser(messageVO)

      let _this = this;
      // 清空输入框的内容
      _this.$refs.chatInput.clear();
      if (!content.trim()) {
        _this.$message.error("请输入内容");
        return;
      }
      let messageVo = {
        roomId: _this.currentRoom.sessionId,
        messageLevel: 10003,
        message: content,
        category: category,
        duration: duration,
        toUserId: _this.currentRoom.receiveId,
        messageType: _this.currentRoom.roomType,
        atUserUid: atUserUid,
      };
      _this.doSend(messageVo);
    },
    onMessage(e) {
      let _this = this;
      let messageVo = JSON.parse(e.data);
      if (messageVo.messageLevel && messageVo.messageLevel === 40005) {
        this.$notify.error({
          title: "警告",
          message: messageVo.message,
          offset: 100,
        });
        return;
      }
      if (messageVo.messageType == 10010) {
        // 刷新聊天室列表
        _this.$refs["left-panel"].refreshRoomList(messageVo);
        return;
      } else if (messageVo.messageType == 10011) {
        // 撤回消息
        _this.$refs["left-panel"].refreshRoomListWithdraw(messageVo);
        return;
      } else if (messageVo.messageType == 10008) {
        // 视频通话相关
        _this.apply(messageVo);
        if (
          messageVo.type == "hang-up" ||
          messageVo.type == "reject" ||
          messageVo.type == "cancel"
        ) {
          _this.$refs["msg-box"].omMessage(messageVo);
          _this.$refs["left-panel"].omMessage(messageVo);
        }
      } else {
        // 收到新的消息
        let userUid = this.$store.state.user.userInfo.uid
        if (messageVo.fromUserId != userUid) {
          // 当前操作的用户不是当前登录人
          this.watchMessage = true
          this.handleVisibilityChange()
        }
        _this.$refs["msg-box"].omMessage(messageVo);
        _this.$refs["left-panel"].omMessage(messageVo);
      }
    },
    read(messageVo) {
      let _this = this;
      messageVo.messageType = 10003;
      _this.doSend(messageVo);
    },
    selectRoom(roomInfo) {
      let _this = this;
      _this.chating = true;
      _this.currentRoom = roomInfo;

      _this.$refs["msg-box"].selectRoom(roomInfo);
    },
    onOpen() {
      this.chatIsOpen = true;
      console.log("websocket已连接");
    },
    onError() {
      this.chatIsOpen = false;
      console.log("websocket连接失败");
    },
    onClose(e) {
      this.chatIsOpen = false;
      // this.$message.success("已断开连接")
      console.log("断开连接", e);
    },
  },
};
</script>

<style lang="less">
.el-message {
  z-index: 999999 !important;
}

::-webkit-scrollbar {
  height: 0;
  width: 0;
  color: transparent;
}

.chat-container {
  position: absolute;
  z-index: 10086;
  overflow: hidden;
}

.chat-load-more {
  text-align: center;
  cursor: pointer;
  padding: 5px;
}

.user-item {
  margin: 1px;
  cursor: pointer;
  display: flex;
  padding: 10px;
  border-bottom: 1px solid #e5e5e5;

  img {
    user-select: none;
    align-self: center;
  }

  span {
    align-self: center;
    margin-left: 5px;
  }
}

@media screen and (max-width: 700px) {
  .chat-container {
    bottom: 0px;
    right: 0px;
    width: 100%;
    height: 100%;
  }

  .room-info {
    display: none;
  }

  .room-new-msg-time {
    display: none;
  }

  .chat-body .left {
    width: 80px;
  }

  .chat-body .right {
    width: calc(100% - 82px);
  }

  .left .new-room {
    display: none;
  }

  .room-item {
    position: relative;
    border-bottom: 1px solid #292c33;
  }

  .room-item .room-unread-tips {
    float: right;
    position: absolute;
    bottom: 10px;
    right: 10px;
  }

  .chat-msg-img {
    height: 100px;
  }

  // TODO
  .local-video {
    width: 300px;
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 1001;
  }

  .remote-video {
    width: 100%;
    height: 100%;
    position: absolute;
    right: 0px;
    top: 0px;
    z-index: 1000;
    object-fit: fill;
  }
}

@media screen and (min-width: 700px) {
  .chat-container {
    bottom: 0px;
    right: 0px;
    width: 100%;
    height: 100%;
  }

  .room-info {
    display: block;
  }

  .room-new-msg-time {
    display: block;
  }

  .chat-body .left {
    width: 250px;
  }

  .chat-body .right {
    width: calc(100% - 252px);
  }

  .left .new-room {
    display: block;
  }

  .room-item {
    position: relative;
  }

  .room-item .room-unread-tips {
    float: right;
    position: absolute;
    bottom: 10px;
    right: 10px;
  }

  .chat-msg-img {
    height: 100px;
  }

  // TODO
  .local-video {
    width: 400px;
    position: absolute;
    top: calc(calc(100% - 600px) / 2);
    right: calc(calc(100% - 1024px) / 2);
    z-index: 1001;
  }

  .remote-video {
    width: 1024px;
    height: 600px;
    position: absolute;
    z-index: 1000;
    left: calc(calc(100% - 1024px) / 2);
    top: calc(calc(100% - 600px) / 2);
    object-fit: fill;
  }
}

.chat-room-container {
  width: 100%;
  height: 100%;
  background: #fdfcfc;
  //position: absolute;
  top: 125px;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  border-radius: 5px;
  overflow: hidden;
  border: 2px solid #00000030;
}

.right-container {
  width: 100%;
  height: 100%;
}

.chat-pickup {
  color: #fff;
  padding: 10px;
  font-size: 16px;
  position: absolute;
  right: 10px;
  bottom: 2px;
  user-select: none;
  cursor: pointer;
}

.chat-body {
  width: 100%;
  height: calc(100% - 40px);
}

.chat-body .left {
  float: left;
  height: 100%;
  background: #00000000;
  border-right: 2px solid #00000030;
  position: relative;
}

.left .room-container {
  width: 100%;
  height: 100%;
}

.left .search-container {
  width: 100%;
  height: 100%;
}

.left .search-container .user-list {
  height: calc(100% - 41px);
  overflow-y: auto;
  list-style: none;
  margin: 0px;
  padding: 0px;
}

.left .search-container .search-box {
  height: 40px;
  width: 100%;
}

.left .new-room {
  height: 20px;
  width: 100%;
  text-align: center;
  background: #00000010;
  user-select: none;
  cursor: pointer;
  border-bottom: 1px solid #e5e5e5;
}

.left .new-room span {
  font-size: 18px;
  font-weight: bold;
}

.left .float-tool {
  position: absolute;
  left: 10px;
  bottom: 10px;
}

.float-tool ul {
  list-style: none;
  margin: 0px;
  padding: 0px;
}

.chat-footer {
  width: 100%;
  height: 60px;
  background: #00000030;
}

.room-list {
  height: calc(100% - 21px);
  overflow-y: auto;
  list-style: none;
  margin: 0px;
  padding: 0px;
}

.room-list .active {
  background: #332e2e;
}

.room-item {
  min-height: 50px;
  padding: 10px;
}

.room-item .room-unread-tips {
  float: right;
  margin-top: 5px;
}

.room-avatar {
  width: 40px;
  height: 40px;
  margin-left: 10px;
  border-radius: 50%;
  overflow: hidden;
  float: left;
}

.room-avatar img {
  height: 100%;
  height: 100%;
}

.room-info {
  float: left;
  height: 50px;
  margin-left: 10px;
  width: 50%;
  user-select: none;
}

.room-info .title {
  font-size: 16px;
  margin: 0 5px;
  padding: 0px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.room-info .subtitle {
  max-height: 30px;
  font-size: 12px;
  //margin: 5px 5px;
  padding: 0px;
  //color: #00000050;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.room-new-msg-time {
  float: right;
}

.room-new-msg-time span {
  font-size: 12px;
}

.chat-body .right {
  float: left;
  height: 100%;
}

.chat-body .right .message-body {
  width: 100%;
  height: 65%;
}

.chat-body .right .message-input-box {
  width: 100%;
  height: 35%;
  border-top: 1px solid #00000030;
}

.msg-avatar {
  //width: 40px;
  //height: 40px;
  margin-left: 10px;
  //border-radius: 50%;
  //overflow: hidden;
  float: left;
}

.msg-avatar img {
  height: 100%;
}

.msg-info {
  float: left;
  margin-left: 10px;
  width: calc(100% - 60px);
  user-select: none;

}

.msg-info .msg-header {
  font-size: 16px;
  margin: 0 5px;
  padding: 0px;
}

.msg-info .msg-content {
  font-size: 14px;
  padding: 0px;
  color: black;
  text-align: left;
  margin: 0px;

  word-break: normal;
  width: auto;
  display: block;
  white-space: pre-wrap;
  word-wrap: break-word;
  overflow: hidden;
}

.msg-info .msg-left-content-container {
  margin: 2px 2px;
  padding: 5px 10px;
  position: relative;
  background-color: #a6c6f7;
  border: 1px solid #dcdcdc;
  border-radius: 5px;
  max-width: 500px;
  float: left;
}

.msg-info .msg-left-content-container:after {
  content: "";
  width: 8px;
  height: 8px;
  position: absolute;
  top: 5px;
  left: -5px;
  transform: rotate(50deg);
  background-color: #a6c6f7;
  border: 1px #dcdcdc;
  border-style: none none solid solid;
}

.msg-info .msg-right-content-container {
  margin: 2px 2px;
  padding: 5px 10px;
  position: relative;
  background-color: #a0f3a0;
  border: 1px solid #dcdcdc;
  border-radius: 5px;
  max-width: 500px;
  float: right;
}

.msg-info .msg-right-content-container:after {
  content: "";
  width: 8px;
  height: 8px;
  position: absolute;
  top: 5px;
  right: -5px;
  transform: rotate(40deg);
  background-color: #a0f3a0;
  border: 1px #dcdcdc;
  border-style: solid solid none none;
}

.msg-info .msg-header .send-user {
  font-size: 16px;
}

.msg-info .msg-header .send-time {
  font-size: 12px;
  margin: 5px 5px;
  padding: 0px;
  color: #00000050;
}

.input-toolbar-icon {
  font-size: 32px !important;
  color: #00000040;
}

.input-tool-item {
  color: #1c1b19;
  float: left;
  padding: 5px;
  margin-left: 10px;
}

@media only screen and (max-width: 767px) {
  .msg-info {
    float: left;
    margin-left: 10px;
    width: calc(100% - 5px);
    user-select: none;
  }

  .msg-info .msg-content {
    max-width: 220px;
  }
}

.hangup {
  padding: 10px;
  background: red;
  border-radius: 10px;
  position: absolute;
  bottom: 10px;
  left: 50%;
  color: #fff;
  z-index: 1002;
}

.calling-span {
  position: absolute;
  top: 50%;
  left: 50%;
  color: #fff;
  font-size: 18px;
}

.video-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0px;
  left: 0px;
  z-index: 1000;
}
</style>

<style scoped lang="less">

</style>
