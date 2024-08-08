<template>
  <!-- 添加或修改对话框 -->
  <el-dialog
    :modal-append-to-body="false"
    :title="title"
    :visible.sync="dialogFormVisible"
    :before-close="closeDialog"
    fullscreen
  >
    <el-form :model="form" :rules="rulesProblem" ref="form">

      <el-row>
        <el-col :span="16">
          <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
            <el-input v-model="form.title" auto-complete="off" @input="contentChange"></el-input>
          </el-form-item>

          <!--            <el-form-item label="简介" :label-width="formLabelWidth">-->
          <!--              <el-input v-model="form.summary" auto-complete="off" @input="contentChange"></el-input>-->
          <!--            </el-form-item>-->

        </el-col>
      </el-row>

      <el-row>
        <el-col :xl="5" :md="5">
          <el-form-item label="问题标签" :label-width="formLabelWidth" prop="problemTagUid">
            <el-select
              v-model="tagValue"
              multiple
              size="small"
              placeholder="请选择"
              style="width:210px"
              filterable
              :multiple-limit="5"
              @input="contentChange"
            >
              <el-option
                v-for="item in tagData"
                :key="item.uid"
                :label="item.name"
                :value="item.uid"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xl="5" :md="5">
          <el-form-item label="问题类型" :label-width="formLabelWidth" prop="problemType">
            <el-select
              v-model="form.problemType"
              size="small"
              placeholder="请选择"
              style="width:160px"
              filterable
              @input="contentChange"
            >
              <el-option
                v-for="item in problemTypeDictList"
                :key="item.uid"
                :label="item.dictLabel"
                :value="parseInt(item.dictValue)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xl="5" :md="5">
          <el-form-item label="问题难度" :label-width="formLabelWidth" prop="problemDifficulty">
            <el-select
              v-model="form.problemDifficulty"
              size="small"
              placeholder="请选择"
              style="width:160px"
              filterable
              @input="contentChange"
            >
              <el-option
                v-for="item in problemDifficultyDictList"
                :key="item.uid"
                :label="item.dictLabel"
                :value="parseInt(item.dictValue)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :xl="5" :lg="7" :md="8">
          <el-form-item label="问题评论" :label-width="formLabelWidth" prop="openComment">
            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openComment" :label="item.dictValue"
                      border size="small">{{ item.dictLabel }}
            </el-radio>
          </el-form-item>
        </el-col>

        <el-col :xl="5" :lg="7" :md="8">
          <el-form-item label="精选" :label-width="formLabelWidth" prop="isSelection">
            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.isSelection" :label="item.dictValue"
                      border size="small">{{ item.dictLabel }}
            </el-radio>
          </el-form-item>
        </el-col>

        <el-col :xl="5" :lg="7" :md="8">
          <el-form-item label="是否发布" :label-width="lineLabelWidth" prop="isPublish">
            <el-radio-group v-model="form.isPublish" size="small">
              <el-radio v-for="item in blogPublishDictList" :key="item.uid" :label="item.dictValue" border>
                {{ item.dictLabel }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
        <mavon v-if="editorModel === '1'" ref="editor" :context="form.content" @contentChange="contentChange"
               :height="windowHeight"></mavon>
        <CKEditor v-else ref="editor" :content="form.content" @contentChange="contentChange" :height="windowHeight-100"></CKEditor>
      </el-form-item>

      <el-form-item label="解析" :label-width="formLabelWidth" prop="answer">
        <mavon v-if="editorModel === '1'" ref="editor2" :context="form.answer" @contentChange="contentChange"
               :height="windowHeight"></mavon>
        <CKEditor2 v-else ref="editor2" :content="form.answer" @contentChange="contentChange" :height="windowHeight-100"></CKEditor2>
      </el-form-item>

      <el-form-item style="float: right; margin-right: 20px;">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitProblemForm()">提交审核</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>

import CKEditor from "../CKEditor";
import CKEditor2 from "../CKEditor2";
import {addProblem, getProblemTagList} from "@/api/problem";
import {getListByDictTypeList} from "@/api/sysDictData"
import Mavon from "../Mavon";

export default {
  name: "CreateProblem",
  props: ["visible", "isEdit", "formData", "editorModel"],
  components: {
    CKEditor,
    CKEditor2,
    Mavon
  },
  data() {
    return {
      changeCount: 0, // 改变计数器
      tagValue: [], //保存选中标签id(编辑时)
      openDictList: [], // 是否启动字典
      problemDifficultyDictList: [], // 问题难度字典
      openDefault: null, // 是否开启评论默认值
      selectionDefault: null, // 是否精选默认值
      blogPublishDefault: null, //问题发布默认值
      title: "增加问题",
      isChange: false, // 表单内容是否改变
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      tagData: [], //标签数据
      showCreateProblem: true,
      blogOriginalDictList: [], //是否原创字典
      blogPublishDictList: [], //是否发布字典
      yesNoDictList: [], // 是否 字典列表
      auditStatusDictList: [], // 审批状态字典
      problemTypeDictList: [], // 问题类型
      dialogFormVisible: this.visible, //控制弹出框
      form: {},
      showEditor: false,
      rulesProblem: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'}
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
        ],
        isOriginal: [
          {required: true, message: '原创字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'},
        ],
        openComment: [
          {required: true, message: '网站评论不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '网站评论只能为自然数'},
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
        answer: [
          {required: true, message: '解析不能为空', trigger: 'blur'}
        ],
        problemTagUid: [
          {required: true, message: '标签不能为空', trigger: 'blur'}
        ],
      }
    }
  },
  watch: {
    visible: function () {
      this.dialogFormVisible = this.visible;
    },
  },
  computed: {
    windowHeight() {
      return (window.innerHeight - 400) / 2;
    }
  },
  destroyed() {
    console.log("销毁实例")
  },
  mounted() {
    this.showEditor = true
  },
  created() {
    this.showEditor = true
    this.tagList()
    this.getDictList()
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_yes_no', 'sys_user_sex', 'sys_feedback_status', 'sys_editor_modal', 'sys_content_report_type', 'sys_original_status', 'sys_publish_status', 'sys_audit_status', 'sys_problem_type', 'sys_problem_difficulty', 'sys_normal_disable', 'sys_selection', 'sys_publish_status']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;

          this.blogPublishDictList = dictMap.sys_publish_status.list
          this.auditStatusDictList = dictMap.sys_audit_status.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.openDictList = dictMap.sys_normal_disable.list

          if (dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if (dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }
          if (dictMap.sys_normal_disable.defaultValue) {
            this.openDefault = dictMap.sys_normal_disable.defaultValue;
          }
          if (dictMap.sys_selection.defaultValue) {
            this.selectionDefault = dictMap.sys_selection.defaultValue;
          }
          if (dictMap.sys_publish_status.defaultValue) {
            this.blogPublishDefault = dictMap.sys_publish_status.defaultValue;
          }

          if (this.isEdit) {
            this.form = this.formData
          } else {
            this.form = this.getFormObject()
            this.$refs.editor.initData()
            this.$refs.editor2.initData()
            console.log("获取配置", this.getFormObject())
          }
        }
      });
    },

    // 关闭窗口
    closeDialog(done) {
      if (this.isChange) {
        this.$confirm("是否关闭问题编辑窗口", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            this.isChange = false;
            this.changeCount = 0
            this.$emit("beforeClose", "");
            this.showEditor = false
            done();
          })
          .catch(() => {
            this.$commonUtil.message.info("已取消")
          });
      } else {
        this.isChange = false;
        this.changeCount = 0
        this.$emit("beforeClose", "");
        this.showEditor = false
        done();
      }
    },
    // 内容改变，触发监听
    contentChange: function () {
      this.isChange = true;
      this.changeCount = this.changeCount + 1;
    },
    tagList: function () {
      let tagParams = {};
      tagParams.pageSize = 200;
      tagParams.currentPage = 1;
      tagParams.tagLevel = 2
      getProblemTagList(tagParams).then(response => {
        this.tagData = response.data.records;
        this.tagOptions = response.data.records;
      });
    },
    submitProblemForm: function () {
      console.log('>>>>>>>>>>>>>>>>>>>>>>>>>' + this.$refs.editor.getData())
      console.log('>>>>>>>>>>>>>>>>>>>>>>>>>' + this.$refs.editor2.getData())

      this.form.content = this.$refs.editor.getData(); //获取内容
      this.form.answer = this.$refs.editor2.getData(); //获取解析
      this.form.problemTagUid = this.tagValue.join(",");

      // debugger
      this.$refs.form.validate((valid) => {
        if (!valid) {

        } else {
          addProblem(this.form).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success(response.message)
              this.dialogFormVisible = false;
              setTimeout(() => {
                location.reload();
              }, 500)
            } else {
              this.$commonUtil.message.error(response.message)
            }
          });
        }
      })
    },
    getFormObject: function () {
      let formObject = {
        uid: null,
        title: null,
        summary: null,
        content: null,
        answer: null,
        fileUid: null,
        isPublish: this.blogPublishDefault, //是否发布
        type: this.blogTypeDefault, // 文章类型
        author: null, //作者
        openComment: this.openDefault, // 是否启动
        problemTagUid: null,
        problemType: parseInt(this.problemTypeDefault),
        problemDifficulty: parseInt(this.problemDifficultyDefault),
        isSelection: this.selectionDefault,
      };
      return formObject;
    },
    // 关闭时的回调
    beforeClose(done) {
      //取消时，开始状态
      this.dialogFormVisible = false
      this.$emit("beforeClose", "");
      done();
    },
    cancel: function () {
      this.dialogFormVisible = false
      this.$emit("beforeClose", "");
    },
  }
}
</script>

<style scoped>

</style>
