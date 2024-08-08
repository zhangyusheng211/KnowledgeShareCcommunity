<template>
  <div>
    <div class="pagebg sorts"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">归档</a>
      </h1>


      <el-tabs class="sortEvent" v-model="activeName" @tab-click="handleClick" style="margin-top: 5px">

        <el-tab-pane name="2" label="分类">
          <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>分类</span></span>

          <div class="sortBox">
            <div class="time">
              <div class="block">
                <div class="radio" style="margin-bottom:20px;"></div>
                <el-timeline :reverse="reverse">
                  <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                  @click="getBlogList(activity.uid)"
                  :class="[activity.uid == selectBlogUid ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{activity.sortName}}</span>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>

            <div class="article">
              <div class="block" v-infinite-scroll="loadBlogSort">
                <el-timeline>
                  <el-timeline-item
                    v-for="item in itemByDate"
                    :key="item.uid"
                    :timestamp="item.createTime"
                    placement="top"
                  >
                    <el-card>
                      <h4 @click="goToList('blogContent', item)" class="itemTitle">{{item.title}}</h4>
                      <br>
                      <el-tag class="elTag" v-if="item.isOriginal==1" type="danger">原创</el-tag>
                      <el-tag class="elTag" v-if="item.isOriginal==0" type="info">转载</el-tag>

                      <el-tag
                        class="elTag"
                        v-if="item.author"
                        @click="goToList('author', item)"
                      >{{item.author}}</el-tag>

                      <el-tag
                        class="elTag"
                        type="success"
                        v-if="item.blogSort != null"
                        @click="goToList('blogSort', item)"
                      >{{item.blogSort.sortName}}</el-tag>
                      <el-tag
                        class="elTag"
                        v-for="tagItem in item.tagList"
                        v-if="tagItem != null"
                        :key="tagItem.uid"
                        style="margin-left: 3px;"
                        @click="goToList('tag', tagItem)"
                        type="warning"
                      >{{tagItem.content}}</el-tag>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </div>

        </el-tab-pane>


        <el-tab-pane name="3" label="标签">
          <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>标签</span></span>

          <div class="sortBox">
            <div class="time">
              <div class="block">
                <div class="radio" style="margin-bottom:20px;"></div>
                <el-timeline :reverse="reverse">
                  <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                  @click="getBlogTagList(activity.uid)"
                  :class="[activity.uid == selectBlogUid ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{activity.tagName}}</span>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>

            <div class="article">
              <div class="block" v-infinite-scroll="loadTag">
                <el-timeline>
                  <el-timeline-item
                    v-for="item in itemByDate"
                    :key="item.uid"
                    :timestamp="item.createTime"
                    placement="top"
                  >
                    <el-card>
                      <h4 @click="goToList('blogContent', item)" class="itemTitle">{{item.title}}</h4>
                      <br>
                      <el-tag class="elTag" v-if="item.isOriginal==1" type="danger">原创</el-tag>
                      <el-tag class="elTag" v-if="item.isOriginal==0" type="info">转载</el-tag>

                      <el-tag
                        class="elTag"
                        v-if="item.author"
                        @click="goToList('author', item)"
                      >{{item.author}}</el-tag>

                      <el-tag
                        class="elTag"
                        type="success"
                        v-if="item.blogSort != null"
                        @click="goToList('blogSort', item)"
                      >{{item.blogSort.sortName}}</el-tag>
                      <el-tag
                        class="elTag"
                        v-for="tagItem in item.tagList"
                        v-if="tagItem != null"
                        :key="tagItem.uid"
                        style="margin-left: 3px;"
                        @click="goToList('tag', tagItem)"
                        type="warning"
                      >{{tagItem.content}}</el-tag>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane name="1" label="归档">
          <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>归档</span></span>
          <div class="sortBox">
            <div class="time">
              <div class="block">
                <div class="radio" style="margin-bottom:20px;">
                  <el-switch
                    v-model="reverse"
                    active-text="倒序"
                    inactive-text="正序"
                    active-color="#000000"
                    inactive-color="#13ce66"
                  ></el-switch>
                </div>
                <el-timeline :reverse="reverse">
                  <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                  @click="clickTime(activity.content)"
                  :class="[activity.content == selectContent ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{activity.content}}</span>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>

            <div class="article">
              <div class="block">
                <el-timeline>
                  <el-timeline-item
                    v-for="item in itemByDate"
                    :key="item.timestamp"
                    :timestamp="item.createTime"
                    placement="top"
                  >
                    <el-card>
                      <h4 @click="goToList('blogContent', item)" class="itemTitle">{{item.title}}</h4>
                      <br>
                      <el-tag class="elTag" v-if="item.isOriginal==1" type="danger">原创</el-tag>
                      <el-tag class="elTag" v-if="item.isOriginal==0" type="info">转载</el-tag>

                      <el-tag
                        class="elTag"
                        v-if="item.author"
                        @click="goToList('author', item)"
                      >{{item.author}}</el-tag>

                      <el-tag
                        class="elTag"
                        type="success"
                        v-if="item.blogSort != null"
                        @click="goToList('blogSort', item.blogSort)"
                      >{{item.blogSort.sortName}}</el-tag>
                      <el-tag
                        class="elTag"
                        v-for="tagItem in item.tagList"
                        v-if="tagItem != null"
                        :key="tagItem.uid"
                        style="margin-left: 3px;"
                        @click="goToList('tag', tagItem)"
                        type="warning"
                      >{{tagItem.content}}</el-tag>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </div>

        </el-tab-pane>
      </el-tabs>


    </div>
  </div>
</template>

