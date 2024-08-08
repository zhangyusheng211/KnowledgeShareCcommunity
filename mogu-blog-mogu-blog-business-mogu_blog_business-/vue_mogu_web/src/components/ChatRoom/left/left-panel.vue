<template>
  <div class="leftPanel" style="overflow-y: scroll">
    <div v-show="!showSearchPanel" class="room-container">
      <div class="new-room">
        <el-input v-model="keyword" clearable class="searchUser"
                  style="background: #26292E; border: solid 1px #3a3a3a;" placeholder="搜用户"></el-input>
      </div>
      <ul v-if="keyword" class="user-list">
        <li v-for="(item, key) in userList" :key="key" class="user-item" @click="openRoom(item)">
          <div class="room-user-avatar">
                  <span :class="item.userTag > 0 ?'vip-avatar':''">
                    <el-avatar :class="item.userTag > 0 ?'vip-color':''" v-if="item.photoUrl" fit="fill" size="medium"
                               :src="item.photoUrl"></el-avatar>
                    <el-avatar v-else fit="fill" size="medium" :src="defaultAvatar">
                      <img :src="defaultAvatar"/>
                    </el-avatar>
                    <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                          v-if="item.userTag > 0">v</span>
                  </span>

          </div>
          <span>{{ item.nickName }}</span>
        </li>
      </ul>
      <ul v-else class="room-list">
        <li class="room-item" v-for="(item, index) in roomList" @click="selectRoom($event, item)"
            @mouseover="iconShow(item, true)" @mouseout="iconShow(item, false)" :id="`id-${item.uid}`">
          <i
            :id="item.uid"
            style="position: absolute; left: 1px; top: 1px; font-size: 18px"
            class="el-icon-error inputClass"
            @click="delRoom(item)"
          ></i>
          <div class="room-avatar">
            <img :src="item.avatar ? item.avatar:defaultAvatar" onerror="onerror=null;src=defaultAvatar"
                 alt="item.nickName"/>
          </div>
          <div class="room-info">
            <p class="title">{{ item.title }}</p>
            <p class="subtitle" v-html="item.subtitle">{{ item.subtitle }}</p>
          </div>
          <div class="room-new-msg-time" v-if="item.time">
            <span>{{ timeAgo(item.time) }}</span>
          </div>
          <el-badge v-if="item.unread && item.unread > 0" class="room-unread-tips item" :value="item.unread"
                    :max="99"></el-badge>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
import {deleteRoom, newRoom, roomList} from "../../../api/chat";
import {getUserByUid, getUserList} from "../../../api/about";
import {timeAgo} from "../../../utils/webUtils";

