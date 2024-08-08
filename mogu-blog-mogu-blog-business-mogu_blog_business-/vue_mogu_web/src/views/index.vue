<template>

  <article @click="handlesClick">
    <!--banner begin-->
    <div class="picsbox"  v-if="(isFirstRecommendShow || isSecondRecommendShow) && componentShowMap.showFirstLevel">
      <FirstRecommend @isFirstRecommendShow="getFirstRecommendShow"></FirstRecommend>
      <!--banner end-->

      <!-- 二级推荐 -->
      <div class="toppic" >
        <li v-for="item in secondData" :key="item.uid">
          <a :href="item.type == 0 ? VUE_MOGU_WEB + '/info/'+item.oid : item.outsideLink">
            <i>
              <img v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]">
            </i>
            <h2>{{item.title}}</h2>
            <span>{{item.blogSort.sortName}}</span>
          </a>
        </li>
      </div>
    </div>

    <!--移动端签到-->
    <SignDesk v-if="showAppSignIn && componentShowMap.showUserSign" ></SignDesk>

    <!--首页文章分类-->
    <Sticky style="min-height:40px; z-index: 100" id="anchor" v-if="componentShowMap.showBlogSort">
      <el-tabs v-model="activeName" @tab-click="handleClick" style="background: #eeeeee;">
        <el-tab-pane name="0">
          <span slot="label" ><i class="el-icon-collection-tag"></i> <span>推荐</span></span>
        </el-tab-pane>

        <el-tab-pane name="1">
          <span slot="label" ><i class="el-icon-collection-tag"></i> <span>最新</span></span>
        </el-tab-pane>

        <el-tab-pane name="2">
          <span slot="label" ><i class="el-icon-search"></i> <span>关注</span></span>
        </el-tab-pane>

        <el-tab-pane name="3">
          <span slot="label" ><i class="el-icon-star-off"></i> <span>最热</span></span>
        </el-tab-pane>

        <el-tab-pane name="4">
          <span slot="label" ><i class="el-icon-news"></i> <span>随机</span></span>
        </el-tab-pane>

        <el-tab-pane v-for="(item, index) in hotBlogSortData" :name="(index+5)+''" :key="item.uid">
          <span slot="label" ><i :class="item.icon"></i> <span>{{item.sortName}}</span></span>
        </el-tab-pane>
      </el-tabs>
    </Sticky>

    <!--首页正文-->
    <div class="blogsbox" style="margin-top: 10px;">
      <div
        v-for="(item, itemIndex) in newBlogData"
        :key="item.uid"
        class="blogs"
        data-scroll-reveal="enter bottom over 1s"
      >

        <div v-if="item.visitAuth === 3" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="评论可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/comment.png"></img>
          </el-tooltip>
        </div>
        <div v-else-if="item.visitAuth === 5" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="会员可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/vip.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="item.visitAuth === 6" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="付费可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/pay.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="item.visitAuth === 7" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="点赞可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/vip.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="item.visitAuth === 8" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="收藏可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/collect.png"></img>
          </el-tooltip>
        </div>
        <div v-else-if="item.visitAuth === 9" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="关注可见" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/watch.png"></img>
          </el-tooltip>
        </div>

        <div v-else-if="item.isTop === 1" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">
          <el-tooltip class="item" effect="dark" content="置顶文章" placement="top">
            <img style="width: 30px; height: 30px" src="../assets/img/top.png"></img>
          </el-tooltip>
        </div>