<script>
import { getSortList, getArticleByMonth } from "../api/sort";
import {getArticleByBlogSortUid, getBlogSortList} from "../api/classify";
import {getArticleByTagUid, getTagList} from "../api/tag";
import {recorderBehavior} from "../api";
export default {
  data() {
    return {
      selectBlogUid: "",
      selectContent: "",
      activeName: "2", // 激活页
      reverse: false,
      activities: [],
      itemByDate: [],
      articleByDate: {}
    };
  },
  components: {
    //注册组件
  },
  mounted() {},
  created() {
    this.blogSortList()

    setTimeout(()=>{
      recorderBehavior({"otherData": "归档", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)
  },
  methods: {
    sortList() {
      getSortList().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let activities = response.data;
          let result = [];
          for (let a = 0; a < activities.length; a++) {
            let temp = activities[a].replace("年", "-").replace("月", "-") + "1";
            let dataForDate = { content: activities[a], timestamp: temp };
            result.push(dataForDate);
          }
          this.activities = result;
          // 默认选择最后一个
          this.clickTime(activities[activities.length - 1]);
        }
      });
    },
    blogSortList() {
      getBlogSortList().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let activities = response.data;
          let result = [];
          for (let a = 0; a < activities.length; a++) {
            let dataForDate = {
              sortName: activities[a].sortName,
              uid: activities[a].uid
            };
            result.push(dataForDate);
          }
          this.activities = result;
          // 默认选择第一个
          this.getBlogList(activities[0].uid);
        }
      });
    },
    tagList() {
      getTagList().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let activities = response.data;
          let result = [];
          for (let a = 0; a < activities.length; a++) {
            let dataForDate = {
              tagName: activities[a].content,
              uid: activities[a].uid
            };
            result.push(dataForDate);
          }
          this.activities = result;
          // 默认选择第一个
          this.getBlogTagList(activities[0].uid);
        }
      });
    },
    getBlogTagList(tagUid) {
      this.selectBlogUid = tagUid;
      let params = new URLSearchParams();
      params.append("tagUid", tagUid);
      getArticleByTagUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.itemByDate = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    getBlogList(blogSortUid) {
      debugger
      this.selectBlogUid = blogSortUid;
      let params = new URLSearchParams();
      params.append("blogSortUid", blogSortUid);
      getArticleByBlogSortUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.itemByDate = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    loadBlogSort() {
      let params = new URLSearchParams();
      if (
        this.selectBlogUid == null ||
        this.selectBlogUid == "" ||
        this.selectBlogUid == undefined
      ) {
        return;
      }
      params.append("blogSortUid", this.selectBlogUid);
      params.append("currentPage", this.currentPage + 1);
      getArticleByBlogSortUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.itemByDate = this.itemByDate.concat(response.data.records);
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    loadTag() {
      let params = new URLSearchParams();
      if (
        this.selectBlogUid == null ||
        this.selectBlogUid == "" ||
        this.selectBlogUid == undefined
      ) {
        return;
      }
      params.append("tagUid", this.selectBlogUid);
      params.append("currentPage", this.currentPage + 1);
      getArticleByTagUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.itemByDate = this.itemByDate.concat(response.data.records);
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    clickTime(content) {
      this.selectContent = content;
      let params = new URLSearchParams();
      params.append("monthDate", content);
      getArticleByMonth(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.itemByDate = response.data;
        }
      });
    },
    //跳转到搜索详情页
    goToList(type, entity) {
      switch (type) {
        case "tag":
        {
          // 标签uid
          let routeData = this.$router.resolve({
            path: "/list",
            query: { tagUid: entity.uid }
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "blogSort":
        {
          let routeData = this.$router.resolve({
            path: "/list",
            query: { sortUid: entity.blogSort.uid }
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "author":
        {
          let routeData = this.$router.resolve({
            path: "/list",
            query: { author: entity.author }
          });
          window.open(routeData.href, "_blank");
        }
          break;

        case "blogContent":
        {
          if(entity.type == "0") {
            let routeData = this.$router.resolve({
              path: "/info/" + entity.oid
            });
            window.open(routeData.href, "_blank");
          } else if(entity.type == "1") {
            window.open(entity.outsideLink, '_blank');
          }
        }
          break;
      }
    },
    // 一级tab点击事件
    handleClick(tab, event) {
      // 切换选项卡时，永远选择第一个
      this.listData = []
      switch (tab.label) {
        case "归档": {
          this.reverse = true
          this.currentPage = 1
          this.sortList()
        } break;
        case "分类": {
          this.reverse = false
          this.currentPage = 1
          this.blogSortList();
        } break;
        case "标签": {
          this.reverse = false
          this.currentPage = 1
          this.tagList()
        } break;
      }

    },
    formatDate: function(time) {
      let date = new Date(time);
      let year = date.getFullYear();
      /* 在日期格式中，月份是从0开始的，因此要加0
       * 使用三元表达式在小于10的前面加0，以达到格式统一  如 09:11:05
       * */
      let month =
        date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1;
      let day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
      // 拼接
      return year + "-" + month + "-" + day;
    }
  }
};
</script>


<style scoped>
.sortBox {
  color: #555;
}

.el-timeline-item__content {
  cursor: pointer;
}

.el-tabs__active-bar {
  display: none;
}
.sortEvent .el-tabs__nav {
  width: 100%;
  text-align: center;
}

.tab-pane {
  font-size: 16px;
  color: #282828;
}

.tab-pane-active {
  font-size: 16px;
  color: #00a7eb;
}

.sortBoxSpan {
  cursor: pointer;
}
.sortBoxSpan:hover {
  color: #409eff;
}
.sortBoxSpanSelect {
  color: #409eff;
}

.itemTitle {
  cursor: pointer;
}
.itemTitle:hover {
  color: #409eff;
}
.elTag {
  cursor: pointer;
}
</style>
