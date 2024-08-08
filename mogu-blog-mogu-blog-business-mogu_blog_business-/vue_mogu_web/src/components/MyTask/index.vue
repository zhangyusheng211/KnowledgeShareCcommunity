<template>
  <div v-if="isLogin && splitList.length > 0">
    <el-card :body-style="{ padding: '10px' }" class="cardStyle">
      <div slot="header" class="clearfix">
        <span style="font-weight: 600; font-size: 18px;">任务广场</span>
      </div>

      <el-carousel indicator-position="outside" arrow="never" :autoplay="true" height="210px" :interval="4000">
        <el-carousel-item v-for="(taskList, index) in splitList" :key="index">
          <div v-for="task in taskList" :key="task.uid" class="text item">
            <el-row style="justify-items: center">
              <el-col :span="3" style="line-height: 40px; text-align: center">
                <i :class="task.icon" style="font-size: 20px"></i>
              </el-col>
              <el-col :span="16">
                <div
                  style="height: 20px; font-size: 16px;font-weight: 600; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                  {{ task.name }}
                </div>
                <div
                  style="height: 20px; font-size: 13px;color: #72777b; margin-top: 5px; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; ">
                  {{ task.description }}
                </div>
              </el-col>
              <el-col :span="5" style="margin-top: 10px;">

                <el-button v-if="task.button && task.button.text && task.state == 'WAIT'" style="float: right; padding: 3px 0"
                           type="text" @click="click(task, task.button.action)"> {{ task.button.text }}
                </el-button>

                <el-button v-else-if="task.state == 'WAIT'" style="float: right; padding: 3px 0" type="text">
                  待完成
                </el-button>
                <el-button v-else-if="task.state == 'FINISH'" style="float: right; padding: 3px 0" type="text">
                  已完成
                </el-button>
              </el-col>
            </el-row>
            <el-divider></el-divider>
          </div>
        </el-carousel-item>


      </el-carousel>


    </el-card>
  </div>
</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData";
import {mapMutations} from "vuex";
import countTo from 'vue-count-to';
import {getMyTaskList, taskClick} from "../../api/task";

export default {
  name: "MyTask",
  props: [],
  data() {
    return {
      userLevelDictList: [],
      userTagDictList: [],
      userInfo: {},
      isLogin: false,
      myTaskList: [],
      splitList: [],
      taskHandlerMap: {
        "CLICK": "CREATE_BLOG",
        "BLOG": "CREATE_BLOG",
        "QUESTION": "CREATE_QUESTION",
        "PROBLEM": "CREATE_PROBLEM",
      },
    };
  },
  components: {
    countTo
  },
  created() {
    this.isLogin = this.$store.state.user.isLogin
    this.getDictList()
    this.getMyTaskListMethod()
  },
  mounted() {

  },
  watch: {
    // 判断用户信息
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.getMyTaskListMethod()
    },
  },
  methods: {
    ...mapMutations(["setLoginMessage", "setDomainEventMessage"]),
    getMyTaskListMethod() {
      this.isLogin = this.$store.state.user.isLogin
      if (!this.isLogin) {
        return
      }
      // 只展示任务相关的
      let params = {}
      params.marketName = "task"
      getMyTaskList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          // this.myTaskList = response.data
          this.splitArray(response.data, 3)
        }
      })
    },
    // array需要拆分的数组
    splitArray(array, size) {
      let data = [];
      for (let i = 0; i < array.length; i += size) {
        data.push(array.slice(i, i + size))
      }
      this.splitList = data
    },
    click(task) {
      switch (task.button.action) {
        case "CLICK": {
          let params = {}
          params.id = task.uid
          params.key = "task"
          taskClick(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.getMyTaskListMethod()
              this.$message.success(response.message)
            }
          })
        } break
        default: {
          // 其它的动作，直接打开操作页
          let event = {
            "type": "task",
            "action":  task.button.action,
            "message": task.button.message,
            "router":  task.button.router,
            "time": new Date(),
          }
          this.setDomainEventMessage(event)
          console.log("发送领域事件", task)
        }
      }
    },
    login() {
      if (this.isLogin) {
        this.$message.success("用户已登录")
        return
      }
      this.setLoginMessage(Math.random())
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_level', 'sys_user_tag']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userLevelDictList = dictMap.sys_user_level.list
          this.userTagDictList = dictMap.sys_user_tag.list
        }
      });
    },

    // 跳转到个人中心页
    getUserCenter: function (type) {
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: this.userData.uid, type: type}
      });
      window.open(routeData.href, '_blank');
    },

  }
};
</script>

<style scoped>
.cardStyle {
  min-height: 210px;
  margin-bottom: 10px;
}

.loginText {
  margin-top: 15px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0px;
  line-height: 0px;
  color: rgba(0, 0, 0, 1);
}

.loginSummary {
  margin-top: 30px;
  font-size: 16px;
  font-weight: 500;
  color: rgba(51, 51, 51, 1);
}

.countStyle {
  color: #000;
  font-weight: bold;
}

.countStyle2 {
  color: #000;
}

/deep/ .el-progress-bar__innerText {
  color: #f4f4f5;
}

/deep/ .el-progress-bar__outer {
  background: #d8d8d8;
}

/deep/ .el-divider--horizontal {
  margin: 12px 0;
}
</style>