<!--        <div v-else-if="item.isOriginal === '1'" style="float: right; font-size:20px; margin-top: -22px; margin-right: -22px;">-->
<!--          <el-tooltip class="item" effect="dark" content="原创文章" placement="top">-->
<!--            <img style="width: 40px; height: 40px" src="../assets/img/original.png"></img>-->
<!--          </el-tooltip>-->
<!--        </div>-->

        <h3 class="blogtitle">
          <a :href="item.type == 0 ? VUE_MOGU_WEB + '/info/' + item.oid : item.outsideLink" target="_blank">{{item.title}}</a>
        </h3>

        <el-row>
          <el-col  :span="19" :xs="24">
            <p class="blogtext" v-html="$xss(item.summary, options)">{{item.summary}}</p>
          </el-col>

          <el-col  :span="5" :xs="0">
              <span class="blogpic" >
                <a :href="item.type == 0 ? VUE_MOGU_WEB + '/info/' + item.oid : item.outsideLink" target="_blank" >
                  <img v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]" >
                </a>
              </span>
          </el-col>
        </el-row>

        <div class="bloginfo">
          <ul>
            <div @mouseover="onEnterTd(item.user, itemIndex)" @mouseleave="onLeaveTd(false)">
              <li style="padding-right: 6px; text-align: center;cursor: pointer"  >
              <span :id="getTabIndex(itemIndex)" :class="item.user.userTag > 0 ?'vip-avatar':''"  >
                <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user"  @click.native="getUserCenter(item)" fit="fill"  size="medium"  :src="item.user.photoUrl"></el-avatar>
                <el-avatar v-else fit="fill" size="small" src="https://empty">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="item.user.userTag > 0">v</span>
              </span>
              </li>
              <li  class="author" style="margin-top: 9px;">
                <span v-if="item.user"  class="pointer" :class="'lv'+ item.user.userLevel" @click="getUserCenter(item)">{{item.user.nickName}}</span>
              </li>
            </div>

<!--            <UserAvatar :user="item.user" style="display: inline-block; float: left;"></UserAvatar>-->

            <li class="lmname" style="margin-top: 8px;">
              <el-tag
                style="cursor:pointer;"
                :type="typeList[(item.blogSort.sort+2)%5]"
                @click.native="goToSortList(item.blogSort.uid)"
                hit
                size="mini"
                effect="light">
                {{ item.blogSort.sortName }}
              </el-tag>

              <el-tag
                style="margin-left: 1px; cursor:pointer;"
                v-for="(tag, index) in item.tagList"
                @click.native="goToTagList(tag.uid)"
                :key="tag.uid"
                :type="typeList[tag.sort%5]"
                hit
                size="mini"
                effect="light">
                {{ tag.content }}
              </el-tag>
            </li>

            <li class="view" style="margin-top: 8px;margin-left: 3px;">
              <span class="iconfont">&#xe8c7;</span>
              <span>{{item.clickCount}}</span>
            </li>

            <li class="like" style="margin-top: 8px; margin-left: 3px;">
              <span class="iconfont">&#xe606;</span>
              <span>{{timeAgo(item.createTime)}}</span>
            </li>

