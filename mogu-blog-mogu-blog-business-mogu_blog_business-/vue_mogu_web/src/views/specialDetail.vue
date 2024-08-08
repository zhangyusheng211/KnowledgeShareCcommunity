<template>
  <div>
    <div class="pagebg sh"></div>
    <el-image-viewer
      v-if="showViewer"
      :on-close="showViewer = false"
      :url-list="picList"/>

    <el-row>
      <el-col>
        <!--        <div :style="{'background-image': 'url('+ photoUrl +')'}" style=" height: 250px; color: white; text-align: center; background: #000000 no-repeat center; background-size: auto 100%; ">-->
        <div
          style=" height: 250px; color: white; text-align: center; background: #000000 no-repeat center; background-size: auto 100%; ">
          <div style="padding-top: 60px;">
            <p style="font-size: 32px; font-weight: bold;">{{ subject.subjectName }}</p>
            <p style="font-size: 18px;margin-top: 10px;">
              <span v-if="subject.summary">
                {{ subject.summary.substring(0, 30) }}
                <span v-if="subject.summary.length > 30">
                  ...
                </span>
              </span>
            </p>
            <p style="font-size: 16px; margin-top: 10px">
              <span>{{ total }} 章节</span>
              <span>
                <el-tag effect="plain" style="height: 21px; line-height: 21px" v-for="item in visitAuthDictList" :key="item.uid" v-if="parseInt(item.dictValue) === subject.visitAuth">
                  <span v-if="[5, 6, 10, 11, 12, 13].includes(subject.visitAuth)">{{ item.dictLabel}}</span>
                  <span v-else>免费</span>
                </el-tag>
              </span>
            </p>

            <p>
              <UserSubscribe style="margin-top: 15px" v-if="subject.uid" :resourceUid="subject.uid" resourceType="13"></UserSubscribe>
            </p>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="container">
      <el-row>
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <div>
            <el-card class="box-card" :body-style="{ padding: '5px' }">

              <el-tabs v-model="activeIndex" @tab-click="handleClick">
                <el-tab-pane name="1" v-if="showSubjectContent">
                  <span :class="activeIndex=='1'?'tab-pane-active':'tab-pane'" slot="label"><i
                    class="el-icon-collection-tag"></i> <span>专栏介绍</span></span>
                </el-tab-pane>
                <el-tab-pane name="2">
                  <span :class="activeIndex=='2'?'tab-pane-active':'tab-pane'" slot="label"><i
                    class="el-icon-tickets"></i> <span>专栏目录</span></span>
                </el-tab-pane>
              </el-tabs>

              <div
                class="blogs"
                style="padding-top: 1px; padding-bottom: 5px"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row :gutter="20" style="min-height: 700px">

                  <!--展示简介-->
                  <div v-if="activeIndex=='1'"  class="newsview">
                    <div
                      class="news_con ck-content"
                      v-html="subject.content"
                      v-highlight
                      @click="imageChange"
                    ></div>
                  </div>

                  <!--展示目录-->
                  <div v-if="activeIndex=='2'">

                    <div
                      style="padding-top: 1px; padding-bottom: 5px"
                      data-scroll-reveal="enter bottom over 1s"
                    >
                      <el-row v-if="item.blog" :gutter="24" v-for="(item, index) in dataList" :key="item.uid">
                        <router-link :to="'/info/' + item.blog.oid + '?subject=' + item.subjectUid">
                          <el-col class="chooseCol" style="margin: 10px;" :xs="24" :sm="24">
                            <div class="detail">
                              <span>({{(currentPage-1)*pageSize + index + 1}}).  {{ item.blog.title }}</span>
                              <el-tag type="success" style="float: right; margin-right: 50px;" v-if="item.tryRead == '1'" effect="plain">试读</el-tag>
                            </div>
                          </el-col>
                        </router-link>
                      </el-row>
                    </div>

                    <span v-if="dataList.length == 0">
                      <el-empty description="空空如也" image=""></el-empty>
                    </span>

                    <!--分页-->
                    <div class="block" style="text-align: center;" v-if="total > pageSize">
                      <el-pagination
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-size="pageSize"
                        layout="total, prev, pager, next, jumper"
                        :total="total">
                      </el-pagination>
                    </div>
                  </div>

                </el-row>
              </div>
            </el-card>

          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>

