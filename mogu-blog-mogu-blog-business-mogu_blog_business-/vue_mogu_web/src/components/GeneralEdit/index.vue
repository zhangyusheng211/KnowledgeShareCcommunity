<template>
  <!-- 添加或修改对话框 -->
  <el-dialog
    class="item-dialog"
    :title="title"
    fullscreen
    :visible.sync="dialogEditVisible"
    :before-close="beforeCloseEdit"
  >
    <el-form :model="form" :rules="rules" ref="form">
      <el-row>
        <el-col :span="24">
          <el-form-item label="修改原因" :label-width="formLabelWidth" prop="reason">
            <el-input v-model="form.reason" auto-complete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="修改内容" :label-width="formLabelWidth" prop="content">
<!--            <MarkdownEditor ref="editor" @contentChange="contentChange" :content="form.content"  :height="300"></MarkdownEditor>-->
            <mavon ref="editor" :context="form.content" @contentChange= "contentChange" :height="windowHeight"></mavon>
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
import MarkdownEditor from "../MarkdownEditor";
import {generalEdit} from "../../api/generalEdit";
import CKEditor from "../CKEditor";
import {getCookie} from "@/utils/cookieUtils";
const axios = require('axios');
import Mavon from "../Mavon";
export default {
  name: "index",
  props: ["visible", "generalInfo"],
  data() {
    return {
      windowHeight: document.documentElement.clientHeight - 200,
      codeStyle: "github",
      value: '',
      changeCount: 0, // 改变计数器
      title: "勘误",
      form: {
        reason: "",
        content: "",
      },
      generalEditRequest: {},
      icon: false,
      formLabelWidth: "100px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      dialogEditVisible: this.visible,
      rules: {
        reason: [
          {required: true, message: '修改原因为空', trigger: 'blur'},
          {min: 1, max: 255, message: '长度在1到255个字符'},
        ],
      }
    }
  },
  watch: {
    visible: function() {
      this.dialogEditVisible = this.visible;
    },
    generalInfo: function() {
      console.log("监听过来的资源", this.generalInfo.summary)
      this.generalEditRequest = this.generalInfo;
      setTimeout(()=>{
        this.$refs.editor.setData(this.generalInfo.content); //设置富文本内容
        // console.log("转换前的内容", this.generalInfo.content)
        // let content = this.$commonUtil.htmlToMarkdown(this.generalInfo.content)
        // console.log("转换后的内容", content)
        // this.form.content = content;

      },100)
    },
  },
  components: {
    //注册组件
    MarkdownEditor,
    CKEditor,
    Mavon,
  },
  mounted() {
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };

  },
  created() {
    this.generalEditRequest = this.generalInfo
    setTimeout(()=>{
      this.$refs.editor.setData(this.generalInfo.content); //设置富文本内容
      // console.log("转换前的内容", this.generalInfo.content)
      // let content = this.$commonUtil.htmlToMarkdown(this.generalInfo.content)
      // console.log("转换后的内容", content)
      // this.form.content = content;
    },100)
  },
  methods: {
    // 内容改变，触发监听
    contentChange: function(val) {
      let that = this;
      // that.form.content = that.$refs.editor.getData(); //获取文本中的内容
      that.form.content = val;
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
      let generalInfo = this.generalEditRequest
      if(userInfo.userName == undefined) {
        this.$notify.error({
          title: '错误',
          message: "登录后才能进行勘误哦~",
          offset: 100
        });
        return
      }

      this.subContent = this.$commonUtil.markdownToHtml(this.form.content);
      generalInfo.reason = this.form.reason
      generalInfo.content = this.subContent

      this.$refs.form.validate((valid) => {
        if(!valid) {
          console.log("校验出错")
        } else {
          //编辑问题
          generalEdit(generalInfo).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success(response.message)
              // 清空缓存
              window.localStorage.clear()
              setTimeout(()=> {
                location.reload();
              }, 500)
            } else {
              this.$alert(response.message, '警告', {
                confirmButtonText: '确定',
                callback: action => {
                  this.$message({
                    type: 'info',
                    message: `请稍后再试!`
                  });
                }
              });
              this.$commonUtil.message.error(response.message)
            }
          });
        }
      })

    },
    cancel: function () {
      this.$emit("beforeCloseEdit", "");
    },
    beforeCloseEdit(done) {
      this.isChange = false;
      this.changeCount = 0
      //取消时，开始状态
      this.$emit("beforeCloseEdit", "");
      done();
    }
  }
}
</script>

<style scoped>
.item-dialog {
  margin-top: -80px;
}
</style>
