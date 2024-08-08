<template>
  <!--加载更多-->
  <div class="isEnd">
    <div class="loadContent" @click="loadMore" v-if="!isEnd&&!loading" >查看更多</div>
    <div class="lds-css ng-scope" v-if="!isEnd&&loading">
      <div style="width:100%;height:100%" class="lds-facebook">
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <span v-if="isEnd && length === 0">
        <div class="noComment" v-if="length === 0">还没有评论，快来抢沙发吧！</div>
    </span>
    <span v-if="isEnd && length > 0">我也是有底线的~</span>
  </div>
</template>

<script>
export default {
  name: "LoadMore",
  props: {
    // 是否结束
    isEnd: {
      type: Boolean,
      default: false,
    },
    // 是否加载中
    loading: {
      type: Boolean,
      default: false,
    },
    // 当前长度
    length: {
      type: Number,
      default: 0,
    },
  },
  methods: {
    // 加载更多
    loadMore() {
      this.$emit("loadMore", "");
    }
  }
}
</script>

<style scoped>
.isEnd {
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
  cursor: pointer;
}
.ng-scope {
  margin: 0 auto;
  width: 18%;
  height: 10%;
}

.loadContent {
  border-radius: 25px;
  width: 120px;
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  margin: 0 auto;
  color: aliceblue;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.8);
}

</style>