<!--            <li class="like" style="margin-top: 8px;margin-left: 3px;">-->
<!--              <span class="iconfont">&#xe663;</span>-->
<!--              {{item.collectCount}}-->
<!--            </li>-->

            <li class="like" style="margin-top: 8px;">
              <Collection v-if="item.collectInfo"
                :collectCountValue="item.collectInfo.collectCount"
                :isCollectValue="item.collectInfo.isCollect"

                :bizUid="item.uid" :collectType="'1'"></Collection>
            </li>

            <li class="like" style="margin-top: 8px;">
              <Praise
                      :isPraiseValue="item.praiseInfo.isPraise"
                      :isTreadValue="item.praiseInfo.isTread"
                      :praiseCountValue="item.praiseInfo.praiseCount"
                      :treadCountValue="item.praiseInfo.treadCount"
                      :bizUid="item.uid"
                      :resourceType="'1'"></Praise>
            </li>



          </ul>
        </div>
      </div>

      <div class="isEnd">
        <!-- <span v-if="!isEnd">正在加载中~</span> -->
        <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading" >查看更多</div>
        <div class="lds-css ng-scope" v-if="!isEnd&&loading">
          <div style="width:100%;height:100%" class="lds-facebook">
            <div></div>
            <div></div>
            <div></div>
          </div>
        </div>
        <span v-if="isEnd && newBlogData.length == 0">
          <el-empty description="空空如也" image=""></el-empty>
        </span>
        <span v-if="isEnd && newBlogData.length > 0">我也是有底线的~</span>
      </div>
    </div>
    <!--blogsbox end-->

    <div class="sidebar" style="z-index: 999; margin-top: 10px;">

      <UserCard v-if="showUsrCard && componentShowMap.showUserCard" :usrCard="this.usrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>

      <UserInfo v-if="componentShowMap.showUserCard"></UserInfo>

      <MyTask v-if="componentShowMap.showUserTask"></MyTask>

      <SignCalendar v-if="componentShowMap.showUserSign" :calendarList="this.calendarList"></SignCalendar>

      <UserTopN v-if="componentShowMap.showUserRank"></UserTopN>

      <!--标签云-->
      <TagCloud v-if="componentShowMap.showHotTag"></TagCloud>

      <!-- 三级推荐 -->
      <ThirdRecommend v-if="componentShowMap.showThreeRecommend"></ThirdRecommend>

      <!--四级推荐-->
      <FourthRecommend v-if="componentShowMap.showFourRecommend"></FourthRecommend>

      <!--点击排行-->
      <HotBlog v-if="componentShowMap.showHotClick"></HotBlog>

      <!-- 友情链接-->
      <Link v-if="componentShowMap.showUserLink"></Link>

      <FollowUs v-if="componentShowMap.showFollowUs"></FollowUs>

<!--      <Sticky :sticky-top="50" >-->
<!--        &lt;!&ndash;关注我们&ndash;&gt;-->
<!--        <FollowUs v-if="componentShowMap.showFollowUs"></FollowUs>-->
<!--      </Sticky>-->


    </div>

  </article >
</template>

