<template>
  <div style="text-align: center; border-top: 1px solid #f0f0f0; margin-top: 10px;">
    <!--        <div>-->
    <!--          <a v-for="(userInfo, index) in userPraiseList" :key="userInfo.uid" style="color: #0d3d86" target="_blank" :href="'/userCenter?userUid=' + userInfo.uid">-->
    <!--            <img v-if="index <= 10" :src="userInfo.photoUrl" class="like-user-avatar">-->
    <!--          </a>-->

    <!--          <a v-if="userPraiseList.length > 10" style="float: left; color: #0d3d86">-->
    <!--            ..-->
    <!--          </a>-->
    <!--          <span style="line-height: 24px; margin-left: 5px; color: #909399; font-size: 14px;">{{userPraiseList.length}} 人点赞</span>-->
    <!--        </div>-->

    <div style="margin-top: 10px;">
      <el-button circle @click="cancelUserPraise" type="primary" v-if="isPraise">
        <slot name="icon">
          <span class="iconfont">&#xe618;</span>
        </slot>
      </el-button>
      <el-button circle @click="praise" v-else>
        <slot name="icon">
          <span class="iconfont">&#xe615;</span>
        </slot>
      </el-button>
    </div>


    <div style="margin-top: 10px; color: #999">
      <p>
        <span v-if="userPraiseList.length > 0">
          {{ userPraiseList.length }}人已点赞
        </span>
        <span v-else>
          都看完了，点个赞吧~
        </span>
      <div class="praise-box">
        <a class="praise-img" v-for="(userInfo, index) in userPraiseList" :key="userInfo.uid"
           target="_blank" :href="'/userCenter?userUid=' + userInfo.uid">
          <img :src="userInfo.photoUrl">
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import {addPraise, cancelPraise, getPraiseUserList} from "../../api/praise";
import {mapMutations} from "vuex";

export default {
  name: "BottomPraise",
  props: ['resourceUid', 'resourceType'],
  data() {
    return {
      userPraiseList: [],
      isPraise: false,
      random: 0,
    };
  },
  created() {
    this.getPraiseUserListMethod()
  },
  watch: {
    'resourceUid': function (newFlag, oldFlag) {
      this.getPraiseUserListMethod()
    },
    // 判断是否点赞成功
    '$store.state.app.praiseMessage': function (newFlag, oldFlag) {
      if (this.random == newFlag) {
        return
      }
      this.getPraiseUserListMethod()
    },
  },
  mounted() {

  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage', 'setPraiseMessage']),
    praise() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      addPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = true
          this.random = Math.random()
          this.getPraiseUserListMethod()
          this.setPraiseMessage(this.random)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    cancelUserPraise() {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      cancelPraise(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.isPraise = false
          this.random = Math.random()
          this.getPraiseUserListMethod()
          this.setPraiseMessage(this.random)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    getPraiseUserListMethod() {
      console.log("开始获取点赞列表")
      if (!this.resourceUid || !this.resourceType) {
        console.log("必须参数为空")
        return
      }
      let params = {}
      params.resourceUid = this.resourceUid
      params.resourceType = this.resourceType
      this.isPraise = false
      let userUid = this.$store.state.user.userInfo.uid
      console.log("当前用户", userUid)
      getPraiseUserList(params).then(res => {
        if (res.code == this.$ECode.SUCCESS) {
          console.log("获取点赞列表", res.data)
          this.userPraiseList = res.data
          // 判断一下点赞中是否有自己
          let userPraiseList = res.data
          for (let a = 0; a < userPraiseList.length; a++) {
            if (userPraiseList[a].uid === userUid) {
              this.isPraise = true
              break
            }
          }
        }
      })
    },
  },
};
</script>

<style scoped>
/*.like-user-avatar {*/
/*  height: 20px;*/
/*  width: 20px;*/
/*  border-radius: 40px;*/
/*  float: left;*/
/*  margin-left: -5px;*/
/*  box-shadow: 5px 0 10px #c9c9c9;*/
/*  border: 2px solid white;*/
/*  cursor: pointer;*/
/*  transition: all 0.6s;*/
/*}*/

.praise-box {
  margin-top: 10px;
}

.praise-img img {
  display: inline-block;
  width: 25px;
  height: 25px;
  margin-right: 7.5px;
  border-radius: 50%;
  cursor: pointer;
  text-align: center;
  transition: all 0.6s;
}
.praise-img img:hover {
  transform: scale(1.5); /*transform:变形属性，scale：缩放1.1倍 */
}
</style>
