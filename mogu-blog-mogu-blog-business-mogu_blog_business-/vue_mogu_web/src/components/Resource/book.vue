<template>
  <div class="resource-module">
    <a :href="'/list?keyword=电子书'"  target="_blank">
      <div class="resource-module-main resource-title-book" style="background-color: #79a8a9">电子书</div>
    </a>
    <div id="pdf-resource-home">
      <a :href="'/resource/' + item.uid" target="_blank" v-for="item in dataList" :key="item.uid">
        <div class="dianzishu box-resource">
          <el-image class="dianzishu-img" :src="item.photoUrl ? item.photoUrl:getFileIcon(item.resourceSort)" fit="cover" style="width: 110px; height: 150px"></el-image>
          <p class="dianzishu-name line2ppp">{{item.name}}</p>
          <p class="dianzishu-date line1ppp">上传于 {{timeAgo(item.createTime)}}</p>
          <span style="position: absolute; right: 30px; top: 130px;">
            <el-tag effect="dark" size="mini" v-if="item.chargeType == 1" type="primary">免费</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 3" type="success">限时优惠</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 4" type="warning">会员免费</el-tag>
          </span>

        </div>
      </a>

    </div>
    <div style="clear: both"></div>
    <div style="width: 100%; text-align: center" v-if="total > pageSize">
      <el-pagination
        :total="total"
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        small
        layout="prev, pager, next">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {timeAgo} from "../../utils/webUtils";
import {getResourceList} from "../../api/resource";

export default {
  name: "book",
  data() {
    return {
      dataList: [],
      pageSize: 12,
      currentPage: 1,
      total: 0,
      //  文件图片Map映射
      fileIconList: [1, 2, 3, 4],
      fileIconMap: {
        1: require('@/assets/file/file_pdf.png'),
        2: require('@/assets/file/file_word.png'),
        3: require('@/assets/file/file_exe.png'),
        4: require('@/assets/file/file_video.png'),
        unknown: require('@/assets/file/file_unknown.png'),
      }
    }
  },
  created() {
    this.resourceList()
  },
  methods: {
    resourceList() {
      let params = {}
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      params.resourceSort = 1
      getResourceList(params).then(response => {
        console.log("返回参数", response)
        this.dataList = response.data.records
        this.total = response.data.total
      })
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.resourceList();
    },
    timeAgo(createTime) {
      return timeAgo(createTime)
    },
    getFileIcon: function (resourceSort) {
      resourceSort = parseInt(resourceSort)
      if (!this.fileIconList.includes(resourceSort)) {
        return this.fileIconMap.unknown
      } else {
        return this.fileIconMap[resourceSort]
      }
    },
  }
}
</script>

<style scoped>

</style>
