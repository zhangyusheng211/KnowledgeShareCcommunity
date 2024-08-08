<template>
  <el-dialog
    title="选择标题图"
    :visible.sync="centerDialogVisible"
    :width="dialogSize"
    @before-close="beforeClose"
    center>

    <el-row style="margin-bottom: 10px; text-align: right">
      <span style="font-size: 12px;">大小不超过10M</span>
      <el-button type="primary" size="mini" @click="uploadPhoto">本地上传图片</el-button>
    </el-row>


    <el-row>
      <el-col :xl="6" :xs="12" :sm="8" v-for="file in recentFileList" :key="file.uid">
        <div class="choosePhoto">
          <el-image class="photoImg" :src="file.pictureUrl" @click="chooseCallback(file)"></el-image>
        </div>
      </el-col>
    </el-row>

    <el-pagination
      hide-on-single-page
      style="text-align: center"
      layout="prev, pager, next"
      @current-change="currentChange"
      :total="50">
    </el-pagination>

  </el-dialog>

</template>

<script>
import {getUserRecentlyUploadFile} from "../../api/file";
import {done} from "nightwatch/lib/core/queue";

export default {
  name: "index",
  data() {
    return {
      centerDialogVisible: true,
      currentPage: 1,
      pageSize: 12,
      total: 0,
      recentFileList: [],
      dialogSize: "40%",
    }
  },
  created() {
    this.listUserRecentlyUploadFile()
  },
  mounted() {
    this.resizeWin()
  },
  components: {

  },
  watch: {

  },
  methods: {
    listUserRecentlyUploadFile() {
      let params = {}
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize
      getUserRecentlyUploadFile(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.recentFileList = response.data.records
          this.total = response.data.total
        }
        console.log(response)
      })
    },
    currentChange(val) {
      this.currentPage = val
      this.listUserRecentlyUploadFile()
    },
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.dialogSize = "40%"
      } else if (centerWidth > 1000) {
        this.dialogSize = "50%"
      } else if (centerWidth > 600) {
        this.dialogSize = "60%"
      } else {
        this.dialogSize = "95%"
      }
    },
    uploadPhoto() {
      this.$emit("uploadCallback", "")
    },
    chooseCallback(file) {
      this.$emit("chooseCallback", file)
    },
    beforeClose() {
      this.$emit("close", "")
      done()
    },
  },
}
</script>

<style scoped>
.choosePhoto {
  width: 150px;
  height: 150px;
  border: 1px dashed #8F8F8F;
  margin-bottom: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.choosePhoto:hover {
  border: 1px dashed #66c8e8;
}


.choosePhoto .photoImg {
  cursor: pointer;
  transition: all 0.6s;
}
.choosePhoto .photoImg:hover {
  transform: scale(1.2);
}
</style>
