<template>
  <el-card class="box-card" v-if="hotSearchs.length > 0" >
    <div class="hotSearch text item" @mouseenter="onEnterTd" @mouseleave="onLeaveTd">
      <div class="hotSearchList" v-for="(item, index) in hotSearchs"  :key="item.uid" >
        <span @click="choseItem(item)" class="pointer hotSearchListBox">
          <span class="searchTitle">
            <div class="HotItem-index">
              <div
                class="HotItem-rank" :class="index<3 ?'HotItem-hot':''" >
                {{index + 1}}
              </div>
            </div>
          </span>
          <span class="content">{{item.content}}</span>
        </span>
      </div>
    </div>
  </el-card>
</template>

<script>
import { getHotSearchList } from "../../api/hotSearch";

export default {
  name: "HotSearch",
  data () {
    return {
      hotSearchs: [],
    }
  },
  created () {
    getHotSearchList().then(result => {
      if (result.code == this.$ECode.SUCCESS) {
        this.hotSearchs = result.data;
      }
    })
  },
  methods: {
    choseItem (item) {
      this.$emit('chooseitem', item.content)
    },
    closeHotSearch() {
      this.$emit('close', "")
    },
    // 鼠标移走
    onLeaveTd: function () {
      console.log("鼠标移走")
      this.$emit("leave", true)
    },
    onEnterTd: function () {
      console.log("鼠标移入")
      this.$emit("enter")
    },
  }
}
</script>

<style scoped>

/deep/ .el-card__body {
  padding: 5px;
}

.hotSearch {
  display: flex;
  flex-direction: column;
  max-height: 300px;
  overflow-y: auto;
}
.hotSearchListBox {
  display: flex;
  align-items: center;

}
.hotSearchList :hover {

  background: rgba(64,158,255,0.1);
}
.searchTitle {
  width: 25px;
  height: 25px;
  line-height: 25px;
}
.content {
  flex: 1;
  text-align: left;
  padding-left: 5px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  line-height: 25px;
}
.HotItem-index {
  text-align: center;
}
.HotItem-hot {
  color: #ff9607 !important;
}
.HotItem-rank {
  color: #999;
  font-weight: 600;
  font-size: 16px;
}
.text {
  font-size: 14px;
}
.item {
  padding: 0px 0;
}
.box-card {
  width: 200px;
}

</style>
