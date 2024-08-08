<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">专栏</a>
      </h1>

      <!--此专栏页面已经废弃，没有使用了-->
      <div class="share">
        <ul>
          <li v-for="item in dataList" :key="item.uid" >
            <div class="shareli" @click="showSubjectItemList(item)" >
              <a href="javascript:void(0);">
                <i>
                  <img v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]" class="resImg" >
                  <el-image class="resImg" v-else>
                    <div slot="error" class="image-slot">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                </i>
                <h2>
                  <b>{{item.summary}}</b>
                </h2>
                <span>{{item.subjectName}}</span>
              </a>
            </div>
          </li>
        </ul>
      </div>

      <!--分页-->
      <div class="block" style="text-align: center;">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <SubjectItemList :visiable="visiable" @close="closeSubjectItemList" :subject="selectSubject"></SubjectItemList>

  </div>
</template>

<script>
import {getSubjectList} from "../api/subject";
import SubjectItemList from "../components/SubjectItemList";
import {recorderBehavior} from "../api";
export default {
  name: "share",
  data() {
    return {
      dataList: [],
      currentPage: 1,
      pageSize: 16,
      total: 0, //总数量
      visiable: false, //是否显示专辑item侧边栏
      selectSubject: {}, // 选中的Subject
      subjectUid: "", //选中的SubjectUid
    };
  },
  components: {
    //注册组件
    SubjectItemList
  },
  created() {
    this.subjectUid = this.$route.query.uid;
    this.subjectList()

    setTimeout(()=>{
      recorderBehavior({"otherData": "专栏", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)
  },
  methods: {
    subjectList() {
      let params = {};
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      getSubjectList(params).then(response => {
        console.log("得到的结果", response)
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
    //改变页码
    handleCurrentChange(val) {
      this.currentPage = val; //改变当前所指向的页数
      this.subjectList();
    },
    showSubjectItemList(selectSubject) {
      this.$router.push({
        path: "/subject",
        query: { uid: selectSubject.uid }
      });
      this.selectSubject = selectSubject
      this.visiable = !this.visiable
    },
    closeSubjectItemList(selectSubject) {
      this.visiable = !this.visiable
    }
  }
};
</script>

<style scoped>
.share {
  min-height: 785px;
}
.resImg {
  width: 100%;
  height: 160px;
  vertical-align: middle;
}
.el-drawer {
  min-width: 400px;
}
</style>