<script>
  import FirstRecommend from "../components/FirstRecommend";
  import ThirdRecommend from "../components/ThirdRecommend";
  import FourthRecommend from "../components/FourthRecommend";
  import TagCloud from "../components/TagCloud";
  import HotBlog from "../components/HotBlog";
  import FollowUs from "../components/FollowUs";
  import SignDesk from "../components/SignDesk";
  import Copyright from "../components/Copyright"
  import Link from "../components/Link";
  import Sticky from "@/components/Sticky";
  import UserTopN from "@/components/UserTopN";
  import MyTask from "@/components/MyTask";
  import SignCalendar from "@/components/SignCalendar";
  import Collection from "../components/Collection";
  import Praise from "../components/Praise";
  import UserCard from "../components/UserCard";
  import UserInfo from "../components/UserInfo";
  import UserAvatar from "../components/UserAvatar";
  import {getBlogByLevel, getHotBlogSort, getNewBlog, recorderBehavior, recorderVisitPage} from "../api/index";
  import { Loading } from 'element-ui';
  import {getBlogByUid} from "../api/blogContent";
  import {timeAgo} from "../utils/webUtils";
  import {getCookie, setCookie, delCookie} from "@/utils/cookieUtils";

  export default {
    name: "index",
    components: {
      //注册组件
      FirstRecommend,
      FourthRecommend,
      ThirdRecommend,
      TagCloud,
      HotBlog,
      FollowUs,
      Link,
      Copyright,
      Sticky,
      SignDesk,
      UserTopN,
      SignCalendar,
      UserInfo,
      Collection,
      Praise,
      UserCard,
      MyTask,
      UserAvatar,
    },
    data() {
      return {
        // xss白名单配置
        options : {
          whiteList: {
            a: ['href', 'title', 'target'],
            span: ['class'],
            h1: ['class'],
            h2: ['class'],
            h3: ['class'],
            h4: ['class'],
            pre: [],
            code: ['class'],
            p: ['class']
          }
        },
        showAppSignIn: false,
        loadingInstance: null, // loading对象
        VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
        isFirstRecommendShow: true, //是否显示一级推荐
        isSecondRecommendShow: true, //是否显示二级推荐
        secondData: [], //；二级级推荐数据
        newBlogData: [], //最新文章
        hotBlogSortData: [], // 最热博客分类
        keyword: "",
        activeName: "0",
        orderBy: "", // 排序
        orderByDescColumn: "", // 降序字段
        blogSortUid: "", // 当前选中的博客分类UID
        currentPage: 1,
        pageSize: 15,
        total: 0, //总数量
        isEnd: false, //是否到底底部了
        loading: false, //是否正在加载
        calendarList: [],
        usrCard: "",
        coorDinatex: "",
        coorDinatey: "",
        showUsrCard: false,
        currentId:1,
        listName: [], //滚动的列表
        typeList: ['warning', 'danger', 'success', 'info','primary'],
        leaveTimeout: null, // 离开事件
        defaultAvatar: process.env.defaultAvatar, // 默认头像
        isShowNoticeSuccess: false,
        componentShowMap: {},
      };
    },
    watch: {
      // 判断个人中心是否弹出新内容(用于控制弹出框)
      '$store.state.app.webConfigData': function (event, oldFlag) {
          this.getComponentShowMap()
      },
    },
    mounted() {
      // 注册scroll事件并监听
      this.loading = false;
      this.resizeWin()
      this.getComponentShowMap()
    },
    created() {
      let secondParams = new URLSearchParams();
      secondParams.append("level", 2);
      // 是否排序
      secondParams.append("useSort", 1);
      getBlogByLevel(secondParams).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.secondData = response.data.records;
          // 当没有数据时，进行触发回调函数
          if(this.secondData.length > 0) {
            this.isSecondRecommendShow = true
          } else {
            this.isSecondRecommendShow = false
          }
        }
      });
      // 获取最新博客
      this.newBlogList();
      // 获取最热博客分类列表
      this.hotBlogSortList()
      // 跳转上一次学习进度
      this.showSubjectProcess()
      // 5S后埋点信息上报
      setTimeout(()=>{
        recorderBehavior({"otherData": "首页", "behavior": "VISIT_PAGE"}).then(response => {})
      }, 5000)
    },
    methods: {
      getComponentShowMap() {
        let webConfigData = this.$store.state.app.webConfigData
        if (webConfigData.componentShowMap) {
          this.componentShowMap = webConfigData.componentShowMap
        }
      },
      handlesClick() {
        this.showUsrCard = false
      },
      // 显示上一次专栏学习进度
      showSubjectProcess() {
        // 从Cookie中加载学习进度
        let subjectRecord = localStorage.getItem('subjectRecord')
        console.log("判断是否获取到专栏记录", subjectRecord)
        // 只展示一次，避免频繁限制影响用户
        if (subjectRecord) {
          let subjectRecordJson = JSON.parse(subjectRecord)
          // 缓存时间
          const cacheTime = new Date(subjectRecordJson.date);
          const nowTime = new Date();
          // 计算两个日期之间的毫秒数差值
          const timeDifference = Math.abs(nowTime - cacheTime);
          // 将毫秒数差值转换为小时
          const dayDifference = Math.ceil(timeDifference / (1000 * 60));
          // 如果时间相差30分钟，暂时不弹出上一个访问进度
          if (dayDifference < 30) {
            return
          }

          let subjectLink = this.VUE_MOGU_WEB + "/subject/" + subjectRecordJson.subjectUid
          let urlLink = this.VUE_MOGU_WEB + "/info/" + subjectRecordJson.blogOid
          // 然后开始展开提示
          this.$notify({
            title: '跳转上一次进度',
            onClose: () => {
              console.log("关闭通知")
              localStorage.setItem('subjectRecord', "");
            },
            limit: 1,
            duration: 20000,
            dangerouslyUseHTMLString: true,
            message:  '上一次学习专栏: <a href="'+ subjectLink +'" style="color: #08c">' + subjectRecordJson.subjectTitle + '</a> <br>  ' +
              '上一次学习文章: <a href="'+ urlLink +'" style="color: #08c">' + subjectRecordJson.blogTitle + '</a>'
          });
          // 只弹出一次，关闭后删除缓存
          localStorage.setItem('subjectRecord', "");
        }

      },
      /**
       * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
       * @param dateTimeStamp
       * @returns {string}
       */
      timeAgo(dateTimeStamp) {
        return timeAgo(dateTimeStamp)
      },
      menu(e) {
        e = e || window.event;
        if (e.wheelDelta) {  //判断浏览器IE，谷歌滑轮事件
          if (e.wheelDelta > 0) { //当滑轮向上滚动时
            alert("滑轮向上滚动");
          }
          if (e.wheelDelta < 0) { //当滑轮向下滚动时
            alert("滑轮向下滚动");
          }
        }
        else if (e.detail) {  //Firefox滑轮事件
          if (e.detail> 0) { //当滑轮向上滚动时
            alert("滑轮向上滚动");
          }
          if (e.detail< 0) { //当滑轮向下滚动时
            alert("滑轮向下滚动");x
          }
        }
      },
      getTabIndex: function (index) {
        return "blogId" + index
      },
      //鼠标进入的事件。
      onEnterTd (item,index) {
        this.showUsrCard = true;
        this.usrCard = item;
        let tagElement = document.getElementById("blogId"+ index);
        let leftx = tagElement.getBoundingClientRect().left;
        let topx = tagElement.getBoundingClientRect().top;
        this.coorDinatex = leftx;
        this.coorDinatey = topx;
        console.log( `坐标位置为（${leftx},${topx}）`);
        this.leaveTimeout = null
      },
      onCardEnterTd: function () {
        clearTimeout(this.leaveTimeout)
      },
      //鼠标离开的事件。
      onLeaveTd (nowClean) {
        let that = this
        if (nowClean) {
          that.showUsrCard = false;
        } else {
          this.leaveTimeout = setTimeout(() => {
            that.showUsrCard = false;
          }, 300)
        }
      },
      resizeWin () {
        //当前window 宽
        let centerWidth = document.documentElement.scrollWidth;
        if (centerWidth > 650) {
          this.showAppSignIn = false
        }else {
          this.showAppSignIn = true
        }
      },
      getFirstRecommendShow(isFirstRecommendShow) {
        this.isFirstRecommendShow = isFirstRecommendShow
      },
      //跳转到搜索详情页
      goToSortList(uid) {
        let routeData = this.$router.push({
          path: "/list",
          query: {sortUid: uid}
        });
      },
      //跳转到搜索详情页
      goToTagList(uid) {
        let routeData = this.$router.push({
          path: "/list",
          query: {tagUid: uid}
        });
      },
      // 获取最热博客分类列表
      hotBlogSortList: function () {
        getHotBlogSort().then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.hotBlogSortData = response.data;
          }
        });
      },
      handleClick: function (tab, event) {
        // this.$el.querySelector("#anchor").scrollIntoView()
        this.isEnd = false;
        this.currentPage = 1
        this.activeName = tab.index
        switch (tab.index) {
          case "0": {
            this.orderBy = "";
            this.orderByDescColumn = "";
            this.blogSortUid = ""
            this.newBlogList()
          } break;
          case "1": {
            this.orderBy = "";
            this.orderByDescColumn = "create_time";
            this.blogSortUid = ""
            this.newBlogList()
          } break;
          case "2": {
            this.orderBy = "userWatch";
            this.orderByDescColumn = "";
            this.blogSortUid = ""
            this.newBlogList()
          } break;
          case "3": {
            this.orderBy = "";
            this.orderByDescColumn = "click_count";
            this.blogSortUid = ""
            this.newBlogList()
          } break;
          case "4": {
            this.orderBy = "";
            this.orderByDescColumn = "random";
            this.blogSortUid = ""
            this.newBlogList()
          } break;
          default: {
            this.orderByDescColumn = "create_time";
            let blogSort = this.hotBlogSortData[tab.index - 5]
            this.blogSortUid = blogSort.uid
            this.newBlogList()
          }
        }
      },
      // 跳转到个人中心页
      getUserCenter: function (blog) {
        console.log("跳转到用户中心", blog)
        // 判断是后台添加，还是前台投稿
        if(blog.articleSource == 0) {
          let routeData = this.$router.resolve({
            path: "/userCenter",
            query: {adminUid: blog.adminUid}
          });
          window.open(routeData.href, '_blank');
        } else {
          let routeData = this.$router.resolve({
            path: "/userCenter",
            query: {userUid: blog.userUid}
          });
          window.open(routeData.href, '_blank');
        }
      },

      //最新博客列表
      newBlogList() {
        let that = this;
        that.loadingInstance = Loading.service({
          lock: true,
          text: '正在努力加载中……',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        let params = {};
        this.currentPage = 1
        params.currentPage = this.currentPage;
        params.pageSize = 15;
        params.orderBy = this.orderBy
        params.orderByDescColumn = this.orderByDescColumn
        params.blogSortUid = this.blogSortUid
        getNewBlog(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            let newBlogData = response.data.records;
            that.newBlogData = newBlogData
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;
            //全部加载完毕
            if (newBlogData.length < this.pageSize) {
              this.isEnd = true;
            }
          }
          that.loadingInstance.close();
        },function(err){
          that.loadingInstance.close();
        });
      },

      loadContent: function () {
        let that = this;
        this.loading = false;
        this.currentPage = this.currentPage + 1;
        let params = {};
        params.currentPage = this.currentPage;
        params.pageSize = this.pageSize;
        params.orderBy = this.orderBy
        params.orderByDescColumn = this.orderByDescColumn
        params.blogSortUid = this.blogSortUid
        getNewBlog(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.isEnd = false;
            let newBlogData = response.data.records;
            let newData = that.newBlogData.concat(newBlogData);
            that.newBlogData = newData;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;
            //全部加载完毕
            if (newBlogData.length < that.pageSize) {
              that.isEnd = true;
            }
          } else {
            that.isEnd = true;
          }
          that.loading = false;
        });
      }
    }
  };
