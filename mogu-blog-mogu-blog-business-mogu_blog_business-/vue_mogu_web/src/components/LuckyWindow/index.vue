<template>
  <div v-if="isShow">
    <div class="luckyWindowBox">
      <div
        style="color: #ffffff; background-color: rgba(0, 0, 0, 0.5); font-size: 16px; font-weight: 550;  text-align: center; margin-top: -30px; border-top-left-radius: 15px; border-top-right-radius: 15px;">
        <span>
          恭喜抽中奖品
        </span>
        <div class="t2" @click="closeBox()">
          X
        </div>
      </div>
      <div class="medalDiv">
        <el-image class="medalImage" :src="awardProduct.photoUrl"></el-image>
      </div>
      <div
        style="color: #ffffff; background-color: rgba(0, 0, 0, 0.5); font-size: 16px; font-weight: 500; text-align: center; margin-top: 0;  border-bottom-left-radius: 15px; border-bottom-right-radius: 15px;">
        <span>{{awardProduct.title}}</span>
        <br />
      </div>
    </div>
    <div class="mask"></div>
  </div>
</template>

<script>

export default {
  name: "share",
  props: ['awardProduct', 'showWindow'],
  data() {
    return {
      isShow: this.showWindow,
    };
  },
  components: {

  },
  created() {
    console.log("获取抽奖项", this.awardProduct)
  },
  mounted() {

  },
  watch: {
    // 判断用户信息
    '$store.state.user.isLogin': function (newFlag, oldFlag) {

    },
    'awardProduct': function (newFlag, oldFlag) {
      console.log("抽奖项", this.awardProduct)
      let awardProduct = this.awardProduct
      if (awardProduct.openWindow) {
        setTimeout(() => {
          this.isShow = true
        }, 5500)
      }
    },
  },
  methods: {
    closeBox: function () {
      this.isShow = false
    },

  }
};
</script>


<style scoped>
.luckyWindowBox {
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

.luckyWindowBox .t2 {
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
