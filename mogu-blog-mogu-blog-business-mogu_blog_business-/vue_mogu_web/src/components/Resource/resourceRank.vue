<template>
  <div class="special-resource-module" :style="{width: width, height: height}" >
    <div class="resource-module-main resource-title-new" style="top: 15px;" :style="{backgroundColor: backgroundColor}">{{ title }}</div>
    <div style="width: 100%;  margin-top: 50px; overflow: hidden" id="realTimeListModule">
      <a :href="'/resource/' + item.uid" target="_blank" v-for="item in dataList" :key="item.uid">
        <div class="resource-list-item">
          <p class="resource-list-item-name line1ppp">
            <img class="resource-list-item-icon" :src="getFileIcon(item.resourceSort)">
            {{item.name}}
            <el-tag effect="dark" size="mini" v-if="item.chargeType == 1" type="primary">免费</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 3" type="danger">限时优惠</el-tag>
            <el-tag effect="dark" size="mini" v-else-if="item.chargeType == 4" type="warning">会员免费</el-tag>
          </p>
          <p class="resource-list-item-extinfo">
            <span style="color: #0a5370;" v-if="item.user">
                <img
                  :src="item.user.photoUrl"
                  style="width: 15px; height: 15px; margin-right: 5px; border-radius: 20px;">
                {{item.user.nickName}}
            </span>
            <span style="margin-left: 5px;">{{timeAgo(item.createTime)}}</span>上传
            <span style="margin-left: 10px">
              <span>&nbsp;{{item.downloadCount}} 下载</span>
              <span>&nbsp;{{item.clickCount}} 预览</span>
            </span>
          </p>
        </div>
      </a>

    </div>
  </div>
</template>

<script>
import {getResourceList} from "../../api/resource";
import {timeAgo} from "../../utils/webUtils";
export default {
  name: "book",
  props: {
    orderByDesc: {
      type: String
    },
    title: {
      type: String
    },
    backgroundColor: {
      type: String
    },
    width: {
      type: String
    },
    height: {
      type: String,
      default: "420px"
    },
    pageSize: {
      type: Number,
      default: 6
    }
  },
  // props: ['orderByDesc', 'title', 'backgroundColor', 'width'],
  data() {
    return {
      dataList: [],
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
      params.orderByDescColumn = this.orderByDesc
      getResourceList(params).then(response => {
        console.log("返回参数", response)
        this.dataList = response.data.records
        this.total = response.data.total
      })
    },
    getFileIcon: function (resourceSort) {
      resourceSort = parseInt(resourceSort)
      console.log("判断是否存在", this.fileIconList.includes(resourceSort))
      if (!this.fileIconList.includes(resourceSort)) {
        return this.fileIconMap.unknown
      } else {
        return this.fileIconMap[resourceSort]
      }
    },
    handleCurrentChange: function (val) {
      this.currentPage = val;
      this.resourceList();
    },
    timeAgo(createTime) {
      return timeAgo(createTime)
    }
  }
}
</script>

<style>

.search-hot-resource button {
  position: relative;
  padding: 7px 20px;
  color: white;
  margin-right: 15px;
  border: 0px;
  border-radius: 3px;
  background-color: #1b4d86;
  float: left;
}

.line1ppp {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  word-break: break-all;
  text-align: center;
}


.resource-module-main:hover {
  cursor: pointer;
  opacity: 1;
}

.resource-module-main {
  position: absolute;
  top: 35px;
  left: 0px;
  width: 100px;
  padding: 8px 50px 8px 20px;
  border-radius: 0px 50px 50px 0px;
  text-align: left;
  color: white;
  opacity: 0.8;
}


.special-resource-module {
  position: relative;
  /*height: 420px;*/
  background: white;
  border-radius: 5px;
  float: left;
  margin: 0px 10px;
  padding: 10px;
  box-shadow: 0 4px 30px 0 rgb(238 242 245 / 90%);
}

.resource-list-item {
  position: relative;
  width: 353px;
  height: 50px;
  background-color: white;
  padding: 5px;
  border-radius: 5px;
}

.resource-list-item-icon {
  width: 20px;
  height: 20px;
  border-radius: 3px;
  margin-right: 5px;
  display: inline-block;
  vertical-align: middle;
}

.resource-list-item-extinfo {
  line-height: 20px;
  font-size: 12px;
  color: gray;
  text-align: left;
}

.resource-list-item-extinfo img{
  display: inline-block;
  vertical-align: middle;
}

.line1ppp {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  word-break: break-all;
}

.resource-list-item-name {
  font-size: 15px;
  text-align: left;
  line-height: 30px;
  overflow: hidden;
}

.resource-list-item:hover {
  cursor: pointer;
  background-color: #f0f4fb;
  color: #cb3406;
}
</style>
