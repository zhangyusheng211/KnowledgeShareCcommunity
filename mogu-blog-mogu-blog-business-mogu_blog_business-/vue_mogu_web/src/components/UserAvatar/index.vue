<template>
    <span v-if="user" style="cursor: pointer; display: flex; align-items: center; justify-content: center;"
          @click="goUserCenter(user)">
      <div @mouseover="onEnterTd" @mouseleave="onLeaveTd(false)">

        <li style="padding-right: 6px; text-align: center; cursor: pointer">
          <span :id="getTabIndex(user.uid)" :class="user.userTag > 0 ?'vip-avatar':''">
            <el-avatar v-if="user.photoUrl" :class="user.userTag > 0 ?'vip-color':''" @click.native="goUserCenter(user)"
                       fit="fill"
                       size="medium" :src="user.photoUrl"></el-avatar>
            <el-avatar v-else fit="fill" size="small" src="https://empty">
              <img :src="defaultAvatar"/>
            </el-avatar>
            <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                  v-if="user.userTag > 0">v</span>
          </span>
        </li>

        <li class="author" style="margin-top: 9px;">
          <span v-if="user" class="pointer" :class="'lv'+ user.userLevel"
                @click="goUserCenter(user)">{{ user.nickName }}</span>
        </li>
      </div>

      <UserCard style="display: flex;" :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="coorDinatex" :coorDinatey="coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>
    </span>
</template>

<script>
import UserCard from "../UserCard";

export default {
  name: "index",
  props: ['user', 'bizType', 'bizIndex'],
  data() {
    return {
      BLOG_WEB_URL: process.env.VUE_MOGU_WEB,
      usrCard: {},
      showUsrCard: false,
      coorDinatex: "",
      coorDinatey: "",
      defaultAvatar: process.env.defaultAvatar, // 默认头像
    }
  },
  components: {
    UserCard
  },
  methods: {
    goUserCenter(user) {
      console.log("跳转用户详情页")
      window.open(this.BLOG_WEB_URL + '/userCenter?userUid=' + user.uid, '_blank');
    },
    //鼠标进入的事件。
    // onEnterTd(item, index) {
    //   console.log("获取元素ID", item, index)
    //   this.showUsrCard = true;
    //   this.usrCard = item;
    //   let tagElement = document.getElementById("UserUid:" + index + this.bizType + this.bizIndex);
    //   console.log("获取目标元素", tagElement)
    //   let leftx = tagElement.getBoundingClientRect().left;
    //   let topx = tagElement.getBoundingClientRect().top;
    //   this.coorDinatex = leftx;
    //   this.coorDinatey = topx;
    //   console.log(`坐标位置为（${leftx},${topx}）`);
    //   this.leaveTimeout = null
    // },
    onEnterTd(event) {
      this.showUsrCard = true;
      this.usrCard = this.user;
      const targetElement = event.target;
      console.log("获取目标元素", targetElement)
      let leftX = targetElement.getBoundingClientRect().left;
      let topX = targetElement.getBoundingClientRect().top;
      this.coorDinatex = leftX;
      this.coorDinatey = topX;
      console.log(`坐标位置为（${leftX},${topX}）`);
      this.leaveTimeout = null
    },
    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    getTabIndex: function (index) {
      return "UserUid:" + index
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
  }
}
</script>

<style scoped>

</style>
