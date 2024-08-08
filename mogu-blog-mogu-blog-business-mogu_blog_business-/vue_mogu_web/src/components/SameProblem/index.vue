<template>
  <div class="sidebar2">
    <div class="sidebarDiv" style="border-radius: 3px; padding-bottom: 5px; padding-top: 5px;">
      <h4 class="hometitle" style="padding-bottom: 10px; margin-bottom: 4px; margin-left: 5px;">相似题目</h4>
      <ul>
        <li style="padding: 5px; margin-top: 2px; border-bottom: 1px solid #eeeeee"  v-for="(item, index) in sameProblemList" :key="item.uid">
          <el-row  :span="24">
            <el-col :xs="24" :sm="24">
              <div>
                <a :href=" VUE_MOGU_WEB + '/cInfo/' + item.oid +'?title=面经'" target="_blank">{{item.title}}</a>
              </div>
            </el-col>
          </el-row>

          <el-row style="margin-top: 3px">
            <div>

              <el-tag  class="pointer" @click="getToProblem(item, '1')" size="mini" v-if="item.isSelection == '1'" effect="dark" type="warning">精选</el-tag>
              <el-tag  class="pointer" @click="getToProblem(item, '2')" size="mini" effect="plain" v-for="problemType in problemTypeDictList" :key="problemType.uid" v-if="item.problemType == problemType.dictValue" :type="problemType.listClass">{{problemType.dictLabel}}</el-tag>
              <el-tag  class="pointer" @click="getToProblem(item, '3')" size="mini" effect="plain" v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" v-if="item.problemDifficulty == problemDifficulty.dictValue" :type="problemDifficulty.listClass">{{problemDifficulty.dictLabel}}</el-tag>

              <span v-for="(problemTag, index) in item.problemTagList">
                <el-tag class="pointer" @click="getToProblem(problemTag.uid, '4')" size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==0" type="primary">{{problemTag.name}}</el-tag>
                <el-tag class="pointer" @click="getToProblem(problemTag.uid, '4')" size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==1" type="danger">{{problemTag.name}}</el-tag>
                <el-tag class="pointer" @click="getToProblem(problemTag.uid, '4')" size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==2" type="info">{{problemTag.name}}</el-tag>
              </span>
            </div>
          </el-row>

          <el-row>
            <div class="bloginfo" style="margin-top: 3px">
              <ul>
                <li style=" padding-right: 6px" @click="getUserCenter(item)">
                  <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                    <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium" v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                    <el-avatar size="small" v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                    <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="item.user.userTag > 0">v</span>
                  </span>
                </li>
                <li class="author"  style="margin-top: 9px; margin-left: 3px;" @click="getUserCenter(item)">
                  <span class="pointer" :class="'lv'+ item.user.userLevel">{{item.user.nickName}}</span>
                </li>

                <li class="view" style="margin-top: 8px">
                  <span class="iconfont">&#xe8c7;</span>
                  <span>{{item.clickCount}}</span>
                </li>

                <li class="createTime" style="margin-top: 8px">
                  <span class="iconfont">&#xe606;</span>
                  {{timeAgo(item.createTime)}}
                </li>
              </ul>
            </div>
          </el-row>
        </li>

      </ul>
    </div>
  </div>

</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData"
import {timeAgo} from "../../utils/webUtils";
import Collection from "../Collection";
import Praise from "../Praise";

export default {
  name: "SameProblem",
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      problemTypeDictList: [], // 问题类型字典
      problemDifficultyDictList: [], // 问题难度字典
    };
  },
  props: ['sameProblemList'],
  components: {

  },
  created() {
    this.getDictList()
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_problem_difficulty', 'sys_problem_type']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          if(dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if(dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }
        }
      });
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
    getToProblem(problem, type) {
      switch (type) {
        case '1': {

          this.$router.push({
            path: "/code",
            query: {isSelection: problem.isSelection}
          });
        } break;
        case '2': {

          this.$router.push({
            path: "/code",
            query: {problemType: problem.problemType}
          });
        } break;
        case '3': {

          this.$router.push({
            path: "/code",
            query: {problemDifficulty: problem.problemDifficulty}
          });
        } break;
        case '4': {

          this.$router.push({
            path: "/code",
            query: {tagUid: problem}
          });

        } break;
      }
    },
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
  }
};
</script>

<style scoped>
.questionContent .index {
  /*font-size: 16px;*/
}
.questionContent li{
  margin-bottom: 4px;
  border-bottom: 1px solid rgba(0,0,0,.0625);
}
.questionContent li :hover {

}

@media screen and (max-width: 1200px) {
  .sidebar2 {
    width: 100%
  }
}
</style>