</script>

<style>

  .blogs {
    transition: transform .2s, box-shadow .2s;
  }
  .blogs:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 20px 0 rgba(95,101,105,.15);
  }

  .el-loading-mask {
    z-index: 2002;
  }
  .isEnd {
    float: left;
    width: 100%;
    height: 80px;
    text-align: center;
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

  @keyframes lds-facebook_1 {
    0% {
      top: 0px;
      height: 200px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  @-webkit-keyframes lds-facebook_1 {
    0% {
      top: 0px;
      height: 200px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  @keyframes lds-facebook_2 {
    0% {
      top: 20px;
      height: 160px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  @-webkit-keyframes lds-facebook_2 {
    0% {
      top: 20px;
      height: 160px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  @keyframes lds-facebook_3 {
    0% {
      top: 40px;
      height: 120px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  @-webkit-keyframes lds-facebook_3 {
    0% {
      top: 40px;
      height: 120px;
    }
    50% {
      top: 80px;
      height: 40px;
    }
    100% {
      top: 80px;
      height: 40px;
    }
  }

  .lds-facebook {
    position: relative;
  }

  .lds-facebook div {
    position: absolute;
    width: 20px;
  }

  .lds-facebook div:nth-child(1) {
    left: 40px;
    background: #1d0e0b;
    -webkit-animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
    animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
    -webkit-animation-delay: -0.2s;
    animation-delay: -0.2s;
  }

  .lds-facebook div:nth-child(2) {
    left: 90px;
    background: #774023;
    -webkit-animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
    animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
    -webkit-animation-delay: -0.1s;
    animation-delay: -0.1s;
  }

  .lds-facebook div:nth-child(3) {
    left: 140px;
    background: #d88c51;
    -webkit-animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
    animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  }

  .lds-facebook {
    width: 90px !important;
    height: 90px !important;
    -webkit-transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
    transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
  }

  .iconfont {
    font-size: 15px;
    margin-right: 2px;
  }

</style>
