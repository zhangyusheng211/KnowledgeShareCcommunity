<template>
  <div v-if="isLogin && isShow">
    <div class="box loginBox">
      <div
        style="color: #ffffff; background-color: rgba(0, 0, 0, 0.5); font-size: 16px; font-weight: 550;  text-align: center; margin-top: -30px; border-top-left-radius: 15px; border-top-right-radius: 15px;">
        <span>
          恭喜获得一枚勋章
        </span>

        <div class="t2" @click="closeBox()">
          X
        </div>
      </div>
      <div class="medalDiv">
        <el-image class="medalImage" :src="medal.fileUrl"></el-image>
      </div>
      <div
        style="color: #ffffff; background-color: rgba(0, 0, 0, 0.5); font-size: 16px; font-weight: 500; text-align: center; margin-top: 0px;  border-bottom-left-radius: 15px; border-bottom-right-radius: 15px;">
        <span>{{medal.name}}</span>
        <br />
        <span style="font-size: 14px;">{{medal.summary}}</span>
      </div>
    </div>
    <div class="mask"></div>
  </div>
</template>

<script>
import {getMedalByRecent} from "../../api/medal";

export default {
  name: "share",
  data() {
    return {
      medal: {},
      isLogin: false,
      isShow: false,
      medalUid: null,
    };
  },
  components: {

  },
  created() {
    this.medalUid = this.$route.query.medalUid;
    this.getUserMedal()
  },
  mounted() {
    this.getUserMedal()
  },
  watch: {
    // 判断用户信息
    '$store.state.user.isLogin': function (newFlag, oldFlag) {
      this.getUserMedal()
    },
  },
  methods: {
    closeBox: function () {
      this.isShow = false
      // 关闭前，在判断下，有没有下一个勋章
      let params = {}
      getMedalByRecent(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let medal = response.data
          if (this.medal) {
            this.medal = medal
            this.isShow = true
          } else {
            // 没有了，直接关闭
            this.$emit("closeBox", "");
          }
        }
      })
    },
    getUserMedal() {
      this.isLogin = this.$store.state.user.isLogin
      if (!this.isLogin) {
        return
      }
      let params = {}
      if (this.medalUid) {
        params.uid = this.medalUid
      }
      getMedalByRecent(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.medal = response.data
          if (this.medal) {
            this.isShow = true
          }
        }
      })
    },
  }
};
</script>


<style scoped>
.box {
  -webkit-user-drag: none;
  width: 300px;
  height: 300px;
  position: fixed;
  margin: auto;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 101; /* 要比遮罩层大 */
}

.box .t2 {
  font-size: 16px;
  position: absolute;
  right: 15px;
  top: -30px;
  cursor: pointer;
}

.medalDiv {
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0.9;
  filter: alpha(opacity=90);
}

.medalImage {
  -webkit-user-drag: none;
  width: 100%;
  height: 100%;
  animation: mymove 5s infinite;
  -webkit-animation: mymove 5s infinite; /* Safari and Chrome */
}

@keyframes mymove {
  50% {
    transform: rotateY(50deg);
  }
}

@-webkit-keyframes mymove { /* Safari and Chrome */
  50% {
    transform: rotateY(50deg);
  }
}

/* 遮罩层 */
.mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}
</style>