import {getSubjectItemList, getSubjectList} from "../api/subject";
import Sticky from "@/components/Sticky";
import {getListByDictTypeList} from "@/api/sysDictData";
import UserSubscribe from "@/components/UserSubscribe";
import {recorderBehavior} from "../api";
export default {
  name: "index",
  components: {
    Sticky,
    UserSubscribe,
  },
  data() {
    return {
      dataList: [],
      currentPage: 1,
      pageSize: 15,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      maxHeight: window.screen.height - 300,
      subjectUid: "", // 专栏uid
      subject: {}, // 专栏
      photoUrl: "", // 专栏图片
      activeIndex: '1',
      showSubjectContent: true,
      showViewer: false,
      picList: [],
      visitAuthDictList: [],
    };
  },

  mounted() {

  },
  created() {
    this.subjectUid = this.$route.params.subjectUid;
    // 获取专题
    this.subjectList()
    // 获取专题列表
    this.getList();
    this.getDictList()
  },

  methods: {
    getList() {
      let params = {};
      params.subjectUid = this.subjectUid;
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      this.loading = true
      getSubjectItemList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.dataList = response.data.records
          this.currentPage = response.data.current
          this.total = response.data.total
        }
        this.loading = false
      })
    },

    // 获取专题列表
    subjectList() {
      let params = {};
      params.uid = this.subjectUid
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      getSubjectList(params).then(response => {
        console.log("得到的结果", response)
        if (response.code == this.$ECode.SUCCESS) {
          let subjectList = response.data.records;
          if (subjectList.length > 0) {
            this.subject = subjectList[0]
            let subject = subjectList[0]
            // 获取专栏图
            if (this.subject.photoList && this.subject.photoList[0]) {
              this.photoUrl = this.subject.photoList[0]
            }
            // 获取专栏是否有内容
            if (subject.content) {
              this.activeIndex = '1'
            } else {
              this.showSubjectContent = false
              this.activeIndex = '2'
            }
            // 如果参数携带了，优先以参数为准
            console.log("参数携带了", this.$route.query, this.$route.query.index === '2')
            if (this.$route.query.index === '2') {
              this.activeIndex = '2'
            }
            // 5S后埋点信息上报
            setTimeout(() => {
              recorderBehavior({
                "otherData": subject.subjectName,
                "bizUid": subject.uid,
                "bizType": "SUBJECT",
                "behavior": "VISIT_SUBJECT"
              }).then(response => {
              })
            }, 5000)

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
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showViewer = true
        this.picList = [e.target.currentSrc]
      }
    },
    //改变页码
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getList();
    },
    handleClick(tab, event) {
      // 替换参数
      console.log("获取url", this.$commonUtil.setUrlParams("index", tab.name))
      history.pushState(null, null, this.$commonUtil.setUrlParams("index", tab.name));
    },
    handleSelect() {

    },
    loadContent: function () {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      this.subjectList()
    },
  }
};
</script>

<style scoped>
.tab-pane-active {
  font-size: 14px;
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

.detail {
  border-radius: 6px;
  width: 100%;
  color: #21293c;
  font-size: 15px;
  font-weight: 600;
  border-bottom: 1px solid #f2f6f9;
  height: 35px;
  line-height: 35px;
  /*padding: 0 35px;*/
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
  transition: all .3s;
}

.chooseCol :hover {
  border-radius: 6px;
  width: 93%;
  color: #21293c;
  font-size: 15px;
  font-weight: 600;
  border-bottom: 1px solid #f2f6f9;
  height: 35px;
  line-height: 35px;
  padding: 0 11px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
  transition: all .3s;
  background: #eeeaea;
}

</style>
