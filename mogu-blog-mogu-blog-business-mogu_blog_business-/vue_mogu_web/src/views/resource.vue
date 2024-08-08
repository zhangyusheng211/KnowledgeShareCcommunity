<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
<!--        <span>好咖啡要和朋友一起品尝，好“资料”也要和同样喜欢它的人一起分享。</span>-->
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">学习资源</a>
      </h1>

      <div class="header-module" style="overflow: hidden;">
        <div style="width: 100%; text-align: center;">
          <p style="color: white; line-height: 100px; padding-top: 90px;"></p><h1 style="color: white; font-size: 40px;">一键检索全站资源</h1><p></p>
          <input v-model="keyword" v-on:keyup.enter="search" class="resource-search"  style="z-index: 10;" placeholder="输入要搜索的资源名后 回车"></input>
        </div>


        <img src="../../static/images/resourceBackground.png"   style="position: absolute; width: 500px; left: -20px; bottom: -100px; opacity: 0.3">
        <div class="search-hot-resource">
          <a :href="'/list?active=5&keyword=' + item.dictLabel + '&model=' + searchModel" target="_blank" v-for="item in resourceSortDictList" :key="item.uid"><button style="cursor: pointer" >{{item.dictLabel}}</button></a>
        </div>

      </div>

      <div class="resource-module" style="padding: 0px; width: 1200px; height: auto; background-color: transparent;
         box-shadow: none">
        <resourceRank title="实时上传榜" orderByDesc="createTime" background-color="#e76666" width="30%"></resourceRank>
        <resourceRank title="随机资源榜" orderByDesc="random" background-color="#5f64c9" width="30%"></resourceRank>
        <resourceRank title="精品资源榜" orderByDesc="sort" background-color="#348f5c" width="30%"></resourceRank>

        <div style="clear: both"></div>
      </div>

      <book></book>
      <networkDisk></networkDisk>


    </div>
  </div>
</template>

<script>

import book from "../components/Resource/book"
import networkDisk from "../components/Resource/networkDisk"
import resourceRank from "../components/Resource/resourceRank"
import {getListByDictTypeList} from "@/api/sysDictData";
import {getSearchModel} from "@/api/search";

export default {
  name: "share",
  data() {
    return {
      studyVideoData: [],
      currentPage: 1,
      pageSize: 8,
      total: 0, //总数量
      keyword: "", // 关键字
      resourceSortDictList: [], // 资源类型
      searchModel: 0, // 搜索模式
    };
  },
  components: {
    //注册组件
    book,
    networkDisk,
    resourceRank,
  },
  created() {
    this.getBlogSearchModel()
    this.getDictList()
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_resource_sort']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.resourceSortDictList = dictMap.sys_resource_sort.list
        }
      });
    },
    getBlogSearchModel: function () {
      getSearchModel().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.searchModel = response.data
        }
      })
    },
    search() {
      if (this.keyword == "" || this.keyword.trim() == "") {
        this.$notify.error({
          title: '错误',
          message: "关键字不能为空",
          type: 'error',
          offset: 100
        });
        return;
      }
      // 跳转到列表页
      this.$router.push({path: "/list", query: {keyword: this.keyword, model: this.searchModel, active: 5}});
    },
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>

.header-module {
  position: relative;
  margin-top: 30px;
  width: 1190px;
  height: 400px;
  /* background-color: #0f2640; */
  border-radius: 5px;
  box-shadow: 0px 0px 30px white;
  background: #141e30;
  background: -webkit-linear-gradient(to right, rgb(20, 30, 48), rgb(36, 59, 85));
  background: linear-gradient(to right, rgb(20, 30, 48), rgb(36, 59, 85));
}

.resource-search {
  position: relative;
  margin-top: 60px;
  width: 80%;
  height: 50px;
  line-height: 50px;
  border-radius: 3px;
  border: 0px;
  text-indent: 20px;
  font-size: 18px;
  font-weight: 700;
}

.search-hot-resource {
  text-align: center;
  margin-top: 50px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

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

.resource-title-book {
  background: linear-gradient(to right, #3490de, white);
}
.resource-module {
  position: relative;
  width: 1150px;
  background-color: white;
  min-height: 200px;
  height: auto;
  margin-top: 50px;
  border-radius: 5px;
  padding: 100px 20px 30px 20px;
  box-shadow: 0 4px 30px 0 rgb(238 242 245 / 90%);
}
.dianzishu {
  position: relative;
  width: 150px;
  height: 240px;
  padding: 8px 10px 5px 10px;
  float: left;
  overflow: hidden;
  margin: 0px 20px 20px 0px;
  text-align: center;
}
.dianzishu-img {
  position: relative;
  height: 150px;
  border-radius: 3px;
  margin-left: 0px;
}

.line2ppp {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dianzishu-name {
  position: relative;
  margin-top: 10px;
  line-height: 22px;
  font-size: 15px;
  text-align: center;
}
.line1ppp {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  word-break: break-all;
  text-align: center;
}
.dianzishu-date {
  position: relative;
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}

.layui-badge {
  height: 18px;
  line-height: 18px;
}
.layui-badge, .layui-badge-dot, .layui-badge-rim {
  position: relative;
  display: inline-block;
  padding: 0 6px;
  font-size: 12px;
  text-align: center;
  background-color: #FF5722;
  color: #fff;
  border-radius: 2px;
}

.resource-module-main:hover {
  cursor: pointer;
  opacity: 1;
}
.resource-title-book {
  background: linear-gradient(to right, #3490de, white);
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

.box-resource:hover {
  cursor: pointer;
  box-shadow: 0px 0px 5px #e0dddd;
}

.special-resource-module {
  position: relative;
  width: 30%;
  height: 420px;
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

.boutique-resource-item-extName {
  margin-top: 10px;
  text-align: left;
  font-size: 12px;
  color: #464647;
}

.soft-resource {
  position: relative;
  float: left;
  width: 190px;
  height: 170px;
  padding: 10px;
  border: 1px solid #e2e0e0;
  margin: 0px 15px 15px 0px;
  text-align: center;
}


</style>
