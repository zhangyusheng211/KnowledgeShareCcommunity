<template>

  <div class="resource-module">
    <a :href="'/list?keyword=软件安装包'" target="_blank">
      <div class="resource-module-main resource-title-soft" style="background-color: #79a8a9">软件安装包</div></a>

    <div id="cloud-resource-home">
      <a :href="'/resource/' + item.uid" target="_blank" v-for="item in dataList" :key="item.uid">
        <div class="soft-resource box-resource">
          <div class="soft-img-box">
            <img  :src="item.photoUrl ? item.photoUrl:getFileIcon(item.resourceSort)">
          </div>
          <div class="soft-name line1ppp">{{item.name}} </div>
          <span>
            <el-tag effect="dark" size="mini" v-if="item.chargeType == 1" type="primary">免费</el-tag>
            <el-tag effect="dark" size="mini" v-if="item.chargeType == 2" type="danger">付费</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 3" type="success">限时优惠</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 4" type="warning">会员免费</el-tag>
          </span>
          <p class="soft-time line1ppp">上传于 {{timeAgo(item.createTime)}}</p>
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
      pageSize: 10,
      currentPage: 1,
      total: 0,
      fileIconList: [1, 2, 3, 4],
      //  文件图片Map映射
      fileIconMap: {
        1: require('@/assets/file/file_pdf.png'),
        2: require('@/assets/file/file_word.png'),
        3: require('@/assets/file/file_zip.png'),
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
      params.resourceSort = 3
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
.soft-time {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.soft-img-box {
  margin-top: 10px;
  height: 70px;
  width: 100%;
}
.soft-desc {
  line-height: 20px;
  color: #666;
  font-size: 13px;
}
.soft-img-box img {

  height: 70px;
  position: absolute;
  top: 25%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
