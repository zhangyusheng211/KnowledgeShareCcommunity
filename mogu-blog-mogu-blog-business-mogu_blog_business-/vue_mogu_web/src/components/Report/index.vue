<template>
  <!-- 添加或修改对话框 -->
  <el-dialog
    :title="title"
    :width="dialogSize"
    :visible.sync="dialogFormVisible"
    :before-close="beforeClose"
  >
    <el-form :model="form" :rules="rules" ref="form">
      <el-row>
        <el-col :span="24">
          <el-form-item label="举报类型" :label-width="formLabelWidth" prop="title">
            <el-radio-group v-model="form.violationType">
              <el-radio  border style="margin: 5px;" v-for="item in commentReportDictList" :key="item.uid" :type="item.listClass" :label="item.dictLabel"></el-radio>

            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="举报详情" :label-width="formLabelWidth" prop="content">
            <el-input
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 5}"
              placeholder="请详细描述举报原因，我们将第一时间核实处理！"
              v-model="form.content">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submit">提 交</el-button>
      </span>
  </el-dialog>
</template>

<script>
import {getListByDictTypeList} from "@/api/sysDictData"
import {report} from "../../api/report";
export default {
  name: "index",
  props: ["visible", "reportInfo"],
  data() {
    return {
      title: "举报反馈",
      form: {
        violationType: "",
        content: "",
      },
      dialogSize: "35%",
      reportRequest: {},
      icon: false,
      formLabelWidth: "100px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      dialogFormVisible: this.visible,
      commentReportDictList: [],
      commentReportDictDefault: null,
      rules: {
        content: [
          {required: true, message: '举报内容为空', trigger: 'blur'},
          {min: 1, max: 255, message: '长度在1到255个字符'},
        ],
      }
    }
  },
  watch: {
    visible: function() {
      this.dialogFormVisible = this.visible;
    },
    reportInfo: function() {
      console.log("监听过来的举报资源", this.reportInfo)
      this.reportRequest = this.reportInfo;
    },
    // 判断配置
    '$store.state.app.commentReportDict': function (newFlag, oldFlag) {
      this.getDictList()
    },
  },
  mounted() {
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    this.resizeWin();
  },
  created() {
    this.reportRequest = this.reportInfo
    this.getDictList()
  },
  methods: {
    getDictList: function () {
      let commentReportDict = this.$store.state.app.commentReportDict;
      if(commentReportDict.list) {
        this.commentReportDictList = commentReportDict.list;
        this.commentReportDictDefault = commentReportDict.default;
      }
    },
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.dialogSize = "35%"
      } else if(centerWidth > 1000) {
        this.dialogSize = "50%"
      } else if(centerWidth > 600) {
        this.dialogSize = "60%"
      } else {
        this.dialogSize = "95%"
      }
    },
    submit() {
      let userInfo = this.$store.state.user.userInfo
      let userUid = this.$store.state.user.userInfo.uid
      let reportInfo = this.reportRequest
      if(userInfo.userName == undefined) {
        this.$notify.error({
          title: '错误',
          message: "登录后才能举报评论哦~",
          offset: 100
        });
        return
      }

      if(userUid == reportInfo.userUid) {
        this.$notify.error({
          title: '错误',
          message: "不能举报自己的评论哦~",
          offset: 100
        });
        return;
      }
      reportInfo.violationType = this.form.violationType
      reportInfo.content = this.form.content
      report(reportInfo).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$notify({
            title: '成功',
            message: "举报成功",
            type: 'success',
            offset: 100
          });
          this.cancel()
        } else {
          this.$notify.error({
            title: '错误',
            message: response.message,
            type: 'success',
            offset: 100
          });
        }
      });
    },
    cancel: function () {
      this.$emit("beforeClose", "");
    },
    beforeClose(done) {
      this.$emit("beforeClose", "");
      done();
    }
  }
}
</script>

<style scoped>

</style>
