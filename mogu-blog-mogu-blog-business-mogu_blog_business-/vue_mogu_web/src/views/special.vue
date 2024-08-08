<template>
  <div>

    <div class="pagebg sh"></div>
    <div class="container">
      <el-row type="flex" justify="space-between">
        <el-col :xs="0" :sm="5" :md="4" :lg="4" :xl="4" >
          <div >
            <Sticky :sticky-top="10" >
              <div v-for="subjectSort in sortList">
                <div @click="selectSortHandle(subjectSort)" class="subjectItemCard" :class="[subjectSort.name == selectSortName ? 'topicStyle' : '']" :body-style="{ padding: '0px' }">
                  <span>{{subjectSort.name}}</span>
                </div>
              </div>
            </Sticky>
          </div>
        </el-col>

        <el-col  :xs="24" :sm="18" :md="19" :lg="19" :xl="19">
          <div :style="{'background-image': 'url('+ selectSort.photoUrl +')'}" style=" height: 180px; color: white; text-align: center; border-radius: 5px; background-size:100% 100%;  background-color:rgba(0,0,0,.8);">
            <div style="padding-top: 45px;">
              <p style="font-size: 32px; font-weight: bold;">{{selectSort.name}}</p>
              <p style="font-size: 18px; margin-top: 10px;">{{selectSort.summary}}</p>
            </div>
          </div>

          <div  style="min-height: 773px; margin-top: 10px" >
            <el-card class="box-card" :body-style="{ padding: '5px' }">
              <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane name="0">
                  <span :class="activeName==0?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>推荐</span></span>
                </el-tab-pane>
                <el-tab-pane name="1">
                  <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>最新</span></span>
                </el-tab-pane>
                <el-tab-pane name="2">
                  <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>最热</span></span>
                </el-tab-pane>
              </el-tabs>

              <div
                class="blogs"
                style="padding-top: 1px; padding-bottom: 5px"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row  :gutter="20" style="min-height: 700px">
                  <el-col class="chooseCol" @click.native="goDetail(item.uid)"  style="margin: 10px; cursor: pointer;"  v-for="(item, index) in dataList" :key="item.uid" :xs="23" :sm="11">
                    <div class="detail" style="position: relative">
                      <router-link :to="'/subject/' + item.uid" style="line-height: 35px">
                        <div>{{item.subjectName}} </div>
                        <div style="position: absolute; top: 2px; right: 5px; font-size: 12px; font-weight: normal;">
                          <el-tag class="pointer"  size="mini" v-if="item.isSelection == '1'" effect="dark" type="warning">精选</el-tag>
                          <el-tag v-if="item.subjectSort" class="pointer" effect="plain" size="mini" :type="colorList[item.subjectSort.sort % colorList.length]">{{item.subjectSort.name}}</el-tag>

                          <el-tag :type="tagItem.listClass"  effect="plain" size="small" v-for="tagItem in visitAuthDictList" :key="tagItem.uid" v-if="parseInt(tagItem.dictValue) === item.visitAuth">
                            <!--属于这些范围的算付费，直接打印名称-->
                            <span v-if="[5, 6, 10, 11, 12, 13].includes(item.visitAuth)">{{ tagItem.dictLabel}}</span>
                            <span v-else>免费</span>
                          </el-tag>
                        </div>
                        </router-link>

                      <a :href="'/subject/' + item.uid"  class="detailImage">
                        <img v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]" >
                        <img v-else src="../../static/images/default_black.jpg">
                        <div v-if="item.openPictureTitle === 1" class="carousel-title" style="position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%);">
                          <span>{{ item.subjectName }}</span>
                        </div>
                      </a>

                      <div class="summary">
                        <span v-if="item.author">{{item.author}} · </span>
                        <span>{{item.subjectItemCount}} 章节 · </span>
                        <span>{{item.summary}}</span>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <!--分页-->
              <div class="block"  style="text-align: center;">
                <el-pagination
                  @current-change="handleCurrentChange"
                  :current-page.sync="currentPage"
                  :page-size="pageSize"
                  layout="total, prev, pager, next, jumper"
                  :total="total">
                </el-pagination>
              </div>

            </el-card>

          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>


<script>

import {getSubjectList, getSubjectSortList} from "../api/subject";
import Sticky from "@/components/Sticky";
import {getListByDictTypeList} from "@/api/sysDictData";
import {recorderBehavior} from "../api";

