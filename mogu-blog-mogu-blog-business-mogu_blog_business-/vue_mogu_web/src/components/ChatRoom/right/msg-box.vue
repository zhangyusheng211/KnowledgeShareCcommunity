<template>
  <div class="message-body">
    <div class="chat-header">
      <span class="chat-header-title">{{ currentRoom.title }}</span>
    </div>
    <ul id="msgList" class="msg-list" style="overflow-y: scroll; padding: 10px;"
        @contextmenu.prevent="onContextmenuAll">
      <div class="chat-load-more" v-show="hasMore" @click="loadMore" v-loading="loadingMore">
        <span style="color: #1C1B19">加载更多</span>
      </div>
      <el-row class="msg-item" v-for="(item, index) in messageList" :key="index" v-show="isCall(item)">
        <div v-if="item.fromUserId !== $store.state.user.userInfo.uid">
          <div class="msg-avatar" @contextmenu.prevent="onContextmenuUser(item)" @mouseover="onEnterTd(item, index)"
               @mouseleave="onLeaveTd(false)">
                      <span :id="getTabIndex(index)" class="pointer" :class="item.userTag > 0 ?'vip-avatar':''" :userid="item.fromUserId" :username="item.fromUserNickName">
                        <el-avatar
                                   :class="item.userTag > 0 ?'vip-color':''"
                                   v-if="item.formUserAvatar"
                                   @click.native="getUserCenter(item.fromUserId)" fit="fill" size="large"

                                   :src="item.formUserAvatar"></el-avatar>
                        <el-avatar v-else fit="fill" size="medium" :src="defaultAvatar">
                          <img :src="defaultAvatar"/>
                        </el-avatar>
                        <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                              v-if="item.userTag > 0">v</span>
                      </span>
          </div>
          <div class="msg-info">
            <p class="msg-header">
              <span class="send-user pointer" @click="getUserCenter(item.fromUserId)">{{ item.fromUserNickName }}</span>
              <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList"
                      :key="userTag.uid" v-if="item.userTag == userTag.dictValue && item.userTag != 0"
                      :type="userTag.listClass">{{ userTag.dictLabel }}
              </el-tag>
              <span class="timeAgo">
                              <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);"
                                    :class="'lv'+ item.userLevel" v-for="userLevel in userLevelDictList"
                                    :key="userLevel.uid" v-if="item.userLevel == userLevel.dictValue">
                                {{ userLevel.remark }}
                              </span>
                          </span>

              <span class="iconfont" v-if="item.gender=='1'"
                    style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
              <span class="iconfont" v-if="item.gender=='2'"
                    style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>

              <span class="send-time" v-if="item.userIpPossession">IP属地:{{ item.userIpPossession }}</span>

              <span class="send-time">{{ timeAgo(item.sendTime) }}</span>
            </p>

            <div class="msg-left-content-container" :id="item.uid" :userid="item.fromUserId"
                 :username="item.fromUserNickName">

              <p v-if="item.category == 20001" class="msg-content" v-highlight @click="imageChange" :id="item.uid" :userid="item.fromUserId"
                 :username="item.fromUserNickName" :message="item.message"
                 @contextmenu.prevent="onContextmenu(item)"  v-html="$xss(item.message, options)"></p>

              <audio v-if="item.category == 20002" :src="item.message" controls="controls">
                你的浏览器不支持
              </audio>

              <div v-if="item.category == 20003"  @contextmenu.prevent="onContextImageMenu" @mousedown="handleMouseDown">
                <span v-html="item.message" class="chat-msg-img" @click="imageChange">
                  {{item.message}}
                </span>
              </div>

              <div v-if="item.category == 20004">
                <span><i class="fa fa-video-camera"></i> {{ getOperator(item) }}</span>
              </div>
              <div v-if="item.category == 20005">
                <span><i class="fa fa-phone"></i> {{ getOperator(item) }}</span>
              </div>
            </div>

          </div>
        </div>

        <el-row v-else style="text-align: right">
          <div style="float: right" class="msg-avatar">
                      <span class="pointer" :class="item.userTag > 0 ?'vip-avatar':''">
                        <el-avatar :class="item.userTag > 0 ?'vip-color':''" v-if="item.formUserAvatar"
                                   @click.native="getUserCenter(item.fromUserId)" fit="fill" size="large"
                                   :src="item.formUserAvatar"></el-avatar>
                        <el-avatar v-else fit="fill" size="medium" :src="defaultAvatar">
                          <img :src="defaultAvatar"/>
                        </el-avatar>
                        <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                              v-if="item.userTag > 0">v</span>
                      </span>
          </div>
          <div style="float: right" class="msg-info">
            <p class="msg-header">
              <span class="send-time">{{ timeAgo(item.sendTime) }}</span>

              <span class="send-time" v-if="item.userIpPossession">IP属地:{{ item.userIpPossession }}</span>

              <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList"
                      :key="userTag.uid" v-if="item.userTag == userTag.dictValue && item.userTag != 0"
                      :type="userTag.listClass">{{ userTag.dictLabel }}
              </el-tag>

              <span class="timeAgo">
                              <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);"
                                    :class="'lv'+ item.userLevel" v-for="userLevel in userLevelDictList"
                                    :key="userLevel.uid" v-if="item.userLevel == userLevel.dictValue">
                                {{ userLevel.remark }}
                              </span>
                          </span>

              <span class="iconfont" v-if="item.gender=='1'"
                    style=" color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
              <span class="iconfont" v-if="item.gender=='2'"
                    style="color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
              <span class="send-user pointer" @click="getUserCenter(item.fromUserId)">{{ item.fromUserNickName }}</span>

            </p>

            <div class="msg-right-content-container" :id="item.uid" :userid="item.fromUserId"
                 :username="item.fromUserNickName">
              <p v-if="item.category == 20001" class="msg-content"  v-highlight @click="imageChange"  :id="item.uid" :userid="item.fromUserId" :username="item.fromUserNickName" :message="item.message"
                 @contextmenu.prevent="onContextmenu" v-html="$xss(item.message, options)"></p>
              <audio v-if="item.category == 20002" :src="item.message" controls="controls">
                你的浏览器不支持
              </audio>
              <div v-if="item.category == 20003"  @contextmenu.prevent="onContextImageMenu">
                <span v-html="item.message" class="chat-msg-img" @click="imageChange">
                  {{item.message}}
                </span>
