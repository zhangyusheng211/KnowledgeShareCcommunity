<template>
  <div class="sidebarDiv cloud"  style="border-radius: 3px; ">
      <h2 class="hometitle">
        <el-tooltip placement="top" effect="light">
          <div slot="content">通过完成签到、任务、发布文章、问答、<br/>评论、动态、可获取经验值提升排名</div>
          <span>用户排行</span>
        </el-tooltip>

        <el-button style="margin-left: 10px" type="text" @click="showRanking">查看更多</el-button>
        <el-tooltip class="item" effect="light" content="每隔10分钟自动刷新" placement="left-end">
          <i class="el-icon-refresh" @click="userTopN(true)" style="cursor: pointer;float: right"></i>
        </el-tooltip>
      </h2>

    <ul class="userInfo" v-for="(item, index) in userList">
      <li style=" padding-right: 6px" >
        <el-row>
          <el-col :span="5" style="text-align: center" >
            <span :id="getTabIndex(index)"  :class="item.userTag > 0 ?'vip-avatar':''" @mouseover="onEnterTd(item, index)" @mouseleave="onLeaveTd(false)">
              <el-avatar :class="item.userTag > 0 ?'vip-color':''" @click.native="getUserCenter(item.uid, 1)" @error="errorHandler"  style="cursor: pointer;" shape="circle"   :size="40" fit="fill"  :src="item.photoUrl">
                <img :src="defaultAvatar"/>
              </el-avatar>
              <span style="font-size: 10px; right: -4px; bottom: -3px;" class="vip-text pointer" v-if="item.userTag > 0">v</span>
            </span>
          </el-col>

          <el-col :span="13">
            <div>
              <span @click="getUserCenter(item.uid, 1)" class="blackFont" style="cursor: pointer;font-size: 13px;font-weight: 600; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                {{item.nickName}}
              </span>

              <span class="iconfont" v-if="item.gender=='1'" style="margin-left: 1px; color: dodgerblue;  font-size: 16px;">&#xe641;</span>
              <span class="iconfont" v-if="item.gender=='2'" style="margin-left: 1px; color: palevioletred;  font-size: 16px;">&#xe643;</span>

              <span class="lv" :class="'lv'+ item.userLevel">
                    Lv{{item.userLevel}}
              </span>
            </div>

            <div style="font-size: 12px;color: #72777b; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; ">
              <span v-if="item.blogPublishCount > 0">
                <span class="pointer" style="color: #00A7EB" @click="getUserCenter(item.uid, 1)">
                  <countTo :startVal='0' :endVal='item.blogPublishCount' :duration='3000'></countTo></span> 文章
              </span>
<!--              <span class="pointer" v-if="item.questionPublishCount > 0">-->
<!--                <span style="color: #00A7EB" @click="getUserCenter(item.uid, 3)">-->
<!--                  <countTo :startVal='0' :endVal='item.questionPublishCount' :duration='3000'></countTo></span> 问答-->
<!--              </span>-->
              <span class="pointer" v-if="item.momentPublishCount > 0">
                <span style="color: #00A7EB" @click="goToMoment(item.uid)">
                  <countTo :startVal='0' :endVal='item.momentPublishCount' :duration='3000'></countTo></span> 动态
              </span>
              <span class="pointer" v-if="item.commentPublishCount > 0">
                <span style="color: #00A7EB; cursor: pointer;" @click="getUserCenter(item.uid, 1)">
                  <countTo :startVal='0' :endVal='item.commentPublishCount' :duration='3000'></countTo></span>评论
              </span>
            </div>
          </el-col>

          <el-col :span="6" style="line-height: 50px; text-align: center">

            <i class="el-icon-trophy">
                <span style="font-weight: 550;"> &nbsp;
                  <countTo :startVal='0' :endVal='item.expValue' :duration='3000'></countTo>
                </span>
            </i>

          </el-col>
        </el-row>
      </li>
    </ul>
    <el-pagination
      style="width: 100%; text-align: center"
      small
      layout="prev, pager, next"
      :total="total"
      @current-change="handleCurrentChange"
      :current-page.sync="currentPage"
      :page-size="pageSize"
    >
    </el-pagination>

    <UserCard :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>

  </div>
</template>

<script>
import countTo from 'vue-count-to';
import {getUserTopN} from "@/api/index";
import UserCard from "../UserCard";
export default {
  name: "UserTopN",
  data() {
    return {
      userList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      usrCard: "",
      coorDinatex: "",
      coorDinatey: "",
      showUsrCard: false,
      currentId:1,
      defaultAvatar: process.env.defaultAvatar, // 默认头像
    };
  },
  created() {
    this.userTopN(false)
  },
  components: { countTo,UserCard },
  methods: {
    getTabIndex: function (index) {
      return "id" + index
    },
    //鼠标进入的事件。
    onEnterTd (item, index) {
      this.showUsrCard = true;
      this.usrCard = item;
      let tagElement = document.getElementById("id"+ index);
      let leftx = tagElement.getBoundingClientRect().left;
      let topx = tagElement.getBoundingClientRect().top;
      this.coorDinatex = leftx;
      this.coorDinatey = topx;
      clearTimeout(this.leaveTimeout)
    },
    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    //鼠标离开的事件。
    onLeaveTd (nowClean) {
      let that = this
      if (nowClean) {
        that.showUsrCard = false;
      } else {
        this.leaveTimeout = setTimeout(() => {
          that.showUsrCard = false;
        }, 300)
      }
    },
    showRanking: function () {
      this.$router.push("/rank");
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.userTopN();
    },
    errorHandler: function () {
      return true
    },
    userTopN: function(refresh) {
      let params = {}
      params.refresh = refresh;
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize
      getUserTopN(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          if(refresh) {
            this.$message.success("更新成功")
          }
          this.userList = response.data.records
          this.pageSize = response.data.size
          this.total = response.data.total
        }
      });
    },
    goToMoment: function (userUid) {
      let routeData = this.$router.resolve({
        path: "/moment",
        query: {userUid: userUid}
      });
      window.open(routeData.href, '_blank');
    },
    getUserCenter: function (userUid, type) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: userUid, type: type}
      });
      window.open(routeData.href, '_blank');
    },
  }
};
</script>

<style scoped>
.lv1 {
  color: #a1a1a1;
  border-color: #a1a1a1;
}
.lv2 {
  color: #418ac3;
  border-color: #418ac3;
}
.lv3 {
  color: #42c1a2;
  border-color: #42c1a2;
}
.lv4 {
  color:  #e8ca2d;
  border-color: #e8ca2d;
}
.lv5 {
  color: #fc7123;
  border-color: #fc7123;
}
.lv6 {
  color: #f34c54;
  border-color: #f34c54;
}

.userInfo {
  transition: transform .2s, box-shadow .2s;
}
.userInfo:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px 0 rgba(95,101,105,.15);
}
</style>