export default {
  name: "index",
  components: {
    Sticky,
  },
  data() {
    return {
      dataList: [],
      currentPage: 1,
      pageSize: 12,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      activeName: "0", // 激活页
      maxHeight: window.screen.height - 300,
      defaultActive: "",
      sortList: [],
      selectSort: {},
      selectSortName: "",
      selectSortUid: "",
      sortMap: null,
      orderByDescColumn: "",
      isSelection: null, // 是否精选
      colorList: ["default", "info", "primary", "success", "warning", "danger"],
      defaultSubjectSortData: [
        {
          uid: "uid0000000000",
          name: "全部专栏",
          summary: "时时刻刻，发现身边的美好",
          photoUrl:  this.$SysConf.defaultSubjectSortImages[0]
        },
        {
          uid: "uid0000000000",
          name: "推荐专栏",
          summary: "好咖啡要和朋友一起品尝，好“资料”也要和同样喜欢它的人一起分享。",
          photoUrl:  this.$SysConf.defaultSubjectSortImages[1]
        },

      ],
      visitAuthDictList: [], // 访问权限列表
    };
  },

  mounted() {

  },
  created() {
    // 获取专题列表
    this.subjectSortList();
    this.getDictList()
  },

  methods: {
    // 获取专题列表
    subjectList() {
      let params = {};
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      params.orderByDescColumn = this.orderByDescColumn
      params.subjectSortUid = this.selectSortUid
      params.isSelection = this.isSelection
      getSubjectList(params).then(response => {
        console.log("得到的结果2", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.dataList = response.data.records;
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          let list = this.dataList
          for (let i = 0; i < list.length; i++) {
            if (list[i].uid == this.subjectUid) {
              this.showSubjectItemList(list[i])
            }
          }
        }
      });
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_visit_auth']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.visitAuthDictList = dictMap.sys_visit_auth.list
        }
      });
    },
    getVisitAuth(item) {
      let visitAuthDictList = this.visitAuthDictList
      for (let i = 0; i < visitAuthDictList.length; i++) {
        if (parseInt(visitAuthDictList[i].dictValue) == item.visitAuth) {
          return visitAuthDictList[i].dictLabel
        }
      }
    },
    subjectSortList() {
      let sortMap = new Map();
      let params = {}
      params.pageSize = 30;
      params.currentPage = 1;
      getSubjectSortList(params).then(response => {
          console.log("得到的结果", response)
          if (response.code == this.$ECode.SUCCESS) {
            let sortList = response.data.records;
            for (let i = 0; i < sortList.length; i++) {
              sortList[i].index = i
              sortMap.set(sortList[i].uid, sortList[i])
            }
            this.sortMap = sortMap
            this.sortList = sortList
            if (sortList && sortList.length > 0) {
              this.selectSortHandle(sortList[0])
            }
          }
      })
    },
    //改变页码
    handleCurrentChange(val) {
      this.currentPage = val; //改变当前所指向的页数
      this.subjectList();
    },
    handleClick(tab, event) {
      this.currentPage = 1
      this.pageSize = 12
      this.newProblemData = []
      switch (tab.name) {
        case "0": {
          this.orderByDescColumn = "sort"
        } break;
        // 最新
        case "1": {
          this.orderByDescColumn = "create_time"
        } break;
        // 最热
        case "2": {
          this.orderByDescColumn = "click_count"
        } break;
      }
      this.subjectList()
    },
    handleSelect(index ,keyPath) {
        console.log("点击", index, keyPath)
        this.selectSort = this.sortList[0]
        console.log("选择", this.selectSort)
    },
    loadContent: function () {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      this.subjectList()
    },
    goDetail(subjectUid) {
      console.log("跳转专栏详情")
      this.$router.push("/subject/" + subjectUid)
    },
    selectSortHandle: function (subjectSort) {
      this.selectSort = subjectSort
      this.orderBy = ""
      // 默认按创建时间切换
      this.orderByDescColumn = "sort"
      this.isEnd = false;
      this.isSelection =  null
      this.activeName = "0"

      // 切换专栏实时记录
      setTimeout(() => {
        recorderBehavior({
          "otherData": subjectSort.name,
          "bizUid": subjectSort.uid,
          "bizType": "SUBJECT_SORT",
          "behavior": "CLICK_SUBJECT_SORT"
        }).then(response => {
        })
      }, 1000)

      this.selectSortName = subjectSort.name

      // 如果是系统预设，那么需要直接取配置中的信息
      if (subjectSort.systemPreinstall === 1) {
        this.selectSortUid = ""
        let config = JSON.parse(subjectSort.preinstallConfig)
        if (config.isSelection) {
          this.isSelection = config.isSelection
        }
        this.subjectList()
        return
      }

      this.selectSortUid = subjectSort.uid
      this.pageSize = 16
      this.currentPage = 1
      this.momentList = []
      this.subjectList()

    },

  }
};
</script>

<style scoped>
.carousel-title {
  cursor: pointer;
  position: absolute;
  z-index: 10;
  bottom: 40px;
  height: 40px;
  width: 97%;
  line-height: 40px;
  text-align: center;
  background: rgba(0, 0, 0, 0.3);
}

.carousel-title span {
  color: white;
  font-size: 22px;
  display: inline-block;
}
.tab-pane {
  font-size: 16px;
  color: #282828;
}

.tab-pane-active {
  font-size: 16px;
  color: #00a7eb;
}
.tip {
  padding: 10px;
  margin: 5px auto 5px auto;
  background-color: white;
  border-radius: 4px;
  color: #888888;
}
.tip strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
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

.topicStyle {
  color: #fff0ff;
  background: rgb(0, 107, 255);
}

.subjectItemCard {
  height:40px;
  line-height: 40px;
  text-align: center;
  cursor: pointer;
  font-size: 14px;
}

.subjectBackground {
  backdrop-filter:saturate(150%) blur(8px);
  -webkit-backdrop-filter:saturate(150%) blur(8px);
  background-color:rgba(0,0,0,.3);
}

.detail {
  border-radius: 6px;
  color: #21293c;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid #f2f6f9;
  background: #f1f1f1;
  height: 230px;
  padding: 0 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
  transition: all .3s;
}

.detail img {
  height: 160px;
  width: 100%;
  -webkit-transition: all 0.6s ease;
  transition: all 0.6s ease;
  margin-bottom: 10px;
}

.detail .summary {
  font-weight: normal;
  line-height: 18px;
  font-size: 13px;
  margin-left: 5px;
  white-space: nowrap;
  text-overflow:ellipsis;
  overflow:hidden;
}
</style>