<!--                <img @click="viewImg(item.message)" class="chat-msg-img" :src="item.message">-->
              </div>
              <div v-if="item.category == 20004">
                <span><i class="fa fa-video-camera"></i> {{ getOperator(item) }}</span>
              </div>
              <div v-if="item.category == 20005">
                <span><i class="fa fa-phone"></i> {{ getOperator(item) }}</span>
              </div>
            </div>
          </div>
        </el-row>
      </el-row>
    </ul>

    <el-image-viewer
      v-if="showBigPic"
      :on-close="closeViewer"
      :url-list="picList"/>

    <UserCard :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey - 20"
              @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>

  </div>
</template>
<script>
import {history, withdrawMsg} from "../../../api/chat";
import {timeAgo} from "../../../utils/webUtils";
import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'
import {getListByDictTypeList} from "@/api/sysDictData"
import {mapMutations} from "vuex";
import UserCard from "../../UserCard";
import {addEmoticon} from "../../../api/emoticon";
import CodeCopy from "@/components/CodeCopy";

export default {
  name: "msg-box",
  data() {
    return {
      dialogImageUrl: null,
      showBigPic: false,
      hasMore: false,
      loadingMore: false,
      messageList: [],
      currentRoom: {},
      picList: [],
      pageSize: 20,
      currentPage: 1,
      defaultAvatar: process.env.defaultAvatar,
      userTagDictList: [],
      userLevelDictList: [],
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      calendarList: [],
      usrCard: "",
      coorDinatex: "",
      coorDinatey: "",
      showUsrCard: false,
      currentId: 1,
      text: "",
      // xss白名单配置
      options : {
        whiteList: {
          div: ['class'],
          br: ['class'],
          a: ['href', 'title', 'target'],
          span: ['class'],
          h1: ['class'],
          h2: ['class'],
          h3: ['class'],
          h4: ['class'],
          h5: ['class'],
          h6: ['class'],
          pre: [],
          code: ['class'],
          p: ['class'],
          ol: [],
          blockquote: ['class'],
          ul: ['class'],
          li: ['class'],
          img: ['class', 'src', 'alt', 'width', 'height', 'style'],
          iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
          hr: [],
          video: ['class', 'src', 'controls'],
          source: ['src', 'type'],
          strong: []
        }
      },
    };
  },
  components: {
    ElImageViewer,
    UserCard,
  },
  mounted() {

  },
  updated() {
    this.update()
  },
  watch: {

  },
  created() {
    this.getDictList()
  },
  methods: {
    ...mapMutations(['setChatMessageEvent']),
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_tag', 'sys_user_level']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelDictList = dictMap.sys_user_level.list
        }
      });
    },
    handleMouseDown(event) {
      console.log('Mouse down...')
      event.stopPropagation()
    },
    onContextmenuAll(event) {
      return false
    },
    onContextmenuUser(user) {
      let userUid = user.fromUserId
      let userName = user.fromUserNickName
      this.$contextmenu({
        items: [
          {
            label: "@TA",
            onClick: () => {
              let messageText = "@" + userName
              let messageHtml = "<a href='" + this.VUE_MOGU_WEB + "/userCenter?userUid=" + userUid + "' target='_blank'>&nbsp;" + messageText + "&nbsp;</a>"
              let event = {
                "event": "at",
                "messageText": messageText,
                "messageHtml": messageHtml,
                "atUserUid": userUid,
              }
              this.setChatMessageEvent(event)
            }
          },
          {
            label: "拍一拍",
            onClick: () => {
              console.log("userInfo", this.$store.state.user.userInfo.nickName)
              let myUserName = "\"" + this.$store.state.user.userInfo.nickName + "\""
              let toUserName = "\"" + userName + "\""

              let messageVO = {
                "category": 20001,
                "content": myUserName + " 拍了拍 " + toUserName,
                "duration": 0,
                "atUserUid": "",
              }
              this.$emit("send", messageVO);
            }
          },
        ],
        event, // 鼠标事件信息
        customClass: "custom-class", // 自定义菜单 class
        zIndex: 3, // 菜单样式 z-index
        minWidth: 100 // 主菜单最小宽度
      });
      return false;
    },
    //获取对应markdown代码块标签
    update() {
      setTimeout(() => {
        document.querySelectorAll('pre').forEach(el => {
          if (el.classList.contains('code-copy-added')) {
            return
          }
          //   https://cn.vuejs.org/v2/api/index.html#Vue-extend
          /* 使用基础 Vue 构造器，创建一个“子类”。参数是一个包含组件选项的对象 */
          let ComponentClass = Vue.extend(CodeCopy)
          let instance = new ComponentClass()
          instance.code = el.innerText
          instance.parent = el
          /* 手动挂载 */
          instance.$mount()
          el.classList.add('code-copy-added')
          el.appendChild(instance.$el)
        })
      }, 100)
    },
    onContextImageMenu(event) {
      console.log("图片上右击", event)
      // 右键操作的文本
      let userUid = event.target.getAttribute("userid")
      let userName = event.target.getAttribute("username")
      let messageUid = event.target.offsetParent.getAttribute("id")
      let fileUid = event.target.getAttribute("uid")
      let fileSrc = event.target.getAttribute("src")
      console.log("获取userUid", userUid)
      console.log("获取messageUid", messageUid)
      console.log("获取fileUid", fileUid)
      console.log("获取fileSrc", fileSrc)
      this.$contextmenu({
        items: [
          // {
          //   label: "复制地址",
          //   icon: "el-icon-copy-document",
          //   divided: true,
          //   onClick: () => {
          //     // 创建输入框元素
          //     let oInput = document.createElement('input');
          //     // 将想要复制的值
          //     oInput.value = fileSrc;
          //     // 页面底部追加输入框
          //     document.body.appendChild(oInput);
          //     // 选中输入框
          //     oInput.select();
          //     // 执行浏览器复制命令
          //     document.execCommand('Copy');
          //     this.$message.success("复制成功")
          //   }
          // },
          {
            label: "收藏表情",
            icon: "el-icon-picture-outline-round",
            onClick: () => {
              if (fileUid) {
                let params = {}
                params.fileUid = fileUid
                params.isSelection = 0
                addEmoticon(params).then(response => {
                  if (response.code == this.$ECode.SUCCESS) {
                    this.$message.success(response.message)
                  } else {
                    this.$message.error(response.message)
                  }
                })
              } else {
                this.$message.error("收藏失败，该图片无法收藏")
              }
            }
          },
          {
            label: "撤回",
            icon: "el-icon-refresh-left",
            onClick: () => {
              this.$emit('withdrawMsg', messageUid);

            }
          },
        ],
        event, // 鼠标事件信息
        customClass: "custom-class", // 自定义菜单 class
        zIndex: 3, // 菜单样式 z-index
        minWidth: 100 // 主菜单最小宽度
      });
      return false;
    },
    onContextmenu(item) {
      // 右键操作的文本
      console.log("右键菜单", item)
      let messageText = event.target.getAttribute("message")
      let message = event.target.getAttribute("message")
      let userUid = event.target.getAttribute("userid")
      let userName = event.target.getAttribute("username")
      let messageUid = event.target.offsetParent.getAttribute("id")
      let fileUid = event.target.getAttribute("uid")
      let fileSrc = event.target.getAttribute("src")

      if (item.message) {
        messageText = item.message
        message = item.message
        userUid = item.fromUserId
        userName = item.fromUserNickName
        messageUid = item.id
      }


      console.log("获取userUid", userUid)
      console.log("获取messageUid", messageUid)
      console.log("获取fileUid", fileUid)
      console.log("获取fileSrc", fileSrc)

      this.$contextmenu({
        items: [
          {
            label: "复制",
            icon: "el-icon-copy-document",
            divided: true,
            onClick: () => {
              // // 创建输入框元素
              let oInput = document.createElement('input');
              // 将想要复制的值
              oInput.value = messageText;
              // 页面底部追加输入框
              document.body.appendChild(oInput);
              // 选中输入框
              oInput.select();
              // 执行浏览器复制命令
              document.execCommand('Copy');
              this.$message.success("复制成功")
            }
          },
          {
            label: "引用",
            divided: true,
            icon: "el-icon-position",
            onClick: () => {
              let userText = "@" + userName
              let atHtml = "<a href='" + this.VUE_MOGU_WEB + "/userCenter?userUid=" + userUid + "' target='_blank'>&nbsp;" + userText + "&nbsp;</a>"
              let messageHtml = atHtml + "<br/> --------- <br/>" + message

              // 设置事件
              let event = {
                "event": "quote",
                "messageText": messageHtml,
                "messageHtml": messageHtml,
                "atUserUid": userUid,
              }
              this.setChatMessageEvent(event)
            }
          },
          {
            label: "翻译",
            icon: "el-icon-printer",
            onClick: () => {
              window.open("https://fanyi.baidu.com/?aldtype=16047#en/zh/" + messageText, "_blank")
            }
          },
          {
            label: "搜一搜",
            divided: true,
            minWidth: 0,
            icon: "el-icon-search",
            children: [
              {
                label: "站内搜索",
                onClick: () => {
                  window.open(this.VUE_MOGU_WEB + "/list?model=0&keyword=" + messageText, "_blank")
                }
              },
              {
                label: "百度搜索",
                onClick: () => {
                  window.open("https://www.baidu.com/s?wd=" + messageText, "_blank")
                }
              },
              {
                label: "Google搜索",
                onClick: () => {
                  window.open("https://www.google.com/search?q=" + messageText, "_blank")
                }
              },
              {
                label: "Stackoverflow",
                onClick: () => {
                  window.open("https://stackoverflow.com/nocaptcha?s=" + messageText, "_blank")
                }
              },
              {
                label: "Github搜索",
                onClick: () => {
                  window.open("https://github.com/search?q=" + messageText, "_blank")
                }
              },
              {
                label: "Gitee搜索",
                onClick: () => {
                  window.open("https://search.gitee.com/?skin=rec&type=repository&q=" + messageText, "_blank")
                }
              },
            ]
          },

          {
            label: "撤回",
            icon: "el-icon-refresh-left",
            onClick: () => {
              // let message = {
              //   uid: messageUid
              // }
              // // 撤回消息
              // withdrawMsg(message).then(response => {
              //   console.log("消息撤回结果", response)
              //   if (response.code == this.$ECode.SUCCESS) {
              //     this.$message.success(response.message)
              //   } else {
              //     this.$message.error(response.message)
              //   }
              // }, function (err) {
              //   console.error(err)
              // });

              console.log("测回的消息ID", messageUid)

              this.$emit('withdrawMsg', messageUid);

            }
          },
        ],
        event, // 鼠标事件信息
        customClass: "custom-class", // 自定义菜单 class
        zIndex: 3, // 菜单样式 z-index
        minWidth: 200 // 主菜单最小宽度
      });
      return false;
    },
    getOperator(message) {
      let operator = "";
      switch (message.operatorType) {
        case 30001:
          operator = `通话时长: ${message.duration}秒`;
          break;
        case 30002:
          operator = "拒接";
          break;
        case 30004:
          operator = "未接听";
          break;
        case 30005:
          operator = "已取消";
          break;
      }
      return operator;
    },
    isCall(message) {
      if (
        (message.category == 20004 || message.category == 20005) &&
        !message.operatorType
      ) {
        return false;
      }
      return true;
    },
    closeViewer() {
      this.showBigPic = false
    },
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showBigPic = true
        this.picList = [e.target.currentSrc]
      }
    },
    omMessage(messageVo) {
      let _this = this;
      // 自己消息回执或当前聊天框消息
      if (
        messageVo.fromUserId == _this.$store.state.user.uid ||
        messageVo.roomId == _this.currentRoom.sessionId
      ) {
        _this.messageList.push(messageVo);
        _this.scrollBottom();
      }
    },
    // 跳转到个人中心页
    getUserCenter: function (userUid) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: userUid}
      });
      window.open(routeData.href, '_blank');
    },
    selectRoom(roomInfo) {
      console.log("选择room", roomInfo)
      let _this = this;
      _this.currentRoom = roomInfo;
      _this.query = {
        roomId: roomInfo.sessionId,
        page: _this.currentPage,
        pageSize: _this.pageSize,
      };
      _this.loadMsgList().then((msgList) => {
        _this.messageList = msgList || [];
        // 滚动到最底部
        _this.scrollBottom();
        if (_this.messageList.length > 0) {
          // 标记已读
          let messageVo = {
            roomId: _this.currentRoom.uid,
            toUserId: _this.currentRoom.receiveId,
            messageType: 10007,
          };
          _this.$emit("read", messageVo);
        }
      });
    },
    loadMsgList() {
      let _this = this;
      return new Promise((resolve, reject) => {
        _this.msgLoading = true;
        history(this.query).then(response => {
          console.log("查询到聊天记录", response)
          if (response.data.records.length < _this.pageSize) {
            _this.hasMore = false;
          } else {
            _this.hasMore = true;
          }
          _this.msgLoading = false;
          resolve(response.data.records);
        }, function (err) {
          console.error(err)
        });
      });
    },
    scrollBottom() {
      this.$nextTick(() => {
        console.log("滚动到最底下")
        let element = document.getElementById("msgList");
        if (element) {
          element.scrollTop = element.scrollHeight;
        }
      });
    },
    loadMore() {
      let _this = this;
      _this.query.page += 1;
      _this.loadingMore = true;
      _this.loadMsgList().then((msgList) => {
        _this.loadingMore = false;
        _this.messageList = msgList.concat(_this.messageList);
      });
    },
    viewImg(url) {
      this.picList = [url]
      this.showBigPic = true;
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },

    getTabIndex: function (index) {
      return "chatId" + index
    },
    //鼠标进入的事件。
    onEnterTd(item, index) {
      this.showUsrCard = true;
      let user = {
        "uid": item.fromUserId,
      }
      this.usrCard = user;
      let tagElement = document.getElementById("chatId" + index);
      let leftx = tagElement.getBoundingClientRect().left;
      let topx = tagElement.getBoundingClientRect().top;
      this.coorDinatex = leftx;
      this.coorDinatey = topx;
      console.log(`坐标位置为（${leftx},${topx}）`);
      this.leaveTimeout = null
    },
    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    //鼠标离开的事件。
    onLeaveTd(nowClean) {
      let that = this
      if (nowClean) {
        that.showUsrCard = false;
      } else {
        this.leaveTimeout = setTimeout(() => {
          that.showUsrCard = false;
        }, 300)
      }
    },

  },
};
</script>

<style>

.chat-msg-emoji {
  vertical-align: middle;
}

.chat-body {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

.msg-content {
  user-select: text;
}


.msg-item {
  margin-top: 15px;
}

.msg-avatar {
  margin-top: 4px;
}

.send-user {
  color: #000000;
}

.chat-header {
  width: 100%;
  height: 50px;
  line-height: 50px;
  background-color: #eeeded;
  text-align: center;
  position: relative;
}

.chat-header .chat-header-title {
  font-size: 18px;
  color: #000000;
}

.msg-list {
  height: 578px;
}

.custom-class .menu_item__available:hover,
.custom-class .menu_item_expand {
  background: #ffecf2 !important;
  color: #ff4050 !important;
}

@media only screen and (max-width: 767px) {
  .msg-list {
    height: 540px;
  }
}
</style>