export default {
  name: 'left-panel',
  props: ['userUid'],
  data() {
    return {
      showSearchPanel: false,
      keyword: "",
      page: 1,
      pageSize: 20,
      userList: [],
      roomList: null,
      currentRoom: {},
      defaultAvatar: process.env.defaultAvatar
    }
  },
  watch: {
    keyword(newVal) {
      let that = this
      clearTimeout(that.timer);
      this.timer = setTimeout(() => {
        // 在这里发送请求获取内容，并将结果存储到this.result中
        console.log("请求变化", newVal)
        that.querySearchAsync(newVal)
      }, 500);
    }
  },
  created() {
    // 判断是否传递了 userUid
    console.log("userUid", this.userUid)

    if (this.userUid) {
      // 传递了用户uid，需要去查询对应的用户
      let params = new URLSearchParams()
      params.append("userUid", this.userUid)
      getUserByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let userInfo = response.data
          this.openRoom(userInfo)
          this.queryRoomList(null, this.userUid, null);
        } else {
          this.$message.error(response.message)
        }
      })
    } else {
      this.queryRoomList("10001", null, null);
    }
  },
  methods: {
    mediaRoom(messageVo) {
      let _this = this;
      // 接收到消息的聊天框
      let roomInfo = _this.roomList.filter(item => item.sessionId == messageVo.roomId)[0];
      let roomItem = document.querySelector("li.active");
      if (roomItem) {
        roomItem.classList.remove("active");
      }
      let target = document.querySelector("#id-" + roomInfo.uid);
      target.classList.add("active");
      _this.currentRoom = roomInfo;
      _this.currentRoom.unread = 0;
      _this.$emit('selectRoom', roomInfo);
    },
    // 根据房间选择对应的房间
    selectRoomByRoomInfo(roomInfo) {
      let _this = this;
      let roomItem = document.querySelector("li.active");
      if (roomItem) {
        roomItem.classList.remove("active");
      }
      let target = document.querySelector("#id-" + roomInfo.uid);
      target.classList.add("active");
      _this.currentRoom = roomInfo;
      _this.currentRoom.unread = 0;
      _this.$emit('selectRoom', roomInfo);
    },
    iconShow(item, isShow) {
      if (isShow) {
        document.getElementById(item.uid).classList.remove("inputClass")
      } else {
        document.getElementById(item.uid).classList.add("inputClass")
      }
    },
    delRoom(item) {
      this.$confirm("此操作将把该聊天窗口删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = item.uid;
          deleteRoom(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success(response.message)
              // 打开主要聊天
              this.queryRoomList("10001", null, null);
            } else {
              this.$commonUtil.message.error(response.message)
            }
          });
        }).catch(() => {
        this.$commonUtil.message.info("已取消删除")
      });
    },
    omMessage(messageVo) {
      let _this = this;
      // 接收到消息的聊天框
      let roomInfo = _this.roomList.filter(item => item.sessionId == messageVo.roomId)[0];
      console.log("侧边栏收到消息", roomInfo)
      console.log("当前的房间", _this.currentRoom)
      if (!roomInfo) {
        return
      }

      // 非当前聊天室的消息，需要加未读消息数量显示
      if (roomInfo.uid != _this.currentRoom.uid) {
        roomInfo.unread += 1;
      }

      roomInfo.time = messageVo.sendTime;
      if (messageVo.category == 20001) {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: ${messageVo.message}`;
      } else if (messageVo.category == 20002) {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: [语音]`;
      } else if (messageVo.category == 20003) {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: [图片]`;
      } else if (messageVo.category == 20004) {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: [视频通话]`;
      } else if (messageVo.category == 20005) {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: [语音通话]`;
      } else {
        roomInfo.subtitle = `${messageVo.fromUserNickName}: [媒体消息]`;
      }
    },
    selectRoom(event, roomInfo) {
      console.log("选择房间", event, roomInfo)
      let _this = this;
      let roomItem = document.querySelector("li.active");
      if (roomItem) {
        roomItem.classList.remove("active");
      }
      event.currentTarget.classList.add("active");
      _this.currentRoom = roomInfo;
      _this.currentRoom.unread = 0;

      // 需要校验是否发送消息
      console.log("选择房间", roomInfo)

      // 新建一个用户会话
      if (roomInfo.roomType == 10001) {
        let room = {
          roomType: 10001,
          receiveId: roomInfo.receiveId,
          avatar: roomInfo.photoUrl,
          title: roomInfo.title
        }
        newRoom(room).then(res => {
          if (res.code == "success") {
            this.queryRoomList(res.data.uid, null)
          }
        })
      }
      _this.$emit('selectRoom', roomInfo);
    },
    refreshRoomList(messageVo) {
      this.queryRoomList(null, messageVo.toUserId, messageVo);
    },
    // 撤回消息更新房间
    refreshRoomListWithdraw(messageVo) {
      console.log("撤回消息，更新", messageVo)
      let _this = this;
      let currentRoom = this.currentRoom
      roomList().then(response => {
        let roomList = response.data;
        _this.roomList = roomList;
        for (let i = 0; i < roomList.length; i++) {
          if (roomList[i].uid == messageVo.roomId || roomList[i].receiveId == messageVo.toUserId || roomList[i].receiveId == messageVo.fromUserId) {
            // 是否正处于当前房间
            if (currentRoom != null && (currentRoom.uid == messageVo.roomId || currentRoom.receiveId == messageVo.toUserId || currentRoom.receiveId == messageVo.fromUserId)) {
              console.log("正处于当前房间，刷新页面")

              setTimeout(() => {
                _this.mediaRoom(messageVo);
              }, 20);

              _this.$emit('selectRoom', roomList[i]);
            } else {
              console.log("未处于当前房间")
            }
          }
        }
      }, function (err) {
        console.error(err)
      });
    },
    openRoom(item) {
      console.log("开启房间", item)
      let _this = this;
      // 关闭搜索面板
      this.keyword = ""
      // 新建一个用户会话
      // let room = {
      //     roomType: 10001,
      //     receiveId: item.uid,
      //     avatar: item.photoUrl,
      //     title: item.nickName
      // }
      // newRoom(room).then(res => {
      //   console.log("返回的结果", res)
      //   if (res.code == "success") {
      //     this.queryRoomList(res.data.uid, null)
      //   } else {
      //     this.$message.error(res.message)
      //   }

      // })
      _this.$emit('newRoom', item);
    },
    addFriend() {
      this.userQuery.nickName = null;
      this.userList = [];
      this.showSearchPanel = true;
    },
    queryRoomList(roomUid, userUid, messageVo) {
      let _this = this;
      roomList().then(response => {
        console.log("查询到的聊天室", response)
        let roomList = response.data
        // 来源于新会话的刷新且新会话是由我方发起，自动选中该聊天室
        if (messageVo && messageVo.fromUserId == _this.$store.state.user.uid) {
          setTimeout(() => {
            _this.mediaRoom(messageVo);
          }, 20);
        }
        _this.roomList = roomList;
        let selectRoom = false
        for (let i = 0; i < roomList.length; i++) {
          if (roomList[i].uid == roomUid || roomList[i].receiveId == userUid) {
            _this.currentRoom = roomList[i];
            _this.currentRoom.unread = 0;
            console.log("选择房间", roomList[i])
            setTimeout(() => {
              _this.selectRoomByRoomInfo(roomList[i])
            }, 20);

            _this.$emit('selectRoom', roomList[i]);
            selectRoom = true
            break
          }
        }
      }, function (err) {
        console.error(err)
      });
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    querySearchAsync(queryString, cb) {
      console.log("搜索内容", queryString)
      let params = {}
      params.currentPage = this.page;
      params.pageSize = this.pageSize;
      params.keyword = queryString
      getUserList(params).then(resp => {
        if (resp.code == this.$ECode.SUCCESS) {
          console.log("搜索内容", resp.data.records)
          this.userList = resp.data.records
        }
      })
    },
  }
}
</script>

<style scoped>

*::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 0px; /*高宽分别对应横竖滚动条的尺寸*/
  height: 0px;
}

/deep/ .el-input__inner {
  background: rgb(38, 41, 46);
  border: 1px solid #1C1B19;
}

.searchUser {
  width: 100%;
  font-size: 12px;
  color: #fff;
  height: 40px;
  line-height: 30px;
  border: solid 1px #3a3a3a;
  border-radius: 4px;
  outline: none;
  background-color: #26292E;
}

.room-item {
  position: relative;
}

.inputClass {
  display: none;
}

.inputClassActive {

}

.room-user-avatar {
  /*width: 30px;*/
  /*height: 30px;*/
  /*border-radius: 50%;*/
  /*overflow: hidden;*/
  float: left;
}

.room-user-avatar img {
  height: 100%;
  height: 100%;
}

.leftPanel {
  height: 860px;
}

@media only screen and (max-width: 767px) {
  .leftPanel {
    height: 823px;
  }
}
</style>
